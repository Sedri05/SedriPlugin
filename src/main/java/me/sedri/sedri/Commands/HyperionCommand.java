package me.sedri.sedri.Commands;

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

public class HyperionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        if (!p.hasPermission("sedri.hyperion"))return false;
        ItemStack stick = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta meta = stick.getItemMeta();
        if (meta == null) return false;
        Multimap<Attribute, AttributeModifier> attr = ArrayListMultimap.create();
        attr.put(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 260, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        meta.setAttributeModifiers(attr);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Hyperion"));
        meta.setAttributeModifiers(attr);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Gear score: &d615"));
        lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+260");
        lore.add(ChatColor.GRAY + "Strenght: " + ChatColor.RED + "+160");
        lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+350");
        lore.add(ChatColor.GRAY + "Fercocity: " + ChatColor.GREEN + "+30");
        lore.add("");
        lore.add(TACC("&7Deals +&a50%&7 damage to"));
        lore.add(TACC("&7Withers. Grants &c+1 Damage"));
        lore.add(TACC("&7and &a+2 &bIntelligence"));
        lore.add(TACC("&7per &cCatacombs level."));
        lore.add("");
        lore.add(TACC("&6Item Ability: Wither Impact &e&lRIGHT CLICK"));
        lore.add(TACC("&7Teleports &a10 blocks&7 ahead of you;"));
        lore.add(TACC("&7Then implode dealing &c20000&7 damage"));
        lore.add(TACC("&7to nearby enemies. Also applies the"));
        lore.add(TACC("&6wither shield&7 scroll ability reducing"));
        lore.add(TACC("&7damage taken and granting an"));
        lore.add(TACC("&6Absorpsion &7shield for&e 5&7 seconds."));
        lore.add(TACC("&8Mana Cost: &3300"));
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "This item can be reforged!");
        lore.add(TACC("&4&l! &cRequires &aCatacombs Floor VII"));
        lore.add(ChatColor.GRAY + "Completion");
        lore.add(TACC("&6&lLEGENDARY DUNGEON SWORD"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING, "hyperion");
        stick.setItemMeta(meta);
        p.getInventory().addItem(stick);
        return false;
    }

    private String TACC(String str){
        return SedriPlugin.TACC(str);
    }
}
