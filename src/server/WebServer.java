package server;

import server.handler.Handlers;
import server.handler.routes.FileHandler;
import server.handler.routes.HomeRoute;
import server.request.Request;
import server.response.Response;
import server.response.ResponseCode;
import template.TemplateEngine;
import template.TemplateNotFoundException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WebServer {
    private static boolean running = true;
    private static boolean accepting = true;
    private ServerSocket socket;
    private final Handlers requestHandlers;
    private final TemplateEngine engine;


    public WebServer(TemplateEngine engine, String stylesDirectory) {
        this.requestHandlers = new Handlers();

        this.engine = engine;

        File folder = new File("frontend/styles");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                System.out.println(file.getPath());
                this.requestHandlers.register("/styles/" + file.getName(), new FileHandler(engine, stylesDirectory));
            }
        }


        this.requestHandlers.register("/", new HomeRoute(engine));
        this.requestHandlers.register("/:id", new HomeRoute(engine));
    }

    public void serve() {
        Socket client;

        try {
            this.socket = new ServerSocket(5000);
        } catch (IOException e) {
            System.out.println("Error accepting connection");
        }

        try {
            while (accepting) {
                client = this.socket.accept();
                System.out.println("Client connected");

                Thread t = new Thread(new ConnectionHandler(client));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("Error");
        }

        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Error closing server socket");
        }
    }

    class ConnectionHandler implements Runnable {
        private final OutputStream output;
        private final BufferedReader input;
        private final Socket client;
        private final boolean isRunning;

        public ConnectionHandler(Socket clientSocket) throws IOException {
            this.client = clientSocket;

            // Socket streams
            this.output = client.getOutputStream();
            InputStreamReader inStream = new InputStreamReader(client.getInputStream());
            this.input = new BufferedReader(inStream);

            this.isRunning = true;
        }

        /**
         * close
         * properly closes the socket
         */
        public void close() {
            try {
                this.client.shutdownInput();
                this.client.shutdownOutput();
                this.input.close();
                this.output.close();
                System.out.println("Closing Client");
                this.client.close();
            }catch (Exception e) {
                System.out.println("Failed to close socket");
                e.printStackTrace();
            }
        }

        /**
         * handleRequest
         * handles parsing of the request, dispatch of the request handler, and response creating and sending
         */
        public void handleRequest() {
            List<String> rawRequest = new ArrayList<>();

            try {
                if (!this.input.ready()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            try {
                String inputLine = this.input.readLine();


                while (true) {
                    System.out.println(inputLine);
                    if (inputLine != null) {
                        if (inputLine.length() == 0) {
                            break;
                        }

                        rawRequest.add(inputLine);
                    }

                    inputLine = this.input.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }


            StringBuilder payload = new StringBuilder();

            try {
                while (this.input.ready()) {
                    payload.append((char) this.input.read());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            rawRequest.add(payload.toString());

            Request req = Request.parse(rawRequest);

            try {
                Response res = requestHandlers.dispatch(req);
                this.output.write(res.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();

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
                    System.out.println("Cannot render 404 page.");
                }
            }
        }

        @Override
        public void run() {
            this.handleRequest();
            this.close();
        }
    }
}
