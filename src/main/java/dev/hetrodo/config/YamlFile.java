package dev.hetrodo.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

public class YamlFile extends YamlConfiguration {
    private final String path;

    public YamlFile(String pathName) {
        path = "plugins/" + pathName + "/config.yaml";
        this.load();
    }

    public void save() {
        try {
            this.save(this.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            this.load(this.path);
        } catch (IOException | InvalidConfigurationException e) {
            if (e instanceof InvalidConfigurationException) {
                e.printStackTrace();
            }
        }
    }

    public <T> T getGeneric(Class<? extends T> clazz, String path) {
        return clazz.cast(this.get(path));
    }
}
