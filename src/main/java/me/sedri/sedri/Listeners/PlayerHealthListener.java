package me.sedri.sedri.Listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Objects;

public class PlayerHealthListener implements Listener {

    @EventHandler //(priority = EventPriority.LOWEST)
    public void onNaturalRegen(EntityRegainHealthEvent e){
        if (e.getEntity() instanceof Player p){
            p.sendMessage(e.getRegainReason().toString());
            if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM) return;
            double phealth = Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            double regen = phealth/100;
            e.setAmount(regen);
        }
    }
}
