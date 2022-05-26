package me.sedri.sedri.Data;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class SlayerXp {
    private float xp = 0;
    private float level = 0;
    private String slayer;
    private UUID uuid;

    public SlayerXp(UUID uuid, String slayer, float xp, float level) {
        this.uuid = uuid;
        this.slayer = slayer;
        this.xp = xp;
        this.level = level;
    }
    public SlayerXp(UUID uuid, String slayer) {
        this.uuid = uuid;
        this.slayer = slayer;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void incrementLevel(){
        this.level++;
        SlayerLevel lvl = SedriPlugin.getPlugin().Levels.get(slayer).get((int)level);
        Player p = SedriPlugin.getPlugin().getServer().getPlayer(this.uuid);
        if (p != null) {
            p.sendMessage("You have leveled up to level" + this.level);
            for (String perm: lvl.getPermissions()){
                SedriPlugin.getPlugin().addPermission(uuid, perm);
            }
            for (String command: lvl.getCommands()){
                command = command.replace("%player%", p.getName());
                ConsoleCommandSender cons = Bukkit.getServer().getConsoleSender();
                Bukkit.dispatchCommand(cons, command);
            }
        }
    }

    public float getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int xp){
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levelist == null) return;
        if(level >= levelist.size()+1)return;

        if (this.xp + xp >= levelist.get((int) level)) {
            incrementLevel();
            this.xp = this.xp + xp - levelist.get((int) level);
        } else {
            this.xp += xp;
        }
    }
    public String getSlayer() {
        return slayer;
    }

    public void setSlayer(String slayer) {
        this.slayer = slayer;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
