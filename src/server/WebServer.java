package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private static boolean running = true;
    private static boolean accepting = true;
    private ServerSocket socket;

    public void serve() {
        Socket client = null;

        try {
            this.socket = new ServerSocket(5000);
        } catch (IOException e) {
            System.out.println("Error accepting connection");
        }

        try {
            while (accepting) {
                client = this.socket.accept();
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}
