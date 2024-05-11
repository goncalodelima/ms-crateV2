package com.minecraftsolutions.freecrate.command;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.inventory.CrateGeneralInventory;
import com.minecraftsolutions.freecrate.model.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CrateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        FileConfiguration messages = CratePlugin.getInstance().getMessages().getConfig();

        if (args.length == 0 && sender instanceof Player){
            Player player = (Player) sender;
            CratePlugin.getInstance().getViewFrame().open(CrateGeneralInventory.class, player);
            return true;
        }

        if (!(sender.hasPermission("ms-cratev2.admin"))){
            sender.sendMessage(messages.getString("no-permission").replace("&", "§"));
            return false;
        }

        if (args.length != 5 || !args[0].equalsIgnoreCase("give")){
            sender.sendMessage(messages.getString("crate-give-error").replace("&", "§"));
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null || !target.isOnline()){
            sender.sendMessage(messages.getString("invalid-player").replace("&", "§"));
            return false;
        }

        Crate crate = CratePlugin.getInstance().getCrateService().get(args[2]);

        if (crate == null){
            sender.sendMessage(messages.getString("invalid-crate").replace("&", "§"));
            return false;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[3]);
        }catch (Exception e){
            sender.sendMessage(messages.getString("invalid-amount").replace("&", "§"));
            return false;
        }

        if (!args[4].equalsIgnoreCase("virtual") && !args[4].equalsIgnoreCase("fisica")){
            sender.sendMessage(messages.getString("invalid-type").replace("&", "§"));
            return false;
        }

        boolean virtual = args[4].equalsIgnoreCase("virtual");

        CratePlugin.getInstance().getCrateController().giveCrate(target, crate, amount, virtual, false);
        sender.sendMessage(messages.getString("crate-send").replace("&", "§"));
        return false;
    }
}
