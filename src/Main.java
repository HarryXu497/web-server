import server.WebServer;

public class Main {
    public static void main(String[] args) {
        WebServer server = new WebServer();

        server.serve();
    }
}