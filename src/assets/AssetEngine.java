package assets;

import template.TemplateNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class AssetEngine {

    /** Content Types */
    private final static Set<String> TEXT_EXTENSIONS = new HashSet<>();
    private final static Set<String> IMAGE_EXTENSIONS = new HashSet<>();

    static {
        // Text extensions
        TEXT_EXTENSIONS.add("css");
        TEXT_EXTENSIONS.add("html");
        TEXT_EXTENSIONS.add("js");

        // Image extensions
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("png");
    }

    /** the registry for templates*/
    private final Map<String, byte[]> assets;

    /**
     * Constructs a template engine with predefined templates registered
     * @param paths the templates to be registered
     * @throws IOException if an error occurs while working with the files
     */
    public AssetEngine(String... paths) throws IOException {
        this.assets = new HashMap<>();

        for (String path : paths) {
            File dir = new File(path);

            if (dir.isDirectory()) {
                File[] files = dir.listFiles();

                if (files != null) {
                    for (File file : files) {
                        String filename = file.getPath();

                        this.assets.put(filename.replace("\\", "/"), this.read(filename));
                    }
                }
            }

            if (dir.isFile()) {
                this.assets.put(path, this.read(path));
            }
        }
    }

    /**
     * getAsset
     * get the raw asset from the registry as an HTTP compatible string
     * This is necessary to implement 404 and other error pages, as well as non template files such as stylesheets
     * @param path the registered path of the template
     * @return the read template string
     * @throws TemplateNotFoundException if no template is registered under the path
     * */
    public byte[] getAsset(String path) {
        if (!this.assets.containsKey(path)) {
            throw new AssetNotFoundException("Asset " + path + " cannot be found");
        }

        return this.assets.get(path);
    }

    /**
     * read
     * opens and reads a file into a format compatible with HTTP
     * @param inputFile the path of the file to read from
     * @return the contents of the input file as a string
     * @throws IOException if an error occurs while opening or reading the file
     */
    private byte[] read(String inputFile) throws IOException {
        String extension = inputFile.substring(inputFile.lastIndexOf('.') + 1);

        if (TEXT_EXTENSIONS.contains(extension)) {
            return readText(inputFile).getBytes(StandardCharsets.UTF_8);
        }

        if (IMAGE_EXTENSIONS.contains(extension)) {
            return readImage(inputFile);
        }

        throw new ExtensionNotFoundException("File extension " + extension + " does not a have a defined way to read it");
    }

    /**
     * readText
     * opens and reads a file and joins its content together into one string
     * @param inputFile the path of the file to read from
     * @return the contents of the input file as a string
     * @throws IOException if an error occurs while opening or reading the file
     */
    private String readText(String inputFile) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        return String.join("\n", lines);
    }

    /**
     * readImage
     * opens and reads a file and joins its content together into one string
     * @param inputFile the path of the file to read from
     * @return the contents of the input file as a string
     * @throws IOException if an error occurs while opening or reading the file
     */
    private byte[] readImage(String inputFile) throws IOException {
        File imageFile = new File(inputFile);
        List<Byte> bytes = new ArrayList<>(10000);

        try (InputStream stream = Files.newInputStream(imageFile.toPath())) {

            int c;

            while ((c = stream.read()) != -1) {
                bytes.add((byte) c);
            }
        }

        byte[] buffer = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            buffer[i] = bytes.get(i);
        }

        return buffer;
    }

    public static boolean isText(String extension) {
        return TEXT_EXTENSIONS.contains(extension);
    }

    public static boolean isImage(String extension) {
        return IMAGE_EXTENSIONS.contains(extension);
    }

    @Override
    public String toString() {
        return "AssetEngine{" + assets + "}";
    }
}
