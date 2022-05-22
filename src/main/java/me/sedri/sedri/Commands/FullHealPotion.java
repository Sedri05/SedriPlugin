package me.sedri.sedri.Commands;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FullHealPotion implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p && p.hasPermission("sedri.maxhealpot")) {
            ItemStack stick = new ItemStack(Material.SPLASH_POTION, 1);
            ItemMeta meta = stick.getItemMeta();
            if (meta == null) {
                return false;
            }
            meta.setDisplayName(ChatColor.LIGHT_PURPLE +  "Health Kit");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.LIGHT_PURPLE + "A magical potion that will heal everything it hit!");
            lore.add(ChatColor.DARK_GRAY + "Heal to max health.");
            meta.setLore(lore);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING, "fullheal");
            stick.setItemMeta(meta);
            p.getInventory().addItem(stick);
            return true;
        }
        return false;
    }
}
