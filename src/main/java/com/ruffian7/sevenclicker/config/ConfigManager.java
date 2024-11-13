package com.ruffian7.sevenclicker.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILENAME = "sevenclicker.txt";
    private static final String CONFIG_PATH = System.getProperty("user.home") + File.separator + CONFIG_FILENAME;
    private Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadConfig();
    }

    public Properties loadConfig() {
        Properties config = new Properties();
        File configFile = new File(CONFIG_PATH);

        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                config.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading config: " + e.getMessage());
            }
        }

        this.properties = config;
        return config;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    public void saveConfig(String toggleKey1, String toggleKey2, int toggleMouseButton,
                           int minCPS, int maxCPS, int rightMinCPS, int rightMaxCPS,
                           int button, boolean minecraftOnly, boolean randomizer, boolean rightEnabled) {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_PATH)) {
            StringBuilder config = new StringBuilder();
            config.append("# SevenClicker Configuration\n\n");

            // Keybinds
            config.append("# Keybinds\n");
            config.append("toggleKey1=" + toggleKey1 + "\n");
            config.append("toggleKey2=" + toggleKey2 + "\n");
            config.append("toggleMouseButton=" + toggleMouseButton + "\n\n");

            // Left Click Settings
            config.append("# Left Click Settings\n");
            config.append("minCPS=" + minCPS + "\n");
            config.append("maxCPS=" + maxCPS + "\n");

            // Right Click Settings
            config.append("\n# Right Click Settings\n");
            config.append("rightMinCPS=" + rightMinCPS + "\n");
            config.append("rightMaxCPS=" + rightMaxCPS + "\n");
            config.append("rightEnabled=" + rightEnabled + "\n");

            // Other Settings
            config.append("\n# Other Settings\n");
            config.append("button=" + button + "\n");
            config.append("minecraftOnly=" + minecraftOnly + "\n");
            config.append("randomizer=" + randomizer + "\n");

            fos.write(config.toString().getBytes());

            // Also store in properties
            properties.setProperty("toggleKey1", toggleKey1);
            properties.setProperty("toggleKey2", toggleKey2);
            properties.setProperty("toggleMouseButton", String.valueOf(toggleMouseButton));
            properties.setProperty("minCPS", String.valueOf(minCPS));
            properties.setProperty("maxCPS", String.valueOf(maxCPS));
            properties.setProperty("rightMinCPS", String.valueOf(rightMinCPS));
            properties.setProperty("rightMaxCPS", String.valueOf(rightMaxCPS));
            properties.setProperty("button", String.valueOf(button));
            properties.setProperty("minecraftOnly", String.valueOf(minecraftOnly));
            properties.setProperty("randomizer", String.valueOf(randomizer));
            properties.setProperty("rightEnabled", String.valueOf(rightEnabled));

        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }

    public File getConfigFile() {
        return new File(CONFIG_PATH);
    }
}