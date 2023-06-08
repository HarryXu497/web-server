package database;

public class Filter {
    String sourceCode = null;
    String[] acceptLibrary = {"io.BufferedInputStream", "io.BufferedOutputStream", "util", "lang.Boolean", "math", "lang.Byte", "lang.Character", "lang.Double", "lang.Float", "lang.Integer", "lang.Long", "lang.Math", "lang.Number", "lang.Object", "lang.Short", "lang.String", "lang.StringBuffer", "lang.StringBuilder", "io.BufferedReader", "io.BufferedWriter", "io.ByteArrayInputStream", "io.ByteArrayOutputStream", "io.CharArrayReader", "io.CharArrayWriter", "io.DataInputStream", "io.DataOutputStream", "io.Reader", "io.Writer", "io.StringReader", "io.StringWriter"};
    public String filter() {
        for (int i = 0; i < this.sourceCode.length(); i++) {
            int endIndex = Math.min((i + 7), this.sourceCode.length());
            if (this.sourceCode.substring(i, endIndex).equals("import ")) {
                int startJava = endIndex;
                for (; startJava < this.sourceCode.length(); startJava++) {
                    if (this.sourceCode.charAt(startJava) != ' ') {
                        break;
                    }
                }
                boolean accept = false;
                for (String s : acceptLibrary) {
                    if ((this.sourceCode.length() >= startJava + s.length()) && (this.sourceCode.substring(startJava, startJava + s.length()).equals(s))) {
                        accept = true;
                        break;
                    }
                }
                if(!accept) {
                    return "Illegal Library Imported (For Example java.lang.invoke, java.awt)";
                }
            }
        }
        return null;
    }
}
