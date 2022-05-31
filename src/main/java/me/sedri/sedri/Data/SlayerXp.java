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
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levelist == null) return;
        if(level >= levelist.size()) return;
        SlayerLevel lvl = SedriPlugin.getPlugin().Levels.get(slayer).get((int)level);
        if (lvl == null) return;
        Player p = SedriPlugin.getPlugin().getServer().getPlayer(this.uuid);
        this.level++;
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
    public void decrementLevel(){
        if (level == 0) return;
        level--;
        SlayerLevel lvl = SedriPlugin.getPlugin().Levels.get(slayer).get((int)level);
        if (lvl == null) return;
        Player p = SedriPlugin.getPlugin().getServer().getPlayer(this.uuid);
        if (p != null) {
            for (String perm : lvl.getPermissions()) {
                SedriPlugin.getPlugin().removePermission(uuid, perm);
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
        ArrayList<Integer> levellist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levellist == null) return;
        while (level < levellist.size() && this.xp + xp >= levellist.get((int) level)) {
            xp = xp - levellist.get((int) level);
            this.xp = 0;
            incrementLevel();
        }
        this.xp += xp;
    }
    public void removeXp(int xp){
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levelist == null) return;
        while (this.xp - xp < 0) {
            if (level == 0) {
                this.xp = 0;
                return;
            }
            int maxxp = levelist.get((int) level - 1);
            decrementLevel();
            xp -= this.xp;
            this.xp = maxxp;
        }
        this.xp -= xp;
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

    public String getBar(){
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levelist == null) return "UNIDENTIFIED - ERROR";
        if(level >= levelist.size()) return "";
        Integer lvlxp = levelist.get((int) level);
        String str = "&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍&a▍";
        for (int i = 0; i<=xp/lvlxp*25; i++){
            str = str.replaceFirst("&a▍", "&2▍");
        }
        return str;
    }
    public String getPercent(){
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levelist == null) return "UNIDENTIFIED - ERROR";
        if(level >= levelist.size()) {
            Integer lvlxp = levelist.get((int) level-1);
            return Math.round(1.0*(xp+lvlxp)/lvlxp*100) + "%";
        }
        Integer lvlxp = levelist.get((int) level);
        return Math.round(1.0*(xp)/lvlxp*100) + "%";
    }

    public String getNextLevel(){
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levelist == null) return "UNIDENTIFIED - ERROR";
        if(level >= levelist.size())return "∞";
        return Math.round(level+1)+"";
    }

    public Boolean reachedMaxLevel(){
        ArrayList<Integer> levelist = SedriPlugin.getPlugin().LevelList.get(slayer);
        if (levelist == null) return null;
        return level >= levelist.size();
    }
}
