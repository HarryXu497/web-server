package assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for managing all static assets in the web application
 * @author Harry Xu
 * @version 1.0 - June 8th 2023
 */
public class AssetEngine {
    /** Common extensions of text files */
    private final static Set<String> TEXT_EXTENSIONS = new HashSet<>();

    /** Common extensions of image files */
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
        IMAGE_EXTENSIONS.add("ico");
    }

    /** the registry for assets */
    private final Map<String, byte[]> assets;

    /**
     * Constructs an {@link AssetEngine} with predefined assets registered
     * @param paths the assets to be registered
     * @throws IOException if an error occurs while working with the files
     * @throws NullPointerException if {@code points} is null
     */
    public AssetEngine(String... paths) throws IOException {
        if (paths == null) {
            throw new NullPointerException("paths cannot be null");
        }

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
     * get the raw asset from the registry as an HTTP compatible array of bytes
     * This is necessary to implement 404 and other error pages, as well as non asset files such as stylesheets
     * @param path the registered path of the asset
     * @return the read asset string
     * @throws AssetNotFoundException if no asset is registered under the path
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
        StringBuilder content = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new FileReader(inputFile))) {
            int currentChar = input.read();

            while (currentChar != -1) {
                content.append((char) currentChar);

                currentChar = input.read();
            }
        }

        return content.toString();
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

        // Initial buffer size
        List<Byte> bytes = new ArrayList<>(10000);

        try (InputStream stream = Files.newInputStream(imageFile.toPath())) {

            int currentChar = stream.read();

            while (currentChar != -1) {
                bytes.add((byte) currentChar);
                currentChar = stream.read();
            }
        }

        // Copy list to primitive array
        byte[] buffer = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            buffer[i] = bytes.get(i);
        }

        return buffer;
    }

    /**
     * toString
     * returns a string representation of this object
     * @return the string representation
     */
    @Override
    public String toString() {
        return "AssetEngine{" + this.assets + "}";
    }
}
