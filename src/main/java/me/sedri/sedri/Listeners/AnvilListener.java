package me.sedri.sedri.Listeners;

import com.willfp.ecoenchants.enchantments.EcoEnchants;
import com.willfp.ecoenchants.enchantments.util.EnchantChecks;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AnvilListener implements Listener {

    private final SedriPlugin plugin = SedriPlugin.getPlugin();

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (e.getClickedInventory() instanceof AnvilInventory inv){
            if (e.getRawSlot() == 2){
                ItemStack left = inv.getFirstItem();
                ItemStack right = inv.getSecondItem();
                ItemStack result = inv.getResult();
                if (left == null || right == null || result == null || e.getCurrentItem() == null) return;
                List<String> ls = plugin.getConfig().getStringList("1-way-conflicts");
                boolean changed = false;
                for(String enchants_unsplit: ls) {
                    String[] split = enchants_unsplit.split(":");
                    NamespacedKey old = NamespacedKey.fromString(split[0]);
                    NamespacedKey nw = NamespacedKey.fromString(split   [1]);
                    if (old == null || nw == null) continue;
                    if (EnchantChecks.getEnchantsOnItem(left).containsKey(EcoEnchants.getByKey(old))
                            && EnchantChecks.getEnchantsOnItem(right).containsKey(EcoEnchants.getByKey(nw))) {
                        result.removeEnchantment(EcoEnchants.getByKey(old));
                        changed = true;
                    }
                    if (EnchantChecks.getEnchantsOnItem(left).containsKey(EcoEnchants.getByKey(nw))
                            && EnchantChecks.getEnchantsOnItem(right).containsKey(EcoEnchants.getByKey(old))) {
                        result.removeEnchantment(EcoEnchants.getByKey(old));
                        changed = true;
                    }
                }
                if (changed) {
                    e.setCurrentItem(result);
                }
            }
        }
    }
}
