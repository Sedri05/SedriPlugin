package me.sedri.sedri.Listeners;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static org.bukkit.Bukkit.dispatchCommand;

public class ChatListener implements Listener {
    @EventHandler
    public void chatevent(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        String text = event.getMessage();
        if (text.startsWith("#") && player.hasPermission("sedri.scshortcut")) {
            String finalText = text.replaceFirst("#", "");
            Bukkit.getServer().getScheduler().runTask(SedriPlugin.getPlugin(), () -> {
                dispatchCommand(player, "staffchat " + finalText);
            });
            event.setCancelled(true);
        }
    }

}
