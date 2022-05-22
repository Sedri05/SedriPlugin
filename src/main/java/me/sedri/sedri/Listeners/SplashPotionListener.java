package me.sedri.sedri.Listeners;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class SplashPotionListener implements Listener {

    @EventHandler
    public void onSplashPotionHit(PotionSplashEvent e){
        if (e.getPotion().getShooter() instanceof Player p && p.hasPermission("sedri.maxhealpot")) {
            if (Objects.equals(Objects.requireNonNull(e.getPotion().getItem().getItemMeta()).getPersistentDataContainer().get(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING), "fullheal")) {
                for (LivingEntity ent : e.getAffectedEntities()) {
                    ent.setHealth(ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                }
            }
        }
    }
}
