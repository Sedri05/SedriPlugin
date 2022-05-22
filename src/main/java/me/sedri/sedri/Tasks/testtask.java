package me.sedri.sedri.Tasks;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class testtask extends BukkitRunnable {
    SedriPlugin plugin;

    public testtask(SedriPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        //System.out.println("test test! Thanks");
    }
}
