package com.minecraftsolutions.freecrate.model.reward.adapter;

import com.minecraftsolutions.freecrate.model.reward.Reward;
import com.minecraftsolutions.freecrate.util.configuration.ConfigurationAdapter;
import com.minecraftsolutions.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

public class RewardAdapter implements ConfigurationAdapter<Reward> {

    @Override
    public Reward adapt(ConfigurationSection section) {
        return Reward.builder()
                .identifier(section.getString("identifier"))
                .chance(section.getDouble("chance"))
                .item(new ItemBuilder(Material.getMaterial(section.getString("item.material")), section.getInt("item.amount"), (short) section.getInt("item.data"))
                        .setDisplayName(section.getString("item.name"))
                        .setLore(section.getStringList("item.lore"))
                        .glow(section.getBoolean("item.glow"))
                        .changeItemMeta(meta -> {

                            if (section.contains("item.enchants"))
                                for (String enchant : section.getStringList("item.enchants"))
                                    meta.addEnchant(Enchantment.getByName(enchant.split(":")[0]), Integer.parseInt(enchant.split(":")[1]), true);

                        })
                        .build())
                .command(section.getString("command"))
                .CMD(section.getBoolean("CMD"))
                .build();
    }

}
