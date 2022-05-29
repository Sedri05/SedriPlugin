package me.sedri.sedri.Commands;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class testcommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get("zombie_slayer");
        p.sendMessage(levelist.size()+1 + "");

        return false;
    }
}
