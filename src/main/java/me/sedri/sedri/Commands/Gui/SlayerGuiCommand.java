package me.sedri.sedri.Commands.Gui;

import me.sedri.sedri.Data.SlayerXp;
import me.sedri.sedri.Gui.MainSlayerGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SlayerGuiCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p){
            MainSlayerGui inv;
            if (args.length == 0){
                inv = new MainSlayerGui(p);
            } else {
                inv = new MainSlayerGui(p, args[0]);
            }
            inv.openInventory();
        }
        return true;
    }
}
