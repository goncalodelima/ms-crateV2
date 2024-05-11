package com.minecraftsolutions.freecrate.model.crate.controller;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.crate.Crate;
import com.minecraftsolutions.freecrate.model.reward.Reward;
import com.minecraftsolutions.freecrate.model.user.User;
import com.minecraftsolutions.freecrate.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CrateController {

    public void giveCrate(Player player, Crate crate, int amount, boolean virtual, boolean remove){

        User user = CratePlugin.getInstance().getUserService().get(player.getName());

        if (amount <= 0)
            return;

        if (remove) {
            user.getCrates().put(crate, user.getCrates().getOrDefault(crate, 0) - amount);
            CratePlugin.getInstance().getUserService().update(user);
        }

        if (virtual){
            user.getCrates().put(crate, user.getCrates().getOrDefault(crate, 0) + amount);
            CratePlugin.getInstance().getUserService().update(user);
            return;
        }

        ItemStack item = crate.getItem().clone();
        item.setAmount(amount);
        player.getInventory().addItem(item);
    }

    public void openAll(User user){
        user.getCrates().keySet().forEach(crate -> openAll(user, crate));
    }

    public void openAll(User user, Crate crate){
        int amount = user.getCrates().getOrDefault(crate, 0);
        open(user, crate, amount);
    }

    public void open(User user, Crate crate, int amount){

        user.setCratesOpen(user.getCratesOpen() + amount);

        while (amount-- > 0)
            open(user, crate);

    }

    public void open(User user, Crate crate){

        HashMap<Object, Double> rewards = new HashMap<>();
        crate.getRewards().forEach(reward -> rewards.put(reward, reward.getChance()));
        Reward reward = (Reward) BukkitUtils.getObjectChance(rewards);

        if (reward == null)
            return;

        if (!user.getFilter().contains(reward))
            user.getRewards().put(reward, user.getRewards().getOrDefault(reward, 0) + 1);

        user.getCrates().put(crate, user.getCrates().getOrDefault(crate, 0) - 1);
        CratePlugin.getInstance().getUserService().update(user);
    }

    public void giveReward(User user, Reward reward){

        int amount = user.getRewards().getOrDefault(reward, 0);

        if (amount <= 0) {
            user.getRewards().remove(reward);
            CratePlugin.getInstance().getUserService().update(user);
            return;
        }

        user.getRewards().put(reward, user.getRewards().getOrDefault(reward, 0) - 1);
        CratePlugin.getInstance().getUserService().update(user);
        if (reward.isCMD()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.getCommand().replace("{player}", user.getName()));
        else user.getPlayer().getInventory().addItem(reward.getItem());
        giveReward(user, reward);
    }

}
