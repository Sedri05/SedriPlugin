package me.sedri.sedri.Data;

import me.sedri.sedri.SedriPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class SlayerData{
    private final HashMap<EntityType, Integer> mobs;
    private final EntityType boss;
    private boolean bossSpawned = false;
    private final int max_xp;
    private final int reward;
    private int xp = 0;
    private final String tier;
    private final String slayername;
    private final String name;
    private final ArrayList<String> description;
    private String perm = null;
    private double money = 0;
    private BossBar bossBar;

    public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc, String perm, Double money){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.perm = perm;
        this.money = money;
        this.slayername = slayername;
    }
    public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.slayername = slayername;
    }
    public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc, String perm){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.perm = perm;
        this.slayername = slayername;
    }
    public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc, Double money){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.money = money;
        this.slayername = slayername;
    }

    public SlayerData(SlayerData data, Player p){
        this.mobs = data.getMobs();
        this.boss = data.getBoss();
        this.max_xp = data.getMax_xp();
        this.reward = data.getReward();
        this.bossSpawned = data.isBossSpawned();
        this.xp = data.getXp();
        this.tier = data.getTier();
        this.name = data.getName();
        this.description = data.getDescription();
        this.perm = data.getPerm();
        this.money = data.getMoney();
        this.bossBar = data.getBossBar();
        this.slayername = data.getSlayername();
        initBossBar(p);
    }

    public String getTier() {
        return tier;
    }

    public HashMap<EntityType, Integer> getMobs() {
        return mobs;
    }

    public EntityType getBoss() {
        return boss;
    }

    public int getReward() {
        return reward;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }

    public void setBossSpawned(Boolean bossSpawned){
        this.bossSpawned = bossSpawned;
    }

    public int getMax_xp() {
        return max_xp;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int xp_to_add){
        xp = xp + xp_to_add;
        updateBossBar();
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void setToMaxXp(){
        xp = max_xp;
    }
    public String getName() {
        return name;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public String getSlayername() {
        return slayername;
    }

    public String getPerm() {
        return perm;
    }

    public double getMoney() {
        return money;
    }

    public boolean canStart(Player p){
        if (perm != null) {
            if (!p.hasPermission(perm)) return false;
        }
        if (money > 0) {
            Economy econ = SedriPlugin.getEconomy();
            if (!(econ.getBalance(p) >= money)) return false;
            econ.withdrawPlayer(p, money);
        }
        return true;
    }

    public boolean reachedMaxXp(){
        if (xp >= max_xp){
            bossBar.setVisible(false);
            bossBar.removeAll();
            bossBar = null;
            return true;
        }
        return false;
    }

    public void initBossBar(Player p){
        bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', name + ": &c" + xp + " &4/ " + max_xp), BarColor.YELLOW, BarStyle.SOLID);
        bossBar.addPlayer(p);
        bossBar.setProgress(0);
        bossBar.setVisible(true);
    }
    public void updateBossBar(){
        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', name + ": &c" + xp + " &4/ " + max_xp));
        bossBar.setProgress(xp*1D/max_xp);
    }
}
