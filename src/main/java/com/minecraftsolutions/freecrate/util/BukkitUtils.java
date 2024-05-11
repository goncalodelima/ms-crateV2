package com.minecraftsolutions.freecrate.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class BukkitUtils {

    public static List<Location> getArea(Location location1, Location location2){

        List<Location> area = new ArrayList<>();

        double xMin = Math.min(location1.getX(), location2.getX());
        double yMin = Math.min(location1.getY(), location2.getY());
        double zMin = Math.min(location1.getZ(), location2.getZ());
        double xMax = Math.max(location1.getX(), location2.getX());
        double yMax = Math.max(location1.getY(), location2.getY());
        double zMax = Math.max(location1.getZ(), location2.getZ());

        for (int x = (int) xMin; x <= xMax; x++)
            for (int y = (int) yMin; y <= yMax; y++)
                for (int z = (int) zMin; z <= zMax; z++)
                    area.add(new Location(location1.getWorld(), x, y, z));

        return area;
    }

    public static Object getObjectChance(HashMap<Object, Double> objects) {
        double chanceSum = 0.0;
        for (double c : objects.values())
            chanceSum += c;

        double random = new Random().nextDouble() * chanceSum;

        double accumulator = 0.0;
        for (Map.Entry<Object, Double> obj : objects.entrySet()) {
            accumulator += obj.getValue();
            if (random < accumulator) // Correção aqui (usar < em vez de <=)
                return obj.getKey();
        }

        return null;
    }

    public static String generateProgressBar(int barSize, double percentage, char character) {

        percentage = percentage < 0 ? 0 : percentage > 1 ? 1 : percentage;
        int progressChars = (int) Math.floor(percentage * barSize);
        int emptyChars = barSize - progressChars;

        StringBuilder progressBar = new StringBuilder();

        for (int i = 0; i < progressChars; i++)
            progressBar.append("§a").append(character);

        for (int i = 0; i < emptyChars; i++)
            progressBar.append("§7").append(character);

        return progressBar.toString();
    }

    public static void sendTitle(Player player, String titleText, String subTitleText, int fadeIn, int stay, int fadeOut){
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + titleText + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");
        IChatBaseComponent chatSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subTitleText + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle subTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);

    }

    public static void sendActionBar(Player p, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendBroadcastActionBar(String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte) 2);
        for (Player p : Bukkit.getServer().getOnlinePlayers())
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public static boolean isEmptyInventory(Inventory inventory){
        return Arrays.stream(inventory.getContents()).noneMatch(Objects::nonNull);
    }

    public static boolean isPlayerInventoryEmpty(PlayerInventory inventory){
        return isEmptyInventory(inventory) && Arrays.stream(inventory.getArmorContents()).noneMatch(item -> item != null && item.getType() != Material.AIR);
    }

    public static int getItemsCountNotNull(ItemStack[] items){
        return (int) Arrays.stream(items).filter(Objects::nonNull).count();
    }

    public static int getItemsCountIsNull(ItemStack[] items){
        return (int) Arrays.stream(items).filter(Objects::isNull).count();
    }

    public static int getInventorySpace(Inventory inventory){
        return (int) Arrays.stream(inventory.getContents()).filter(Objects::isNull).count();
    }

    public static boolean hasSpace(Inventory inventory, ItemStack[] items){
        return getInventorySpace(inventory) >= getItemsCountNotNull(items);
    }

    public static String formatSeconds(int seconds){

        int days = seconds / 86400;
        seconds %= 86400;
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;

        return (days != 0 ? days + "d " : "") + (hours != 0 ? hours + "h " : "") + (minutes != 0 ? minutes + "m " : "") + (seconds != 0 ? seconds + "s" : "");
    }

}
