package template;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TemplateEngine {

    private final Map<String, String> templates;

    /**
     * Constructs a template engine with predefined templates registered
     * @param paths the templates to be registered
     * @throws IOException if an error occurs while working with the files
     */
    public TemplateEngine(String... paths) throws IOException {
        this.templates = new HashMap<>();

        for (String path : paths) {
            File dir = new File(path);

            if (dir.isDirectory()) {
                File[] files = dir.listFiles();

                if (files != null) {
                    for (File file : files) {
                        String filename = file.getPath();

                        this.templates.put(filename.replace("\\", "/"), this.read(filename));
                    }
                }
            }

            if (dir.isFile()) {
                this.templates.put(path, this.read(path));
            }
        }
    }

    /**
     * getTemplate
     * get the raw un-parsed template from the registry
     * This may be necessary to implement 404 and other error pages
     * */
    public String getTemplate(String path) throws TemplateNotFoundException {
        if (!this.templates.containsKey(path)) {
            throw new TemplateNotFoundException("Template " + path + " cannot be found");
        }


        return this.templates.get(path);
    }

    public <T> String compile(String inputFile, T data) throws TemplateSyntaxException, NoSuchFieldException, IllegalAccessException, TemplateNotFoundException {
        if (!this.templates.containsKey(inputFile)) {
            throw new TemplateNotFoundException("Template " + inputFile + " cannot be found");
        }

        return parse(this.templates.get(inputFile), data);
    }

    private String read(String inputFile) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(inputFile));

        return String.join("\n", lines);
    }

    private void write(String output, String outputFile) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write(output);
        }
    }

    /**
     * parse
     * reads and parses a .th template file into an HTML string.
     * The template wll be given access to a top level data object
     * @param input the input template string
     * @param data the data object
     * @return the parsed and evaluated HTML string
     */
    private <T> String parse(String input, T data) throws TemplateSyntaxException, NoSuchFieldException, IllegalAccessException {
        // put data object into namespace
        Map<String, Object> namespace = new HashMap<>();
        namespace.put("data", data);

        return parse(input, namespace);
    }

    private <T> String parse(String input, Map<String, Object> namespace) throws TemplateSyntaxException, NoSuchFieldException, IllegalAccessException {
        // Keeps track of curly braces
        int interpolationStartIndex = -1;
        int interpolationEndIndex = -1;

        Deque<Directive> directivesStack = new ArrayDeque<>();
        Set<String> directiveVariables = new HashSet<>();

        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);
            char prev = (i - 1) >= 0 ? input.charAt(i - 1) : ' ';

            // Opening of interpolation
            // Captures { ... } without escapes
            if ((c == '{') && (prev != '\\')) {
                interpolationStartIndex = i;
            }

            if ((c == '}') && (prev != '\\') && (interpolationStartIndex != -1)) {
                interpolationEndIndex = i;
            }

            // Interpolation captures
            // Evaluate the token
            if ((interpolationStartIndex != -1) && (interpolationEndIndex != -1)) {
                // Get token
                String token = input.substring(interpolationStartIndex, interpolationEndIndex + 1);

                // Remove braces
                token = token.substring(1, token.length() - 1);

                // Trim
                token = token.trim();

                // Start directive
                if (token.startsWith("#")) {
                    // Directive Type
                    String[] directiveTokens = token.replaceFirst("#", "").split(" ");

                    // Directive matching
                    if ((directiveTokens.length == 4) && (directiveTokens[0].equals("for")) && (directiveTokens[2].equals("in"))) {
                        // For directive
                        directiveVariables.add(directiveTokens[1]);

                        directivesStack.push(new Directive(DirectiveType.FOREACH, interpolationStartIndex, interpolationEndIndex + 1, directiveTokens));
                    } else if ((directiveTokens.length == 2) && (directiveTokens[0].equals("if"))) {
                        // If directive
                        directivesStack.push(new Directive(DirectiveType.IF, interpolationStartIndex, interpolationEndIndex + 1, directiveTokens));
                    } else {
                        // Illegal directive
                        throw new TemplateSyntaxException("no directive type exists for this syntax");
                    }
                } else if (token.startsWith("/")) {
                    // End directive

                    String directiveType = token.replaceFirst("/", "");

                    // Directive matching
                    if (directiveType.equals("for")) {
                        // For directive
                        if ((!directivesStack.isEmpty()) && (directivesStack.peek().getType() == DirectiveType.FOREACH)) {
                            // Get opening tag
                            Directive openingDir = directivesStack.pop();
                            String[] openingDirTokens = openingDir.getTokens();

                            // Get enclosed snippet
                            String snippet = input.substring(openingDir.getEndNumber(), interpolationStartIndex);

                            // Gets the array/iterable
                            Object value = parseExpression(openingDirTokens[3], namespace, directiveVariables);

                            StringBuilder newSnippet = new StringBuilder();

                            if (value.getClass().isArray()) {
                                for (Object v : (Object[]) value) {
                                    // Parse with target variable in namespace
                                    namespace.put(openingDirTokens[1], v);
                                    String parsedSnippet = parse(snippet, namespace);
                                    namespace.remove(openingDirTokens[1]);

                                    // Add snippet
                                    newSnippet.append(parsedSnippet);
                                }

                            } else if (value instanceof Iterable) {
                                for (Object v : (Iterable<?>) value) {
                                    // Parse with target variable in namespace
                                    namespace.put(openingDirTokens[1], v);
                                    String parsedSnippet = parse(snippet, namespace);
                                    namespace.remove(openingDirTokens[1]);

                                    // Add snippet
                                    newSnippet.append(parsedSnippet);
                                }
                            } else {
                                throw new TemplateSyntaxException("target of for directive must be either an array or iterable");
                            }

                            // Remove target variable from allowed roots
                            directiveVariables.remove(openingDirTokens[1]);

                            // Insert snippet
                            String newInput = input = input.substring(0, openingDir.getStartNumber()) + newSnippet + input.substring(interpolationEndIndex + 1);
                            i += (newInput.length() - input.length());
                            input = newInput;
                        } else {
                            throw new TemplateSyntaxException("mismatched directives");
                        }
                    } else if (directiveType.equals("if")) {
                        // If directive
                        if ((!directivesStack.isEmpty()) && (directivesStack.peek().getType() == DirectiveType.IF)) {
                            // Get opening tag
                            Directive openingDir = directivesStack.pop();
                            String[] openingDirTokens = openingDir.getTokens();

                            // Get enclosed sprite
                            String snippet = input.substring(openingDir.getEndNumber(), interpolationStartIndex);

                            // Evaluate target boolean
                            Object value = parseExpression(openingDirTokens[1], namespace, directiveVariables);

                            // check for boolean
                            if (value instanceof Boolean) {
                                boolean showSnippet = (Boolean) value;

                                if (!showSnippet) {
                                    snippet = "";
                                }
                            } else {
                                throw new TemplateSyntaxException("If condition must be a boolean value");
                            }

                            // Insert snippet
                            String newInput = input.substring(0, openingDir.getStartNumber()) + snippet + input.substring(interpolationEndIndex + 1);
                            i += (newInput.length() - input.length());
                            input = newInput;
                        } else {
                            throw new TemplateSyntaxException("mismatched directives");
                        }
                    } else {
                        throw new TemplateSyntaxException("Illegal directive closing tag");
                    }
                } else {
                    // Do not evaluate values inside an if directive if the directive will not be rendered
                    Directive topDirective = directivesStack.peek();

                    if ((topDirective != null) && (topDirective.getType() == DirectiveType.IF)) {
                        String ifValue = topDirective.getTokens()[1];

                        Object value = parseExpression(ifValue, namespace, directiveVariables);

                        // Skip value
                        if (value instanceof Boolean) {
                            if (!((Boolean) value)) {
                                interpolationStartIndex = -1;
                                interpolationEndIndex = -1;
                                continue;
                            }
                        }
                    }

                    // Expression
                    String replacedExpr = parseExpression(token, namespace, directiveVariables).toString();

                    // Insert snippet
                    String newInput = input.substring(0, interpolationStartIndex) + replacedExpr + input.substring(interpolationEndIndex + 1);
                    i += (newInput.length() - input.length());
                    input = newInput;
                }

                interpolationStartIndex = -1;
                interpolationEndIndex = -1;
            }

            i++;
        }

        return input;
    }

    private Object parseExpression(String exp, Map<String, Object> namespace, Set<String> allowedRoots) throws TemplateSyntaxException, NoSuchFieldException, IllegalAccessException {
        String[] commands = exp.split("\\.");

        String root = commands[0];

        // Support for basic negation
        if (commands[0].startsWith("!")) {
            root = commands[0].substring(1);
        }

        if (namespace.containsKey(root)) {
            // Data interpolation with variable

            return evaluateValue(commands, namespace.get(root));
        } else if (allowedRoots.contains(commands[0])) {
            //Skip these braces
            return "{" + exp + "}";
        } else {
            throw new TemplateSyntaxException("variable " + commands[0] + " does not exist");
        }
    }

    private <T> Object evaluateValue(String[] path, T data) throws NoSuchFieldException, IllegalAccessException {
        if (path.length == 1) {
            return data;
        }

        boolean negateValue = false;

        // Negation
        if (path[0].startsWith("!")) {
            path[0] = path[0].substring(1);
            negateValue = true;
        }

        Field current = data.getClass().getDeclaredField(path[1]);

        for (int i = 2; i < path.length; i++) {
            String field = path[i];
            current = current.getClass().getDeclaredField(field);
        }

        Object value = current.get(data);

        if ((negateValue) && (value instanceof Boolean)) {
            return !((Boolean) value);
        }

        return value;
    }
}