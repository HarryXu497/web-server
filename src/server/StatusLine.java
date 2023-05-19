package server;

public class StatusLine {
    private final RequestMethod method;
    private final String URL;
    private final String status;

    public StatusLine(RequestMethod method, String URL, String status) {
        this.method = method;
        this.URL = URL;
        this.status = status;
    }

    public RequestMethod getMethod() {
        return this.method;
    }

    public String getURL() {
        return this.URL;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "StatusLine{" +
                "method=" + this.method +
                ", URL='" + this.URL + '\'' +
                ", status='" + this.status + '\'' +
                '}';
    }
}
