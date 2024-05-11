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

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserRewardInventory extends View {

    private final State<Pagination> rewardState = buildLazyPaginationState(
            (context -> new ArrayList<>(CratePlugin.getInstance().getUserService().get(context.getPlayer().getName()).getRewards().keySet())))
            .itemFactory((itemBuilder, value) -> itemBuilder.onRender(context -> {

                Player player = context.getPlayer();
                User user = CratePlugin.getInstance().getUserService().get(player.getName());

                context.setItem(new ItemBuilder(value.getItem().clone())
                        .addLore(CratePlugin.getInstance().getInventories().getConfig().getStringList("user-reward.item.lore").stream().map(str -> str.replace("&", "§").replace("{amount}", CratePlugin.FORMATTER.formatNumber(user.getRewards().get(value)))).collect(Collectors.toList()))
                        .build());

            }).onClick(click -> {
                Player player = click.getPlayer();
                User user = CratePlugin.getInstance().getUserService().get(player.getName());
                CratePlugin.getInstance().getCrateController().giveReward(user, value);
                click.closeForPlayer();
                CratePlugin.getInstance().getViewFrame().open(UserRewardInventory.class, player);
            }))
            .build();

    @Override
    public void onInit(ViewConfigBuilder config) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();

        config
                .title(configuration.getString("user-reward.title").replace("&", "§"))
                .size(configuration.getInt("user-reward.size"))
                .layout(configuration.getStringList("user-reward.layout").toArray(new String[0]))
                .cancelOnClick()
                .cancelOnDrag()
                .cancelOnDrop()
                .cancelOnPickup()
                .build();

    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
        Pagination pagination = rewardState.get(render);

        render.slot(configuration.getInt("user-reward.empty.slot"), new ItemBuilder(Material.getMaterial(configuration.getString("user-reward.empty.material")), 1, (short) configuration.getInt("user-reward.empty.data"))
                        .setDisplayName(configuration.getString("user-reward.empty.name"))
                        .build())
                .watch(rewardState)
                .displayIf(() -> pagination.getComponents().isEmpty());

        render.layoutSlot('C', new ItemBuilder(Material.getMaterial(configuration.getString("user-reward.close.material")), 1, (short) configuration.getInt("user-reward.close.data"))
                .setDisplayName(configuration.getString("user-reward.close.name"))
                .build()
        ).onClick(click -> CratePlugin.getInstance().getViewFrame().open(CrateGeneralInventory.class, click.getPlayer()));

        render.layoutSlot('<', new ItemBuilder(Material.getMaterial(configuration.getString("user-reward.back.material")), 1, (short) configuration.getInt("user-reward.back.data"))
                        .setDisplayName(configuration.getString("user-reward.back.name"))
                        .build())
                .watch(rewardState)
                .displayIf(pagination::canBack)
                .onClick(pagination::back);

        render.layoutSlot('>', new ItemBuilder(Material.getMaterial(configuration.getString("user-reward.advance.material")), 1, (short) configuration.getInt("user-reward.advance.data"))
                        .setDisplayName(configuration.getString("user-reward.advance.name"))
                        .build())
                .watch(rewardState)
                .displayIf(pagination::canAdvance)
                .onClick(pagination::advance);

    }
}
