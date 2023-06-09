package filter;

public class Filter {
    private static final String[] allowedImports = {
            "io.BufferedInputStream",
            "io.BufferedOutputStream",
            "util.",
            "lang.Boolean",
            "math",
            "lang.Byte",
            "lang.Character",
            "lang.Double",
            "lang.Float",
            "lang.Integer",
            "lang.Long",
            "lang.Math",
            "lang.Number",
            "lang.Object",
            "lang.Short",
            "lang.String",
            "lang.StringBuffer",
            "lang.StringBuilder",
            "io.BufferedReader",
            "io.BufferedWriter",
            "io.ByteArrayInputStream",
            "io.ByteArrayOutputStream",
            "io.CharArrayReader",
            "io.CharArrayWriter",
            "io.DataInputStream",
            "io.DataOutputStream",
            "io.Reader",
            "io.Writer",
            "io.StringReader",
            "io.StringWriter"
    };

    /**
     * filter
     * filters source code for malicious imports
     * @param sourceCode the source code to filter
     * @return a string error message or null if the code passes
     */
    public static String filter(String sourceCode) {
        for (int i = 0; i < sourceCode.length(); i++) {
            
            int endIndex = Math.min((i + 7), sourceCode.length());
            
            // Find imports
            if (sourceCode.substring(i, endIndex).equals("import ")) {
                
                int importStart = endIndex;

                while (sourceCode.charAt(importStart) == ' ') {
                    importStart++;
                }

                boolean accepted = false;

                for (String s : allowedImports) {
                    if ((sourceCode.length() >= importStart + s.length() + 5) && (sourceCode.startsWith(s, importStart + 5))) {
                        accepted = true;
                        break;
                    }
                }

                if (!accepted) {
                    return "Illegal Library Imported: " + sourceCode.substring(i, sourceCode.indexOf(";", endIndex) + 1);
                }
            }
        }

        return null;
    }
}
