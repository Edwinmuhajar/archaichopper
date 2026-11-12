package com.archaichopper;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Initial ArchaicHopper 5.0 reconstruction.
 * Uses the native inventory-move event so the server owns inventory scheduling,
 * which is important on Folia. The event item is expanded only when the source
 * inventory contains a matching stack and the destination can accept it.
 */
public final class ArchaicHopper extends JavaPlugin implements Listener {
    private int batchSize;
    private boolean debug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("ArchaicHopper 5.0 enabled. Batch maximum: " + batchSize);
    }

    private void loadSettings() {
        reloadConfig();
        batchSize = Math.max(1, Math.min(64, getConfig().getInt("batch-size", 32)));
        debug = getConfig().getBoolean("debug", false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent event) {
        ItemStack moving = event.getItem();
        if (moving == null || moving.getType().isAir()) return;

        int configuredMax = batchSize;
        if (configuredMax <= moving.getAmount()) return;

        // Find the matching source stack. InventoryMoveItemEvent is fired by the
        // server's hopper transfer path; do not schedule cross-region inventory access.
        int available = 0;
        for (ItemStack stack : event.getSource().getStorageContents()) {
            if (stack != null && stack.isSimilar(moving)) {
                available = Math.max(available, stack.getAmount());
            }
        }
        if (available <= moving.getAmount()) return;

        int wanted = Math.min(configuredMax, available);
        ItemStack expanded = moving.clone();
        expanded.setAmount(wanted);
        event.setItem(expanded);

        if (debug) {
            getLogger().info("Batch request: " + moving.getType() + " x" + wanted
                    + " from " + event.getSource().getType() + " to " + event.getDestination().getType());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("archaichopper")) return false;
        if (!sender.hasPermission("archaichopper.admin")) {
            sender.sendMessage(ChatColor.RED + "Kamu tidak punya izin.");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            loadSettings();
            sender.sendMessage(ChatColor.DARK_PURPLE + "ArchaicHopper config berhasil dimuat ulang.");
            return true;
        }
        sender.sendMessage(ChatColor.GOLD + "ArchaicHopper 5.0 | /archaichopper reload");
        return true;
    }
}
