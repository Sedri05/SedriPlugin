package me.sedri.sedri.Data;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class SlayerXpStorage {
    private static HashMap<String, ArrayList<SlayerXp>> SlayerList = new HashMap<>();

    public static SlayerXp createPlayer(Player p, String slayer){
        createSlayerType(slayer);
        ArrayList<SlayerXp> playerxp = SlayerList.get(slayer);
        for (SlayerXp xp: playerxp){
            if (xp.getUuid().equals(p.getUniqueId())){
                return xp;
            }
        }

        SlayerXp PlayerXp = new SlayerXp(p.getUniqueId(), slayer);
        playerxp.add(PlayerXp);
        SlayerList.put(slayer, playerxp);
        return PlayerXp;
    }

    public static void createSlayerTypes(ArrayList<String> slayers){
        for (String slayer: slayers){
            if (!SlayerList.containsKey(slayer)) {
                SlayerList.put(slayer, new ArrayList<>());
            }
        }
    }
    public static void createSlayerType(String slayer){
        if (SlayerList.containsKey(slayer)) return;
        SlayerList.put(slayer, new ArrayList<>());
    }
    public static void updatePlayerSlayerXp(SlayerXp playerxp){
        String slayer = playerxp.getSlayer();
        createSlayerType(slayer);
        ArrayList<SlayerXp> playerlist = SlayerList.get(slayer);
        int i = 0;
        for (SlayerXp player: playerlist){
            if (player.getUuid().equals(playerxp.getUuid())){
                //playerlist.set(i, playerxp);
                updatePlayerUpdateSub(slayer, i, playerxp);
                return;
            }
            i++;
        }
    }
    private static void updatePlayerUpdateSub(String slayer, int i,  SlayerXp player){
        SlayerList.get(slayer).set(i, player);
    }
    public static void savePlayerSlayerXp() throws IOException {
        Gson gson = new Gson();
        File file = new File(SedriPlugin.getPlugin().getDataFolder().getAbsolutePath() + "/notes.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        gson.toJson(SlayerList, writer);
        writer.flush();
        writer.close();
        System.out.println("Notes saved.");
    }
    public static void loadPlayerSlayerXp() throws IOException {
        Gson gson = new Gson();
        File file = new File(SedriPlugin.getPlugin().getDataFolder().getAbsolutePath() + "/notes.json");
        if (file.exists()){
            Reader reader = new FileReader(file);
            SlayerList = new HashMap<>();
            HashMap n = gson.fromJson(reader, HashMap.class);
            if (n == null) return;
            Set<String> keys = n.keySet();
            for (String key: keys){
                ArrayList<SlayerXp> ea = new ArrayList<>();
                ArrayList<LinkedTreeMap> apple = (ArrayList) n.get(key);
                for (LinkedTreeMap eas: apple){
                    ea.add(new SlayerXp(UUID.fromString((String) eas.get("uuid")), (String) eas.get("slayer"), (float) (double) eas.get("xp"), (float) (double) eas.get("level")));
                }
                SlayerList.put(key, ea);
            }
            Bukkit.getLogger().info("Notes loaded.");
        }
    }
}
