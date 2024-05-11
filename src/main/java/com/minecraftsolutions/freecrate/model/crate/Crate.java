package com.minecraftsolutions.freecrate.model.crate;

import com.minecraftsolutions.freecrate.model.reward.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class Crate {

    private final String identifier;
    private final ItemStack item;
    private final List<Reward> rewards;

}
