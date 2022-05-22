package me.sedri.sedri.Listeners;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ArrowHitListener implements Listener {

    @EventHandler
    public void OnArrowLand(ProjectileHitEvent e){

        if (e.getEntity().getShooter() instanceof Player player){
            if (e.getEntity() instanceof Arrow) {
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                if (meta != null){
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    String id = data.get(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING);
                    if (id != null && id.equals("tpbow") && player.hasPermission("sedri.tpbow")) {
                        Location location = e.getEntity().getLocation();
                        location.setDirection(player.getLocation().getDirection());
                        player.teleport(location);

                    }
                }
            }
        }
    }
}
