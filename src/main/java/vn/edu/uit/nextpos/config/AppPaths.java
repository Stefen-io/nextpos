package vn.edu.uit.nextpos.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppPaths {

    private AppPaths() {
    }

    public static Path getDataDir() {
        String configuredPath = AppConfig.getString("app.data.dir", "data");
        Path dataDir = Paths.get(configuredPath);
        if (!dataDir.isAbsolute()) {
            dataDir = Paths.get(System.getProperty("user.dir")).resolve(dataDir).normalize();
        }
        return dataDir;
    }

    public static Path getPicturesDir() {
        return getDataDir().resolve("pictures");
    }

    public static Path getSessionFile() {
        return getDataDir().resolve("session").resolve("account.txt");
    }

    public static void ensureDirectoriesExist() {
        Path dataDir = getDataDir();
        ensureDirectoriesExist(dataDir);
        ensureDirectoriesExist(dataDir.resolve("session"));
        ensureDirectoriesExist(dataDir.resolve("pictures"));
    }

    public static String picturePath(String fileName) {
        return getPicturesDir().resolve(fileName).toString();
    }

    private static void ensureDirectoriesExist(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create directory: " + path, e);
        }
    }
}
