package server;

import server.handler.HandlerException;
import server.handler.Handlers;
import server.handler.routes.HomeRoute;
import server.request.Request;
import server.response.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    private static boolean running = true;
    private static boolean accepting = true;
    private ServerSocket socket;
    private Handlers requestHandlers;

    public WebServer() {
        this.requestHandlers = new Handlers();

        this.requestHandlers.register("/", new HomeRoute());
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
//                accepting = false;
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    class ConnectionHandler implements Runnable {
        private final BufferedWriter output;
        private final BufferedReader input;
        private final Socket client;
        private final boolean isRunning;

        public ConnectionHandler(Socket clientSocket) throws IOException {
            this.client = clientSocket;

            // Socket streams
            this.output = new BufferedWriter(new PrintWriter(client.getOutputStream()));
            InputStreamReader inStream = new InputStreamReader(client.getInputStream());
            this.input = new BufferedReader(inStream);

            this.isRunning = true;
        }

        @Override
        public void run() {
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

            Response res;

            try {
                res = requestHandlers.dispatch(req);
                System.out.println(res);
                this.output.write(res.toString());
            } catch (HandlerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error sending response to client.");
                e.printStackTrace();
            }

            try {
                this.input.close();
                this.output.close();
                this.client.close();
            }catch (Exception e) {
                System.out.println("Failed to close socket");
            }
        }
    }
}
