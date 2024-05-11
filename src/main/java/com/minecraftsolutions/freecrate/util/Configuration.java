package com.minecraftsolutions.freecrate.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Configuration {

    private String name;
    private JavaPlugin javaPlugin;

    private FileConfiguration configuration;

    public Configuration(String name, JavaPlugin javaPlugin) {
        this.name = name;
        this.javaPlugin = javaPlugin;
    }

    public Configuration(File file) {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void saveFile() {
        javaPlugin.saveResource(name, false);
        configuration = YamlConfiguration.loadConfiguration(new File(this.javaPlugin.getDataFolder() + File.separator + name));
    }

    public FileConfiguration getConfig() {
        return configuration;
    }

}
