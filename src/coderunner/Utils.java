package coderunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> allFilesInDirectory(String directory) {
        File folder = new File(directory);
        File[] files;

        List<String> filePaths = new ArrayList<>();

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
