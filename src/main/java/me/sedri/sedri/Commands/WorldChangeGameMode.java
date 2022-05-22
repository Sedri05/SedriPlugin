package me.sedri.sedri.Commands;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WorldChangeGameMode implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p){
            if (p.hasPermission("sedri.keepgamemode")) {
                if (SedriPlugin.getPlugin().worldgamemodechange.contains(p)) {
                    SedriPlugin.getPlugin().worldgamemodechange.remove(p);

                    p.sendMessage(ChatColor.RED + "Your gamemode will now no longer be remembered on world change");
                } else {
                    SedriPlugin.getPlugin().worldgamemodechange.add(p);
                    p.sendMessage(ChatColor.GREEN + "Your gamemode will now be remembered on world change");
                }
            } else {
                p.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
            }
        }
        return true;
    }
}
