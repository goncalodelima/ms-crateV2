package com.minecraftsolutions.freecrate.model.crate.adapter;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.crate.Crate;
import com.minecraftsolutions.freecrate.util.configuration.ConfigurationAdapter;
import com.minecraftsolutions.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.stream.Collectors;

public class CrateAdapter implements ConfigurationAdapter<Crate> {

    @Override
    public Crate adapt(ConfigurationSection section) {
        return Crate.builder()
                .identifier(section.getString("identifier"))
                .item(new ItemBuilder(Material.getMaterial(section.getString("item.material")), 1, (short) section.getInt("item.data"))
                        .setDisplayName(section.getString("item.name"))
                        .setLore(section.getStringList("item.lore"))
                        .glow(section.getBoolean("item.glow"))
                        .build())
                .rewards(section.getStringList("rewards")
                        .stream()
                        .map(key -> CratePlugin.getInstance().getRewardService().get(key))
                        .collect(Collectors.toList()))
                .build();
    }

}
