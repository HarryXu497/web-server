package template;

public class Directive {
    private final DirectiveType type;
    private final int startNumber;
    private final int endNumber;

    private final String[] tokens;

    public Directive(DirectiveType type, int startNumber, int endNumber, String[] tokens) {
        this.type = type;
        this.endNumber = endNumber;
        this.startNumber = startNumber;
        this.tokens = tokens;
    }

    public DirectiveType getType() {
        return this.type;
    }

    public int getEndNumber() {
        return this.endNumber;
    }

    public int getStartNumber() {
        return startNumber;
    }

    public String[] getTokens() {
        return this.tokens;
    }
}
