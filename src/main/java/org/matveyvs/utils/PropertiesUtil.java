package org.matveyvs.utils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private static final String FILE_PROPERTIES = "app.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (var inputStream =
                     PropertiesUtil.class.getClassLoader()
                             .getResourceAsStream(FILE_PROPERTIES)) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

}
