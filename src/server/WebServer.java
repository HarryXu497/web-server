package server;

import assets.AssetEngine;
import server.handler.Handler;
import server.handler.HandlerException;
import server.handler.Handlers;
import server.handler.NotFoundException;
import server.handler.routes.FileHandler;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;
import template.TemplateNotFoundException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A multithreaded web server which parses HTTP requests, dispatches route handlers, and stringifies HTTP responses to the client.
 * This server uses sockets and server sockets to communicate HTTP requests and responses over TCP/IP.
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public class WebServer {
    /** The request handlers of the server */
    private final Handlers requestHandlers;

    /** The template engine of the server */
    private final TemplateEngine templateEngine;

    /** Logs requests to the console */
    private final Consumer<Request> requestLogger;

    /**
     * Constructs a web server with a templating engine and the directory of styles
     * @param templateEngine the templating engine used to compile .th files to html
     * @param assets the engine containing static assets such as styles, scripts, and images
     * @param routes a {@link LinkedHashMap} of URL patterns to handlers
     * @param assetMap maps the assets in a directory to a URL on which to host them
     * @param notFoundRoute the handler to handler the request if no other matching handler can be found
     * @param requestLogger the logging function that will be called with the request
     */
    public WebServer(TemplateEngine templateEngine, AssetEngine assets, LinkedHashMap<String, Handler> routes, Map<String, String> assetMap, Handler notFoundRoute, Consumer<Request> requestLogger) {
        this.requestHandlers = new Handlers();

        this.templateEngine = templateEngine;

        // Maps each file in each directory of assets to a corresponding route
        for (Map.Entry<String, String> assetPair : assetMap.entrySet()) {
            File folder = new File(assetPair.getKey());
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    this.requestHandlers.register(assetPair.getValue() + file.getName(), new FileHandler(assets, assetPair.getKey()));
                }
            }
        }

        // Registers all routes
        for (Map.Entry<String, Handler> route : routes.entrySet()) {
            String url = route.getKey();
            Handler handler = route.getValue();

            this.requestHandlers.register(url, handler);
        }

        if (notFoundRoute != null) {
            this.requestHandlers.registerNotFoundHandler(notFoundRoute);
        }

        this.requestLogger = requestLogger;
    }

    /**
     * serve
     * Serves the server at the specified port
     * @param port the port to serve on
     * @param onOpen a consumer that receives the port as its argument
     */
    public void serve(int port, Consumer<Integer> onOpen) {
        // Open the server socket
        try (ServerSocket socket = new ServerSocket(port)) {
            // Server loop
            // Accept client connections and delegate each connection to a separate thread
            try {
                // Output message when server is up
                if (onOpen != null) {
                    onOpen.accept(port);
                }

                while (true) {
                    Socket client = socket.accept();

                    Thread t = new Thread(new ConnectionHandler(client));
                    t.start();
                }
            } catch (IOException e) {
                System.out.println("Error when waiting for connection");
            }
        } catch (IOException e) {
            System.out.println("Error opening server socket");
        }
    }

    /**
     * serve
     * Serves the server at the specified port
     * @param port the port to serve on
     */
    public void serve(int port) {
        this.serve(port, null);
    }

    /**
     * A runnable responsible for handling each request made to the server
     * @author Harry Xu
     * @version 1.0 - May 20th 2023
     */
    class ConnectionHandler implements Runnable {
        /** The socket of the client to read from and write to */
        private final Socket client;

        /** The output stream of the socket */
        private final OutputStream output;

        /** The input stream of the socket wrapped by a {@link BufferedReader} */
        private final BufferedReader input;

        /**
         * Constructs a connection handler with a client socket to read to and write from
         * @param clientSocket the socket to communicate with
         * @throws IOException if an exception occurs while working with the input or output streams of the client
         * */
        public ConnectionHandler(Socket clientSocket) throws IOException {
            this.client = clientSocket;

            // Socket streams
            this.output = client.getOutputStream();
            InputStreamReader inStream = new InputStreamReader(client.getInputStream());
            this.input = new BufferedReader(inStream);
        }

        /**
         * close
         * properly closes the socket and its input and output streams
         */
        public void close() {
            try {
                this.client.shutdownInput();
                this.client.shutdownOutput();
                this.input.close();
                this.output.close();
                this.client.close();
            } catch (Exception e) {
                System.out.println("Failed to close socket");
                e.printStackTrace();
            }
        }

        /**
         * handleRequest
         * Handles parsing of the request, dispatch of the request handler, and response creating and sending
         */
        public void handleRequest() {
            // End execution if input stream is not read
            try {
                if (!this.input.ready()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            List<String> rawRequest = new ArrayList<>();

            // Read HTTP request into a list of strings
            try {
                String inputLine = this.input.readLine();

                while ((inputLine != null) && (inputLine.length() != 0)) {
                    rawRequest.add(inputLine);

                    inputLine = this.input.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // Read request body
            StringBuilder payload = new StringBuilder();

            try {
                while (this.input.ready()) {
                    payload.append((char) this.input.read());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // Append request body to the request list
            rawRequest.add(payload.toString());

            // Parse request into Request object
            Request req = Request.parse(rawRequest);

            synchronized (requestLogger) {
                requestLogger.accept(req);
            }

            // Dispatch the correct handler
            try {
                Response res = requestHandlers.dispatch(req);
                this.output.write(res.toBytes());
            } catch (HandlerException | NotFoundException e) {
                // No 404 handler
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
                // Handler cannot load template

                System.out.println("Error loading template");

                // Load server error page
                try {
                    Response serverError = new Response(
                            new Response.StatusLine(ResponseCode.INTERNAL_SERVER_ERROR),
                            new HashMap<>(),
                            templateEngine.getTemplate("frontend/templates/error.th")
                    );

                    this.output.write(serverError.toBytes());
                } catch (IOException | TemplateNotFoundException ex) {
                    ex.printStackTrace();
                    System.out.println("Cannot render 500 page");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error writing response to client");
            }
        }

        /**
         * run
         * starts execution of the thread separately from the main thread
         */
        @Override
        public void run() {
            this.handleRequest();
            this.close();
        }
    }
}
