package me.sedri.sedri.Listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.Map;

public class EnchantListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent e){
        if (e.getEnchantBlock().getBlockData().getMaterial().equals(Material.ENCHANTING_TABLE)) {
            Map<Enchantment, Integer> enchants = e.getEnchantsToAdd();
            if (enchants.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL) || enchants.containsKey(Enchantment.PROTECTION_EXPLOSIONS) || enchants.containsKey(Enchantment.PROTECTION_FIRE) || enchants.containsKey(Enchantment.PROTECTION_PROJECTILE) || enchants.containsKey(Enchantment.ARROW_DAMAGE)) {
                enchants.remove(Enchantment.PROTECTION_ENVIRONMENTAL);
                enchants.remove(Enchantment.PROTECTION_EXPLOSIONS);
                enchants.remove(Enchantment.PROTECTION_FIRE);
                enchants.remove(Enchantment.PROTECTION_PROJECTILE);
                enchants.remove(Enchantment.ARROW_DAMAGE);
                enchants.put(Enchantment.DURABILITY, 3);
            }
        }
    }
}
