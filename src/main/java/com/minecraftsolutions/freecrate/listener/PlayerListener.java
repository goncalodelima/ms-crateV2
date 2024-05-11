package com.minecraftsolutions.freecrate.listener;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.crate.Crate;
import com.minecraftsolutions.freecrate.model.crate.service.CrateFoundationService;
import com.minecraftsolutions.freecrate.model.user.User;
import com.minecraftsolutions.freecrate.model.user.service.UserFoundationService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class PlayerListener implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){

        UserFoundationService userService = CratePlugin.getInstance().getUserService();
        Player player = event.getPlayer();
        User user = userService.get(player.getName());

        if (user == null)
            userService.put(new User(player.getName(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), 0));

    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event){

        UserFoundationService userService = CratePlugin.getInstance().getUserService();
        Player player = event.getPlayer();
        User user = userService.get(player.getName());

        if (user != null) {
            userService.remove(user);
        }

    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event){

        UserFoundationService userService = CratePlugin.getInstance().getUserService();
        CrateFoundationService crateService = CratePlugin.getInstance().getCrateService();
        Player player = event.getPlayer();
        User user = userService.get(player.getName());
        Optional<Crate> crate = crateService.get(player.getItemInHand());

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && crate.isPresent()){

            int amount = 1;
            if (player.isSneaking()){
                amount = player.getItemInHand().getAmount();
                player.setItemInHand(null);
            }else player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

            user.getCrates().put(crate.get(), user.getCrates().getOrDefault(crate.get(), 0) + amount);
            CratePlugin.getInstance().getUserService().update(user);
        }

    }

}
