package server;

import server.handler.Handler;
import server.handler.HandlerException;
import server.handler.Handlers;
import server.handler.routes.FileHandler;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;
import template.TemplateNotFoundException;
import template.TemplateSyntaxException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A multithreaded web server which parses HTTP requests, dispatches route handlers, and stringifies HTTP responses to the client.
 * This server uses sockets and server sockets to communicate HTTP requests and responses over TCP/IP.
 * @author Harry Xu
 * @version 1.0 - May 20th 2023
 */
public class WebServer {
    /** determines if the server is currently running */
    private static boolean running = true;

    /** determines if the server is currently accepting requests */
    private static boolean accepting = true;

    /** The request handlers of the server */
    private final Handlers requestHandlers;

    /** The template engine of the server */
    private final TemplateEngine engine;

    /**
     * constructs a web server with a templating engine and the directory of styles
     * @param engine the templating engine used to compile .th files to html
     * @param assetMap maps the assets in a directory to a URL on which to host them
     * */
    public WebServer(TemplateEngine engine, LinkedHashMap<String, Handler> routes, Map<String, String> assetMap) {
        this.requestHandlers = new Handlers();

        this.engine = engine;

        // Maps each file in each directory of assets to a corresponding route
        for (Map.Entry<String, String> assetPair : assetMap.entrySet()) {
            File folder = new File(assetPair.getKey());
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    this.requestHandlers.register(assetPair.getValue() + file.getName(), new FileHandler(engine, assetPair.getKey()));
                }
            }
        }

        // Registers all routes
        for (Map.Entry<String, Handler> route : routes.entrySet()) {
            String url = route.getKey();
            Handler handler = route.getValue();

            this.requestHandlers.register(url, handler);
        }
    }

    /**
     * serve
     * Serves the server at the specified port
     * @param port the port to serve on
     */
    public void serve(int port) {
        // open the server socket
        try (ServerSocket socket = new ServerSocket(port)) {
            // Server loop
            // Accept client connections and delegate each connection to a separate thread
            try {
                while (accepting) {
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
         * constructs a connection handler with a client socket to read to and write from
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
         * handles parsing of the request, dispatch of the request handler, and response creating and sending
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

            // Dispatch the correct handler
            try {
                Response res = requestHandlers.dispatch(req);
                this.output.write(res.toString().getBytes(StandardCharsets.UTF_8));
            } catch (HandlerException e) {
                e.printStackTrace();
                // No handler/inappropriate handler
                // Load error page
                try {
                    Response notFound = new Response(
                            new Response.StatusLine(ResponseCode.NOT_FOUND),
                            new HashMap<>(),
                            engine.getTemplate("frontend/templates/not-found.th")
                    );

                    this.output.write(notFound.toString().getBytes(StandardCharsets.UTF_8));
                } catch (IOException | TemplateNotFoundException ex) {
                    ex.printStackTrace();
                    System.out.println("Cannot render 404 page");
                }

            } catch (TemplateSyntaxException | TemplateNotFoundException e) {
                e.printStackTrace();
                // Handler cannot load template

                System.out.println("Error loading template");

                // Load server error page
                try {
                    Response serverError = new Response(
                            new Response.StatusLine(ResponseCode.INTERNAL_SERVER_ERROR),
                            new HashMap<>(),
                            engine.getTemplate("frontend/templates/error.th")
                    );

                    this.output.write(serverError.toString().getBytes(StandardCharsets.UTF_8));
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
