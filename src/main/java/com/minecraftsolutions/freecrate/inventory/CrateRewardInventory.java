package com.minecraftsolutions.freecrate.inventory;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.crate.Crate;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class CrateRewardInventory extends View {

    private final State<ItemStack> openCrateState = computedState(context -> {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
        Player player = context.getPlayer();
        User user = CratePlugin.getInstance().getUserService().get(player.getName());
        int crates = user.getCrates().getOrDefault((Crate) context.getInitialData(), 0);

        return new ItemBuilder(Material.getMaterial(configuration.getString("reward.open.material")), configuration.getInt("reward.open.amount"), (short) configuration.getInt("reward.open.data"))
                .setSkull(configuration.getString("reward.open.url"))
                .setDisplayName(configuration.getString("reward.open.name"))
                .setLore(configuration.getStringList("reward.open.lore").stream().map(str -> str.replace("{amount}", CratePlugin.FORMATTER.formatNumber(crates))).collect(Collectors.toList()))
                .build();

    });

    private final State<ItemStack> collectCrateState = computedState(context -> {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
        Player player = context.getPlayer();
        User user = CratePlugin.getInstance().getUserService().get(player.getName());
        int crates = user.getCrates().getOrDefault((Crate) context.getInitialData(), 0);

        return new ItemBuilder(Material.getMaterial(configuration.getString("reward.collect.material")), configuration.getInt("reward.collect.amount"), (short) configuration.getInt("reward.collect.data"))
                .setSkull(configuration.getString("reward.collect.url"))
                .setDisplayName(configuration.getString("reward.collect.name"))
                .setLore(configuration.getStringList("reward.collect.lore").stream().map(str -> str.replace("{amount}", CratePlugin.FORMATTER.formatNumber(crates))).collect(Collectors.toList()))
                .build();

    });

    private final State<Pagination> rewardState = computedPaginationState(
            (context -> ((Crate) context.getInitialData()).getRewards()),
            (context, itemBuilder, i, value) -> {

                FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
                Player player = context.getPlayer();
                User user = CratePlugin.getInstance().getUserService().get(player.getName());

                itemBuilder.withItem(new ItemBuilder(value.getItem().clone())
                                .addLore(user.getFilter().contains(value) ? configuration.getStringList("reward.in-filter").stream().map(str -> str.replace("&", "§")).collect(Collectors.toList()) : configuration.getStringList("reward.out-filter").stream().map(str -> str.replace("&", "§")).collect(Collectors.toList()))
                                .build())
                        .onClick(click -> {

                            if (user.getFilter().contains(value)) user.getFilter().remove(value);
                            else user.getFilter().add(value);

                            CratePlugin.getInstance().getUserService().update(user);
                            click.update();
                        });

            }
    );

    @Override
    public void onInit(ViewConfigBuilder config) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();

        config
                .title(configuration.getString("reward.title").replace("&", "§"))
                .size(configuration.getInt("reward.size"))
                .layout(configuration.getStringList("reward.layout").toArray(new String[0]))
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

        rewardState.get(render).getComponents().forEach(render::addComponent);

        render.layoutSlot('C', new ItemBuilder(Material.getMaterial(configuration.getString("reward.close.material")), 1, (short) configuration.getInt("reward.close.data"))
                .setDisplayName(configuration.getString("reward.close.name"))
                .build()
        ).onClick(click -> CratePlugin.getInstance().getViewFrame().open(CrateGeneralInventory.class, click.getPlayer()));

        render.layoutSlot('<', new ItemBuilder(Material.getMaterial(configuration.getString("reward.back.material")), 1, (short) configuration.getInt("reward.back.data"))
                        .setDisplayName(configuration.getString("reward.back.name"))
                        .build())
                .watch(rewardState)
                .displayIf(pagination::canBack)
                .onClick(pagination::back);

        render.layoutSlot('>', new ItemBuilder(Material.getMaterial(configuration.getString("reward.advance.material")), 1, (short) configuration.getInt("reward.advance.data"))
                        .setDisplayName(configuration.getString("reward.advance.name"))
                        .build())
                .watch(rewardState)
                .displayIf(pagination::canAdvance)
                .onClick(pagination::advance);

        render.layoutSlot('A').onRender(r -> r.setItem(openCrateState.get(render))).onClick(click -> {

            Player clickPlayer = click.getPlayer();
            User clickUser = CratePlugin.getInstance().getUserService().get(clickPlayer.getName());
            int clickCrates = clickUser.getCrates().getOrDefault((Crate) render.getInitialData(), 0);

            if (click.isLeftClick() && clickCrates >= 64)
                CratePlugin.getInstance().getCrateController().open(clickUser, (Crate) render.getInitialData(), 64);
            else if (click.isRightClick() && clickCrates >= 1)
                CratePlugin.getInstance().getCrateController().open(clickUser, (Crate) render.getInitialData(), 1);
            else if (click.isKeyboardClick() && click.getClickIdentifier().equals("DROP") && clickCrates > 0)
                CratePlugin.getInstance().getCrateController().open(clickUser, (Crate) render.getInitialData(), clickCrates);

            click.update();
        });

        render.layoutSlot('R').onRender(r -> r.setItem(collectCrateState.get(render))).onClick(click -> {

            Player clickPlayer = click.getPlayer();
            User clickUser = CratePlugin.getInstance().getUserService().get(clickPlayer.getName());
            int clickCrates = clickUser.getCrates().getOrDefault((Crate) render.getInitialData(), 0);

            if (click.isLeftClick() && clickCrates >= 64)
                CratePlugin.getInstance().getCrateController().giveCrate(clickPlayer, (Crate) render.getInitialData(), 64, false, true);
            else if (click.isRightClick() && clickCrates >= 1)
                CratePlugin.getInstance().getCrateController().giveCrate(clickPlayer, (Crate) render.getInitialData(), 1, false, true);
            else if (click.isKeyboardClick() && click.getClickIdentifier().equals("DROP") && clickCrates > 0)
                CratePlugin.getInstance().getCrateController().giveCrate(clickPlayer, (Crate) render.getInitialData(), clickCrates, false, true);

            click.update();
        });

    }
}
