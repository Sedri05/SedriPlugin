package me.sedri.sedri;

import me.sedri.sedri.Commands.*;
import me.sedri.sedri.Data.SlayerConfig;
import me.sedri.sedri.Data.SlayerData;
import me.sedri.sedri.Data.SlayerLevel;
import me.sedri.sedri.Data.SlayerXpStorage;
import me.sedri.sedri.Listeners.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Array;
import java.util.*;


public final class SedriPlugin extends JavaPlugin{
    private static SedriPlugin plugin;
    public ArrayList<Player> worldgamemodechange = new ArrayList<>();
    public HashMap<Player, SlayerData> activeSlayer = new HashMap<>();
    public LinkedHashMap<String, SlayerData> allSlayers = new LinkedHashMap<>();
    public HashMap<Player, ArrayList<String>> distance = new HashMap<>();
    public ItemStack[] mainslayermenu = new ItemStack[54];
    public ArrayList<ItemStack> slayermenu;
    public HashMap<String, ArrayList<Integer>> LevelList = new HashMap<>();
    public HashMap<String, ArrayList<SlayerLevel>> Levels = new HashMap<>();
    public LinkedHashMap<Integer, String> slayermenuindex = new LinkedHashMap<>();
    public LinkedHashMap<String, ItemStack> slayersubmenu2 = new LinkedHashMap<>();

    public static void setTransmissionDefault(Player p){
        ArrayList<String> setvalues = new ArrayList<>();
        setvalues.add("8");
        setvalues.add("true");
        SedriPlugin.getPlugin().distance.put(p, setvalues);
    }
    public static SedriPlugin getPlugin(){
        return plugin;
    }
    @Override
    public void onEnable() {
        plugin = this;
        Player p = this.getServer().getPlayer(UUID.fromString("0b0172c6-e10f-49dc-9f27-c9cf12e9ed7b"));
        try {
            SlayerXpStorage.loadPlayerSlayerXp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (p != null) {
            worldgamemodechange.add(p);
        }
        readyEvents();
        readyCommands();
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        if (!getFile().exists()) {
            saveResource("slayers.yml", false);
        }
        SlayerConfig.setup();
        readySlayers();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            SlayerXpStorage.savePlayerSlayerXp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PersistentDataContainer getDataMainHand(Player p){
        ItemStack item = p.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer();
        }
        return null;
    }
    private void readyEvents(){
        PluginManager m = getServer().getPluginManager();
        m.registerEvents(new PlayerInteractListener(), this);
        m.registerEvents(new ChatListener(), this);
        m.registerEvents(new WorldChangeListener(), this);
        m.registerEvents(new ArrowHitListener(), this);
        m.registerEvents(new SplashPotionListener(), this);
        m.registerEvents(new EnchantListener(), this);
    }

    private void readyCommands(){
        Objects.requireNonNull(getCommand("shortbow")).setExecutor(new shortbow());
        Objects.requireNonNull(getCommand("keepgamemode")).setExecutor(new WorldChangeGameMode());
        Objects.requireNonNull(getCommand("tpbow")).setExecutor(new TpBow());
        Objects.requireNonNull(getCommand("tpstick")).setExecutor(new TransmissionStick());
        Objects.requireNonNull(getCommand("swordofthestars")).setExecutor(new SwordOfTheStars());
        Objects.requireNonNull(getCommand("maxhealpot")).setExecutor(new FullHealPotion());
        //getCommand("gui").setExecutor(new TestGuiCommand(testinv));
        Objects.requireNonNull(getCommand("gui")).setExecutor(new TestGuiCommand());
        Objects.requireNonNull(getCommand("slayergui")).setExecutor(new SlayerGuiCommand());
        Objects.requireNonNull(getCommand("sedrireload")).setExecutor(new Reload());
    }

    public void readySlayers(){
        slayermenu = new ArrayList<>();
        mainslayermenu = new ItemStack[54];
        slayermenuindex = new LinkedHashMap<>();
        slayersubmenu2 = new LinkedHashMap<>();
        allSlayers = new LinkedHashMap<>();
        ItemStack fillitem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        fillitem.getItemMeta().setDisplayName("");
        Arrays.fill(mainslayermenu, fillitem);
        Set<String> keys = SlayerConfig.get().getKeys(false);
        int i = 10;
        for (String key: keys){
            ConfigurationSection slayer = SlayerConfig.get().getConfigurationSection(key);
            Material mat = Material.ZOMBIE_HEAD;
            try {
                String mate = slayer.getString("material");
                mat = Material.valueOf(mate.toUpperCase());
            } catch (IllegalArgumentException e){
                getLogger().warning("Invalid material set in " + key);
            }
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            try {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', slayer.getString("name")));
            } catch (NullPointerException e){
                meta.setDisplayName(key);
            }
            List<String> lorelist = slayer.getStringList("description");
            ArrayList<String> lore = new ArrayList<>();
            for (String loreline : lorelist) {
                lore.add(ChatColor.translateAlternateColorCodes('&', loreline));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            if (i == 17 || i == 26 || i == 35){
                i+=2;
            } else if (i >= 44){
                break;
            }
            mainslayermenu[i] = item;
            slayermenu.add(item);
            slayermenuindex.put(i, key);
            i++;
            HashMap<EntityType, Integer> mobs = new HashMap<>();
            List<String> moblist = slayer.getStringList("mob-list");
            for (String mob: moblist) {
                String[] mobs2 = mob.split(":");
                try {
                    EntityType mob2 = EntityType.valueOf(mobs2[0].toUpperCase());
                    mobs.put(mob2, Integer.parseInt(mobs2[1]));
                } catch (IllegalArgumentException e){
                    getLogger().warning("Invalid mob defined in " + key);
                }
            }
            ConfigurationSection tiers = slayer.getConfigurationSection("tiers");
            if (tiers == null) {
                getLogger().warning("No tiers defined in " + key);
                continue;
            }
            Set<String> tierkeys = tiers.getKeys(false);
            for (String tierkey: tierkeys){
                ConfigurationSection tier = tiers.getConfigurationSection(tierkey);
                mat = Material.ZOMBIE_HEAD;
                try {
                    String mate = tier.getString("material");
                    mat = Material.valueOf(mate.toUpperCase());
                } catch (NullPointerException e){
                    getLogger().warning("Invalid material set in " + tierkey + "in "+ key);
                }
                item = new ItemStack(mat);
                meta = item.getItemMeta();
                try {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', tier.getString("name")));
                } catch (NullPointerException e){
                    meta.setDisplayName(tierkey);
                }
                lorelist = tier.getStringList("description");
                lore = new ArrayList<>();
                for (String loreline : lorelist) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreline));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
                EntityType type = null;
                try {
                    String mob = tier.getString("boss");
                    type = EntityType.valueOf(mob.toUpperCase());
                } catch (IllegalArgumentException e){
                    getLogger().warning("Invalid boss mob set in "+ tierkey + " in " + key);
                }
                Integer max_xp = tier.getInt("required-xp");
                Integer reward_xp = tier.getInt("reward-xp");
                String id = key + ":" + tierkey;
                if (type != null) {
                    slayersubmenu2.put(id, item);
                    SlayerData data = new SlayerData(mobs, type, max_xp, reward_xp, key);
                    allSlayers.put(id, data);
                }
            }
            ConfigurationSection levels = slayer.getConfigurationSection("levels");
            if (levels == null) {
                getLogger().warning("No tiers defined in " + key);
                continue;
            }
            Set<String> levelkeys = levels.getKeys(false);
            ArrayList<Integer> levelist = new ArrayList<>();
            for (String levelkey: levelkeys) {
                levelist.add(Integer.parseInt(levelkey));

            }
            LevelList.put(key, levelist);
        }
    }

    public void addPermission(UUID uuid, String permission) {
        // Add the permission
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            user.data().add(Node.builder(permission).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }
}
