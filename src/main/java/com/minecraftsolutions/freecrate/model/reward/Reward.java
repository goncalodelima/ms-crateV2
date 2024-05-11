package com.minecraftsolutions.freecrate.model.reward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Builder
@Data
public class Reward {

    private final String identifier;
    private final double chance;
    private final ItemStack item;
    private final String command;
    private final boolean CMD;

}
