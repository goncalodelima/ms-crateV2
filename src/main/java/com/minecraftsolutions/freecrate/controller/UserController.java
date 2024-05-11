package com.minecraftsolutions.freecrate.controller;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.user.User;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class UserController extends BukkitRunnable {

    public static List<User> RANKING = new ArrayList<>();

    @Override
    public void run() {

        RANKING.clear();

        List<User> add = CratePlugin.getInstance().getUserService().getTop();
        if (add != null) {
            RANKING.addAll(CratePlugin.getInstance().getUserService().getTop());
        }

        for (int i = RANKING.size(); i < 10; i++)
            RANKING.add(new User("nobody-" + i, null, null, null, 0));

    }

}
