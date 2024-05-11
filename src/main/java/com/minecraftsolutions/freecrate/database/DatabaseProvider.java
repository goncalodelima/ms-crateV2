package com.minecraftsolutions.freecrate.database;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.database.Database;
import com.minecraftsolutions.database.DatabaseCredentials;
import com.minecraftsolutions.database.DatabaseFactory;
import com.minecraftsolutions.database.configuration.DatabaseConfiguration;
import com.minecraftsolutions.database.configuration.provider.BukkitDatabaseConfiguration;
import org.bukkit.configuration.ConfigurationSection;

public class DatabaseProvider {

    public Database setup() {
        ConfigurationSection section = CratePlugin.getInstance().getConfig().getConfigurationSection("database");
        DatabaseConfiguration configuration = new BukkitDatabaseConfiguration(section);
        DatabaseCredentials credentials = configuration.build();
        Database database = DatabaseFactory.getInstance().build(credentials);

        database.getConnection();

        return database;
    }
}
