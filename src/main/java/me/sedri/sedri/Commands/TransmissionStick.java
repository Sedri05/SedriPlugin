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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransmissionStick implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player p && p.hasPermission("sedri.tpstick")) {
            if (args.length == 0) {

                ItemStack stick = new ItemStack(Material.STICK, 1);
                ItemMeta meta = stick.getItemMeta();
                if (meta == null) {
                    return false;
                }
                if (!SedriPlugin.getPlugin().distance.containsKey(p)) {
                    SedriPlugin.setTransmissionDefault(p);
                }

                Multimap<Attribute, AttributeModifier> attr = ArrayListMultimap.create();
                attr.put(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 1000000, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bTransmission Stick"));
                meta.setAttributeModifiers(attr);
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', "&c&oTeleports you a default of &a8 &c&oblocks forward!"));
                lore.add(ChatColor.translateAlternateColorCodes('&', "&c&oChange this with /tpstick distance <int>."));
                meta.setLore(lore);
                PersistentDataContainer data = meta.getPersistentDataContainer();
                data.set(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING, "tpstick");
                stick.setItemMeta(meta);
                p.getInventory().addItem(stick);
            } else {
                if (!SedriPlugin.getPlugin().distance.containsKey(p)){
                    SedriPlugin.setTransmissionDefault(p);
                }
                ArrayList<String> setdistance = SedriPlugin.getPlugin().distance.get(p);
                switch (args[0]){
                    case "collision":
                        if (Boolean.parseBoolean(SedriPlugin.getPlugin().distance.get(p).get(1))) {
                            setdistance.set(1, "false");
                            p.sendMessage(ChatColor.RED + "Collision is now set to False");
                        } else {
                            setdistance.set(1, "true");
                            p.sendMessage(ChatColor.GREEN + "Collision is not set to True");
                        }
                        break;
                    case "distance":
                        if (args.length > 1){
                            setdistance.set(0, args[1]);
                            p.sendMessage(ChatColor.GREEN + "Transmission distance set to " + ChatColor.RED + args[1]);
                        } else {
                            p.sendMessage(ChatColor.RED + "Please give a distance");
                        }
                        break;
                }
                SedriPlugin.getPlugin().distance.put(p, setdistance);
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0].equals("distance")){
            return Arrays.asList("5", "10", "15", "30");
        } else if (args[0].equals("collision")){
            return List.of();
        }
        return Arrays.asList("collision", "distance");
    }
}
