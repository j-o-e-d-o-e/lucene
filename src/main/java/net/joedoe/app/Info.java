package net.joedoe.app;

import java.util.Properties;

public class Info {
    public static final String CONTENTS = "contents";
    public static final String FILE_NAME = "filename";
    public static final String FILE_PATH = "filepath";
    public static final int MAX_SEARCH = 10;

    private static Properties prop = Utils.getProperties();
    private static final String dataDirBase = prop.getProperty("dataDirBase");
    private static final String indexDirBase = prop.getProperty("indexDirBase");

    @SuppressWarnings("unused")
    public enum Lang {
        PYTHON(prop.getProperty("python")),
        JAVA(prop.getProperty("java")),
        SPRING(prop.getProperty("spring")),
        CSHARP(prop.getProperty("csharp")),
        CPP(prop.getProperty("cpp")),
        DOCKER(prop.getProperty("docker"));

        private final String dataDir;

        Lang(String dataDir) {
            this.dataDir = dataDirBase + dataDir;
        }

        public String getDataDir() {
            return dataDir;
        }

        public String getIndexDir() {
            return indexDirBase + this.toString().toLowerCase();
        }
    }
}
