package me.sedri.sedri.Commands;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class testcommand implements CommandExecutor {
    HashMap<Player, BukkitRunnable> runnable = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        if (runnable.containsKey(p)){
            BukkitRunnable r = runnable.get(p);
            r.cancel();
            runnable.remove(p);
        } else {
            BukkitRunnable r = new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    p.sendMessage(i+"");
                    i++;
                }
            };
            r.runTaskTimerAsynchronously(SedriPlugin.getPlugin(), 0, 1);
            runnable.put(p, r);
        }
        return true;
    }
}
