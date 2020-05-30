package net.joedoe.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utils {
    public static Properties getProperties() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
