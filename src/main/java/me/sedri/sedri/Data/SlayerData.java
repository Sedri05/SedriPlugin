package me.sedri.sedri.Data;

import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class SlayerData{
    private final HashMap<EntityType, Integer> mobs;
    private final EntityType boss;
    private boolean bossSpawned = false;
    private final int max_xp;
    private final int reward;
    private int xp = 0;
    private final String tier;

    public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
    }

    public SlayerData(SlayerData data){
        this.mobs = data.getMobs();
        this.boss = data.getBoss();
        this.max_xp = data.getMax_xp();
        this.reward = data.getReward();
        this.bossSpawned = data.isBossSpawned();
        this.xp = data.getXp();
        this.tier = data.getTier();
    }

    public void IncrementLevel(){
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
    }

    public void setToMaxXp(){
        xp = max_xp;
    }
}
