package me.sedri.sedri.Commands;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ReforgeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p && p.hasPermission("sedri.reforge")) {
            if (args.length == 0) return false;
            if (!p.hasPermission("sedri.applereforge")) return false;
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return false;
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(SedriPlugin.getPlugin(), "reforge"), PersistentDataType.STRING, args[0]);
            item.setItemMeta(meta);
        } else if (sender instanceof ConsoleCommandSender c){
            c.sendMessage("hello");
        }
        sender.sendMessage("oi");
        return false;
    }
}
