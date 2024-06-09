package dev.emperor.config;

import dev.emperor.config.configs.*;
import java.nio.charset.*;
import org.apache.commons.io.*;
import java.io.*;
import java.util.*;
import dev.emperor.*;
import com.google.gson.*;

public class ConfigManager
{
    public static final List<Config> configs;
    public static final File dir;
    private static final Gson gson;

    public ConfigManager() {
        if (!ConfigManager.dir.exists()) {
            ConfigManager.dir.mkdir();
        }
        ConfigManager.configs.add((Config)new ModuleConfig());
        ConfigManager.configs.add((Config)new HudConfig());
    }

    public void loadConfig(final String name) {
        JsonParser jsonParser = new JsonParser();
        final File file = new File(ConfigManager.dir, name);
        if (file.exists()) {
            System.out.println("Loading config: " + name);
            for (final Config config : ConfigManager.configs) {
                if (config.getName().equals(name)) {
                    try {
                        config.loadConfig(jsonParser.parse((Reader)new FileReader(file)).getAsJsonObject());
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            this.saveConfig(name);
        }
    }

    public void loadUserConfig(final String name) {
        JsonParser jsonParser = new JsonParser();
        final File file = new File(ConfigManager.dir, name);
        if (file.exists()) {
            System.out.println("Loading config: " + name);
            for (final Config config : ConfigManager.configs) {
                if (config.getName().equals("modules.json")) {
                    try {
                        config.loadConfig(jsonParser.parse((Reader)new FileReader(file)).getAsJsonObject());
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            this.saveUserConfig(name);
        }
    }

    public void saveConfig(final String name) {
        final File file = new File(ConfigManager.dir, name);
        try {
            System.out.println("Saving config: " + name);
            file.createNewFile();
            for (final Config config : ConfigManager.configs) {
                if (config.getName().equals(name)) {
                    FileUtils.writeByteArrayToFile(file, ConfigManager.gson.toJson((JsonElement)config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void saveUserConfig(final String name) {
        final File file = new File(ConfigManager.dir, name);
        try {
            System.out.println("Saving config: " + name);
            file.createNewFile();
            for (final Config config : ConfigManager.configs) {
                if (config.getName().equals("modules.json")) {
                    FileUtils.writeByteArrayToFile(file, ConfigManager.gson.toJson((JsonElement)config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void loadAllConfig() {
        System.out.println("Loading all configs...");
        ConfigManager.configs.forEach(it -> this.loadConfig(it.getName()));
    }

    public void saveAllConfig() {
        System.out.println("Saving all configs...");
        ConfigManager.configs.forEach(it -> this.saveConfig(it.getName()));
    }

    static {
        configs = new ArrayList<Config>();
        dir = new File(Client.mc.mcDataDir, "Emperor");
        gson = new GsonBuilder().setPrettyPrinting().create();
    }
}
