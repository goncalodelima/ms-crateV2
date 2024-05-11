package com.minecraftsolutions.freecrate.inventory;

import com.minecraftsolutions.freecrate.CratePlugin;
import com.minecraftsolutions.freecrate.model.user.User;
import com.minecraftsolutions.freecrate.model.user.service.UserFoundationService;
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

import java.util.stream.Collectors;

public class CrateGeneralInventory extends View {

    private final State<ItemStack> openAllState = computedState(context -> {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
        UserFoundationService userService = CratePlugin.getInstance().getUserService();
        Player player = context.getPlayer();
        User user = userService.get(player.getName());
        int allCrates = user.getCrates().values().stream().mapToInt(Integer::intValue).sum();

        return new ItemBuilder(Material.getMaterial(configuration.getString("general.open.material")), configuration.getInt("general.open.amount"), (short) configuration.getInt("general.open.data"))
                .setSkull(configuration.getString("general.open.url"))
                .setDisplayName(configuration.getString("general.open.name"))
                .setLore(configuration.getStringList("general.open.lore").stream().map(str -> str.replace("{amount}", CratePlugin.FORMATTER.formatNumber(allCrates))).collect(Collectors.toList()))
                .glow(configuration.getBoolean("general.open.glow"))
                .build();

    });

    private final State<ItemStack> topState = computedState(context -> {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();

        return new ItemBuilder(Material.getMaterial(configuration.getString("general.top.material")), configuration.getInt("general.top.amount"), (short) configuration.getInt("general.top.data"))
                .setSkull(configuration.getString("general.top.url"))
                .setDisplayName(configuration.getString("general.top.name"))
                .setLore(configuration.getStringList("general.top.lore"))
                .glow(configuration.getBoolean("general.top.glow"))
                .build();

    });

    private final State<ItemStack> filterState = computedState(context -> {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
        UserFoundationService userService = CratePlugin.getInstance().getUserService();
        Player player = context.getPlayer();
        User user = userService.get(player.getName());

        return new ItemBuilder(Material.getMaterial(configuration.getString("general.filter.material")), configuration.getInt("general.filter.amount"), (short) configuration.getInt("general.filter.data"))
                .setSkull(configuration.getString("general.filter.url"))
                .setDisplayName(configuration.getString("general.filter.name"))
                .setLore(configuration.getStringList("general.filter.lore").stream().map(str -> str.replace("{amount}", String.valueOf(user.getFilter().size()))).collect(Collectors.toList()))
                .glow(configuration.getBoolean("general.filter.glow"))
                .build();

    });

    private final State<Pagination> crateState = buildPaginationState(CratePlugin.getInstance().getCrateService().getAll())
            .layoutTarget('C')
            .itemFactory((itemBuilder, value) -> itemBuilder.onRender(context -> {

                FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
                UserFoundationService userService = CratePlugin.getInstance().getUserService();
                Player player = context.getPlayer();
                User user = userService.get(player.getName());
                int amount = user.getCrates().getOrDefault(value, 0);

                context.setItem(new ItemBuilder(value.getItem().clone())
                        .addLore(configuration.getStringList("general.crate.lore").stream().map(str -> str.replace("&", "§").replace("{amount}", CratePlugin.FORMATTER.formatNumber(amount))).collect(Collectors.toList()))
                        .build());

            }).onClick(click -> {
                click.closeForPlayer();
                CratePlugin.getInstance().getViewFrame().open(CrateRewardInventory.class, click.getPlayer(), value);
            }))
            .build();

    @Override
    public void onInit(ViewConfigBuilder config) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();

        config
                .title(configuration.getString("general.title").replace("&", "§"))
                .size(configuration.getInt("general.size"))
                .layout(configuration.getStringList("general.layout").toArray(new String[0]))
                .cancelOnClick()
                .cancelOnDrag()
                .cancelOnDrop()
                .cancelOnPickup()
                .build();

    }

    @Override
    public void onFirstRender(RenderContext render) {

        FileConfiguration configuration = CratePlugin.getInstance().getInventories().getConfig();
        UserFoundationService userService = CratePlugin.getInstance().getUserService();

        render.layoutSlot('L', new ItemBuilder(Material.getMaterial(configuration.getString("general.lock.material")), configuration.getInt("general.lock.amount"), (short) configuration.getInt("general.lock.data"))
                .setSkull(configuration.getString("general.lock.url"))
                .setDisplayName(configuration.getString("general.lock.name"))
                .setLore(configuration.getStringList("general.lock.lore"))
                .glow(configuration.getBoolean("general.lock.glow"))
                .build());

        render.layoutSlot('T').onRender(r -> r.setItem(topState.get(render))).onClick(click -> {

            Player clickPlayer = click.getPlayer();
            click.closeForPlayer();
            CratePlugin.getInstance().getViewFrame().open(CrateRankingInventory.class, clickPlayer);

        });

        render.layoutSlot('F').onRender(r -> r.setItem(filterState.get(render))).onClick(click -> {

                    Player clickPlayer = click.getPlayer();
                    click.closeForPlayer();
                    CratePlugin.getInstance().getViewFrame().open(UserFilterInventory.class, clickPlayer);

        });

        render.layoutSlot('A').onRender(r -> r.setItem(openAllState.get(render))).onClick(click -> {

            Player clickPlayer = click.getPlayer();
            User clickUser = userService.get(clickPlayer.getName());

            CratePlugin.getInstance().getCrateController().openAll(clickUser);
            click.update();

        });

        render.layoutSlot('R', new ItemBuilder(Material.getMaterial(configuration.getString("general.reward.material")), configuration.getInt("general.reward.amount"), (short) configuration.getInt("general.reward.data"))
                .setSkull(configuration.getString("general.reward.url"))
                .setDisplayName(configuration.getString("general.reward.name"))
                .setLore(configuration.getStringList("general.reward.lore"))
                .glow(configuration.getBoolean("general.reward.glow"))
                .build()).onClick(click -> {

            Player clickPlayer = click.getPlayer();

            click.closeForPlayer();
            CratePlugin.getInstance().getViewFrame().open(UserRewardInventory.class, clickPlayer);
        });

        crateState.get(render).getComponents().forEach(render::addComponent);
    }
}
