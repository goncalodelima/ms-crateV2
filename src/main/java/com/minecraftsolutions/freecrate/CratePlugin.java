package com.minecraftsolutions.freecrate;

import com.minecraftsolutions.freecrate.command.CrateCommand;
import com.minecraftsolutions.freecrate.controller.UserController;
import com.minecraftsolutions.freecrate.database.DatabaseProvider;
import com.minecraftsolutions.freecrate.inventory.*;
import com.minecraftsolutions.freecrate.listener.PlayerListener;
import com.minecraftsolutions.freecrate.model.crate.controller.CrateController;
import com.minecraftsolutions.freecrate.model.crate.loader.CrateLoader;
import com.minecraftsolutions.freecrate.model.crate.service.CrateFoundationService;
import com.minecraftsolutions.freecrate.model.crate.service.CrateService;
import com.minecraftsolutions.freecrate.model.reward.loader.RewardLoader;
import com.minecraftsolutions.freecrate.model.reward.service.RewardFoundationService;
import com.minecraftsolutions.freecrate.model.reward.service.RewardService;
import com.minecraftsolutions.freecrate.model.user.service.UserFoundationService;
import com.minecraftsolutions.freecrate.model.user.service.UserService;
import com.minecraftsolutions.freecrate.util.Configuration;
import com.minecraftsolutions.database.Database;
import com.minecraftsolutions.utils.Formatter;
import lombok.Getter;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class CratePlugin extends JavaPlugin {

    public static final Formatter FORMATTER = new Formatter();

    private Configuration inventories;

    private Configuration messages;

    private Configuration reward;

    private Configuration crate;

    private RewardFoundationService rewardService;

    private CrateFoundationService crateService;
    private CrateController crateController;

    private UserFoundationService userService;

    private ViewFrame viewFrame;

    private Database datacenter;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        inventories = new Configuration("inventories.yml", this);
        inventories.saveFile();

        messages = new Configuration("messages.yml", this);
        messages.saveFile();

        reward = new Configuration("reward.yml", this);
        reward.saveFile();

        crate = new Configuration("crate.yml", this);
        crate.saveFile();

        rewardService = new RewardService();
        new RewardLoader().setup().forEach(rewardService::put);

        crateService = new CrateService();
        new CrateLoader().setup().forEach(crateService::put);
        crateController = new CrateController();

        datacenter = new DatabaseProvider().setup();
        userService = new UserService(datacenter);

        viewFrame = ViewFrame
                        .create(this)
                        .with(new CrateGeneralInventory(), new CrateRewardInventory(), new UserFilterInventory(), new UserRewardInventory(), new CrateRankingInventory())
                        .register();

        getCommand("crate").setExecutor(new CrateCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        new UserController().runTaskTimer(this, 20, 20);

    }

    @Override
    public void onDisable() {
        datacenter.close();
    }

    public static CratePlugin getInstance(){
        return getPlugin(CratePlugin.class);
    }

}
