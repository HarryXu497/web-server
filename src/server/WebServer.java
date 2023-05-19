package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    private static boolean running = true;
    private static boolean accepting = true;
    private ServerSocket socket;

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

    static class ConnectionHandler implements Runnable {
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
                        rawRequest.add(inputLine);

                        if (inputLine.length() == 0) {
                            break;
                        }
                    }

                    inputLine = this.input.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

//            try {
//                System.out.println("body: ");
//                System.out.println(this.input.ready());
//
//                if (this.input.ready()) {
//                    String bodyLine = Character.toString(this.input.read());
//
//                    System.out.println("body: " + bodyLine);
//
//                    while ((this.input.ready()) && (!bodyLine.equals(""))) {
//                        this.output.write(bodyLine + "\n");
//                        bodyLine = Character.toString(this.input.read());
//                        System.out.print(bodyLine);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }


            Request req = Request.parse(rawRequest);

            System.out.println(req);

            System.out.println(req.getBody());

        }
    }
}
