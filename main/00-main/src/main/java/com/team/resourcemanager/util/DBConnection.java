package com.team.resourcemanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {

    private static final Map<String, String> DOTENV = loadDotenv();

    // DB 연결 생성
    public static Connection getConnection() throws SQLException {
        String url = getSetting("DB_URL");
        String user = getSetting("DB_USER");
        String password = getSetting("DB_PASSWORD");

        if (url == null || url.isBlank() || user == null || user.isBlank() || password == null) {
            throw new SQLException("DB_URL, DB_USER, and DB_PASSWORD must be set in .env or the process environment.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String getSetting(String key) {
        String environmentValue = System.getenv(key);
        return environmentValue != null ? environmentValue : DOTENV.get(key);
    }

    private static Map<String, String> loadDotenv() {
        Map<String, String> values = new HashMap<>();
        Path dotenvPath = findDotenv();
        if (dotenvPath == null) {
            return values;
        }

        try {
            for (String line : Files.readAllLines(dotenvPath, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, separator).trim();
                String value = trimmed.substring(separator + 1).trim();
                if (value.length() >= 2) {
                    char first = value.charAt(0);
                    char last = value.charAt(value.length() - 1);
                    if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                        value = value.substring(1, value.length() - 1);
                    }
                }
                values.put(key, value);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Could not read .env file: " + e.getMessage());
        }
        return values;
    }

    private static Path findDotenv() {
        try {
            Path codeLocation = Path.of(DBConnection.class.getProtectionDomain().getCodeSource()
                    .getLocation().toURI()).toAbsolutePath().normalize();
            if (Files.isDirectory(codeLocation)) {
                Path found = searchParents(codeLocation, 3);
                if (found != null) {
                    return found;
                }
            } else {
                Path jarDirectory = codeLocation.getParent();
                if (jarDirectory != null && Files.isRegularFile(jarDirectory.resolve(".env"))) {
                    return jarDirectory.resolve(".env");
                }
            }
        } catch (URISyntaxException | NullPointerException e) {
            // Fall back to the process working directory below.
        }

        Path workingDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path localEnv = workingDirectory.resolve(".env");
        return Files.isRegularFile(localEnv) ? localEnv : null;
    }

    private static Path searchParents(Path start, int maxLevels) {
        Path current = start;
        for (int level = 0; current != null && level <= maxLevels; level++, current = current.getParent()) {
            Path candidate = current.resolve(".env");
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
