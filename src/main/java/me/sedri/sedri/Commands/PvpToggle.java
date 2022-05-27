package me.sedri.sedri.Commands;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PvpToggle implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p){
            if (!p.hasPermission("sedri.togglepvp")) return false;
            if (SedriPlugin.getPlugin().pvpallowed.contains(p)){
                SedriPlugin.getPlugin().pvpallowed.remove(p);
            } else {
                SedriPlugin.getPlugin().pvpallowed.add(p);
            }
        }

        return false;
    }
}
