package me.sedri.sedri.Commands;

import me.sedri.sedri.Data.SlayerConfig;
import me.sedri.sedri.Data.SlayerXpStorage;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("sedri.reload")) return false;
            SlayerConfig.reload();
            SedriPlugin.getPlugin().readySlayers();
            try {
                SlayerXpStorage.savePlayerSlayerXp();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            SlayerConfig.reload();
            SedriPlugin.getPlugin().readySlayers();
            try {
                SlayerXpStorage.savePlayerSlayerXp();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

}
