package me.sedri.sedri.Commands.items;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class giveBarrier implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;
        Player p = Bukkit.getPlayer(args[0]);
        if (p == null) return false;
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        meta.setDisplayName(SedriPlugin.TACC("&7&kaa&c&lNULL&7kaa"));
        meta.setLore(List.of("",
                SedriPlugin.TACC("&7e&ke&7ro&4r"),
                "&7&kErr&k&7or",
                "",
                ChatColor.DARK_GRAY + "Only obtainable through the",
                ChatColor.GRAY + "ENCHANTER Quest!"));
        item.setItemMeta(meta);
        p.getInventory().addItem(item);
        return false;
    }
}
