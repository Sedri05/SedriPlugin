package me.sedri.sedri.Listeners;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class WorldChangeListener implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        GameMode gamemode = p.getGameMode();
        if (SedriPlugin.getPlugin().worldgamemodechange.contains(p) && p.hasPermission("sedri.keepgamemode")){
            //Bukkit.getScheduler().runTaskLater(SedriPlugin.getPlugin(), () -> p.setGameMode(gamemode), 5);
            new BukkitRunnable(){
                @Override
                public void run() {
                    p.setGameMode(gamemode);
                }
            }.runTaskLater(SedriPlugin.getPlugin(), 5);
        }
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (p.getUniqueId().equals(UUID.fromString("0b0172c6-e10f-49dc-9f27-c9cf12e9ed7b"))){
            SedriPlugin.getPlugin().worldgamemodechange.add(p);
        }
    }
}
