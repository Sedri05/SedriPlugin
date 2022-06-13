package me.sedri.sedri.Commands.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class BeamRod implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        if (!p.hasPermission("sedri.beamrod"))return false;
        ItemStack stick = new ItemStack(Material.END_ROD, 1);
        ItemMeta meta = stick.getItemMeta();
        if (meta == null) return false;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5Beam Stick"));
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING, "beam");
        stick.setItemMeta(meta);
        p.getInventory().addItem(stick);

        return false;
    }
}
