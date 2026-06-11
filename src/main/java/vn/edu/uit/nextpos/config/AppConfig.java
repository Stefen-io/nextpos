package vn.edu.uit.nextpos.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final String CONFIG_FILE = "application.properties";
    private static final Properties PROPERTIES = loadProperties();

    private AppConfig() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        String overrideValue = System.getProperty(key);
        if (overrideValue != null) {
            return overrideValue;
        }
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String overrideValue = System.getProperty(key);
        if (overrideValue != null) {
            return Boolean.parseBoolean(overrideValue);
        }

        String propertyValue = PROPERTIES.getProperty(key);
        if (propertyValue == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(propertyValue);
    }

    public static String getDbUrl() {
        return getString("app.db.url");
    }

    public static String getDbUser() {
        return getString("app.db.user");
    }

    public static String getDbPassword() {
        return getString("app.db.password");
    }

    public static boolean isSeedOnStartup() {
        return getBoolean("app.seed-on-startup", false);
    }
}
