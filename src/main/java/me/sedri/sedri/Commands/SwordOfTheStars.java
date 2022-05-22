package me.sedri.sedri.Commands;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class SwordOfTheStars implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        if (sender instanceof Player p){
            if (!p.hasPermission("sedri.swordofthestars")){
                return false;
            }
            ItemStack item = new ItemStack(Material.WOODEN_SWORD, 1);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return false;
            }
            meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            Multimap<Attribute, AttributeModifier> attr = ArrayListMultimap.create();
            attr.put(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",Double.MAX_VALUE, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Sword Of The Stars"));
            meta.setAttributeModifiers(attr);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&3A sword forged out a literal star"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&3"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7When in Main Hand:"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&9+1.79*10^308 Attack Damage"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
        }
        return false;
    }
}
