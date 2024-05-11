package com.minecraftsolutions.freecrate.model.user;

import com.minecraftsolutions.freecrate.model.crate.Crate;
import com.minecraftsolutions.freecrate.model.reward.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
@Builder
public class User {

    private final String name;
    private final Map<Crate, Integer> crates;
    private final Map<Reward, Integer> rewards;
    private final List<Reward> filter;
    private int cratesOpen;

    public Player getPlayer(){
        return Bukkit.getPlayerExact(this.name);
    }

}
