package com.minecraftsolutions.freecrate.inventory;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.controller.UserController;
import com.minecraftsolutions.utils.ItemBuilder;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class CrateRankingInventory extends View {

    private final ItemStack nobody = new ItemBuilder(Material.getMaterial(CratePlugin.getInstance().getInventories().getConfig().getString("ranking.nobody.material")), 1, (short) CratePlugin.getInstance().getInventories().getConfig().getInt("ranking.nobody.data"))
            .setDisplayName(CratePlugin.getInstance().getInventories().getConfig().getString("ranking.nobody.name").replace("&", "§"))
            .build();

    private final State<Pagination> paginationState = computedPaginationState(
            (context -> UserController.RANKING),
            (context, builder, index, value) -> {

                if (value.getName().contains("nobody-")) {
                    builder.withItem(nobody);
                    return;
                }

                builder.withItem(new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3)
                        .changeSkull(meta -> meta.setOwner(value.getName()))
                        .setDisplayName(CratePlugin.getInstance().getInventories().getConfig().getString("ranking.item.name").replace("&", "§").replace("%position%", String.valueOf(index)).replace("%player%", value.getName()).replace("%amount%", CratePlugin.FORMATTER.formatNumber(value.getCratesOpen())))
                        .setLore(Collections.singletonList(CratePlugin.getInstance().getInventories().getConfig().getString("ranking.item.lore").replace("&", "§").replace("%position%", String.valueOf(index)).replace("%player%", value.getName()).replace("%amount%", CratePlugin.FORMATTER.formatNumber(value.getCratesOpen()))))
                        .build());

            }
    );

    @Override
    public void onInit(ViewConfigBuilder config) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();

        config
                .title(configuration.getString("ranking.title").replace("&", "§"))
                .size(configuration.getInt("ranking.size"))
                .layout(configuration.getStringList("ranking.layout").toArray(new String[0]))
                .cancelOnClick()
                .cancelOnDrag()
                .cancelOnDrop()
                .cancelOnPickup()
                .build();

    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();

        Pagination pagination = paginationState.get(render);

        paginationState.get(render).getComponents().forEach(render::addComponent);

        render.layoutSlot('C', new ItemBuilder(Material.getMaterial(configuration.getString("ranking.close.material")), 1, (short) configuration.getInt("ranking.close.data"))
                .setDisplayName(configuration.getString("ranking.close.name"))
                .build()
        ).onClick(click -> CratePlugin.getInstance().getViewFrame().open(CrateGeneralInventory.class, click.getPlayer()));

        render.layoutSlot('<', new ItemBuilder(Material.getMaterial(configuration.getString("ranking.back.material")), 1, (short) configuration.getInt("ranking.back.data"))
                        .setDisplayName(configuration.getString("ranking.back.name"))
                        .build())
                .watch(paginationState)
                .displayIf(pagination::canBack)
                .onClick(pagination::back);

        render.layoutSlot('>', new ItemBuilder(Material.getMaterial(configuration.getString("ranking.advance.material")), 1, (short) configuration.getInt("ranking.advance.data"))
                        .setDisplayName(configuration.getString("ranking.advance.name"))
                        .build())
                .watch(paginationState)
                .displayIf(pagination::canAdvance)
                .onClick(pagination::advance);

    }
}
