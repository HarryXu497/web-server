package coderunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains utility methods for operations related to the {@link CodeRunner}
 * @author Harry Xu
 * @version 1.0 - June 9th  2023
 */
public class Utils {
    /**
     * allFilesInDirectory
     * Reads all files paths in a directory and returns them in a list
     * @param directory the directory to read
     * @return a list of file paths ordered lexicographically
     */
    public static List<String> allFilesInDirectory(String directory) {
        File folder = new File(directory);
        File[] files;

        List<String> filePaths = new ArrayList<>();

        // Read files in directory
        if ((files = folder.listFiles()) != null) {
            for (File fileEntry : files) {
                if (fileEntry.isFile()) {
                    filePaths.add(directory + "/" + fileEntry.getName());
                }
            }
        }

        return filePaths;
    }
}
