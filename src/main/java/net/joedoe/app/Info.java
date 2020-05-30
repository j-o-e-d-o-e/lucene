package net.joedoe.app;

public class Info {
    public static final String CONTENTS = "contents";
    public static final String FILE_NAME = "filename";
    public static final String FILE_PATH = "filepath";
    public static final int MAX_SEARCH = 10;

    private static final String dataDirBase = "/media/joe/E/programming";
    private static final String indexDirBase = "src/main/resources/index/";

    @SuppressWarnings("unused")
    public enum Lang {
        PYTHON("/py/code_library_py/library"),
        JAVA("/java/code_library_java/library"),
        SPRING("/java/code_library_spring/library"),
        CSHARP("/c#/code_library_c#/library"),
        CPP("/cpp/code_library_c++/library"),
        DOCKER("/docker/code_library_docker/library");

        public final String dataDir;

        Lang(String dataDir) {
            this.dataDir = dataDirBase + dataDir;
        }

        public String getIndexDir() {
            return indexDirBase + this.toString().toLowerCase();
        }
    }
}
