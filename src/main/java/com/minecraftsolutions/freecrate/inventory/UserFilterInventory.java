package com.minecraftsolutions.freecrate.inventory;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.user.User;
import com.minecraftsolutions.utils.ItemBuilder;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class UserFilterInventory extends View {

    private final State<Pagination> filterState = buildLazyPaginationState(
            (context -> CratePlugin.getInstance().getUserService().get(context.getPlayer().getName()).getFilter()))
            .layoutTarget('I')
            .itemFactory((itemBuilder, value) -> itemBuilder.withItem(new ItemBuilder(value.getItem().clone())
                    .addLore(CratePlugin.getInstance().getInventories().getConfig().getStringList("filter.item.lore").stream().map(str -> str.replace("&", "§")).collect(Collectors.toList()))
                    .build()).onClick(click -> {

                Player player = click.getPlayer();
                User user = CratePlugin.getInstance().getUserService().get(player.getName());
                user.getFilter().remove(value);
                CratePlugin.getInstance().getUserService().update(user);

                click.closeForPlayer();
                CratePlugin.getInstance().getViewFrame().open(UserFilterInventory.class, player);
            }))
            .build();

    @Override
    public void onInit(ViewConfigBuilder config) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();

        config
                .title(configuration.getString("filter.title").replace("&", "§"))
                .size(configuration.getInt("filter.size"))
                .layout(configuration.getStringList("filter.layout").toArray(new String[0]))
                .cancelOnClick()
                .cancelOnDrag()
                .cancelOnDrop()
                .cancelOnPickup()
                .build();

    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
        Pagination pagination = filterState.get(render);

        render.slot(configuration.getInt("filter.empty.slot"), new ItemBuilder(Material.getMaterial(configuration.getString("filter.empty.material")), 1, (short) configuration.getInt("filter.empty.data"))
                .setDisplayName(configuration.getString("filter.empty.name"))
                .build())
                .watch(filterState)
                .displayIf(() -> pagination.getComponents().isEmpty());

        render.layoutSlot('C', new ItemBuilder(Material.getMaterial(configuration.getString("filter.close.material")), 1, (short) configuration.getInt("filter.close.data"))
                .setDisplayName(configuration.getString("filter.close.name"))
                .build()
        ).onClick(click -> CratePlugin.getInstance().getViewFrame().open(CrateGeneralInventory.class, click.getPlayer()));

        render.layoutSlot('<', new ItemBuilder(Material.getMaterial(configuration.getString("filter.back.material")), 1, (short) configuration.getInt("filter.back.data"))
                        .setDisplayName(configuration.getString("filter.back.name"))
                        .build())
                .watch(filterState)
                .displayIf(pagination::canBack)
                .onClick(pagination::back);

        render.layoutSlot('>', new ItemBuilder(Material.getMaterial(configuration.getString("filter.advance.material")), 1, (short) configuration.getInt("filter.advance.data"))
                        .setDisplayName(configuration.getString("filter.advance.name"))
                        .build())
                .watch(filterState)
                .displayIf(pagination::canAdvance)
                .onClick(pagination::advance);

    }
}
