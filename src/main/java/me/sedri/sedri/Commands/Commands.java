package me.sedri.sedri.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class Commands implements CommandExecutor {
    private final HashMap<UUID, Long> cooldown;

    public Commands(){
        this.cooldown = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player p){
            long timeElapled = System.currentTimeMillis() - cooldown.get(p.getUniqueId());
            if (!this.cooldown.containsKey(p.getUniqueId()) || timeElapled >= 10000) {
                this.cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                p.sendMessage("OH NO ur gay LLLLLLLL");
            } else {
                float time2 = Math.round(timeElapled/100);
                p.sendMessage("NAH BRO DONT THINK YOU GET AWAY WITH THIS  " + (100 - time2)/10);

            }
        }
        return true;
    }
}
