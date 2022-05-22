package me.sedri.sedri.Commands;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TpBow implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p && p.hasPermission("sedri.tpbow")){
            ItemStack tpbow = new ItemStack(Material.BOW, 1);
            ItemMeta meta = tpbow.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bTeleport Bow"));
                PersistentDataContainer data = meta.getPersistentDataContainer();
                data.set(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING, "tpbow");
                tpbow.setItemMeta(meta);
                p.getInventory().addItem(tpbow);
            }
            /*if (args.length > 0){
                StringBuilder message = new StringBuilder();
                for (String arg: args){
                    message.append(arg).append(" ");
                }
                ItemStack item = p.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                if (meta == null){ return false; }
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if (data.has(new NamespacedKey(SedriPlugin.getPlugin(), "message"), PersistentDataType.STRING)){
                    p.sendMessage("Message: " + data.get(new NamespacedKey(SedriPlugin.getPlugin(), "message"), PersistentDataType.STRING));
                } else {
                    data.set(new NamespacedKey(SedriPlugin.getPlugin(), "message"), PersistentDataType.STRING, message.toString());
                    item.setItemMeta(meta);
                    p.sendMessage("The stored message is now: " + message);
                }
            }*/
        }

        return true;
    }
}
