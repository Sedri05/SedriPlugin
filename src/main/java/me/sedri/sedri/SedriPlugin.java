package me.sedri.sedri;

import me.sedri.sedri.Commands.*;
import me.sedri.sedri.Commands.Gui.SlayerGuiCommand;
import me.sedri.sedri.Commands.Gui.TestGuiCommand;
import me.sedri.sedri.Commands.Misc.PvpToggle;
import me.sedri.sedri.Commands.Misc.SudeCommand;
import me.sedri.sedri.Commands.items.*;
import me.sedri.sedri.Data.SlayerConfig;
import me.sedri.sedri.Data.SlayerData;
import me.sedri.sedri.Data.SlayerLevel;
import me.sedri.sedri.Data.SlayerXpStorage;
import me.sedri.sedri.Gui.MainSlayerGui;
import me.sedri.sedri.Listeners.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public final class SedriPlugin extends JavaPlugin{
    private static SedriPlugin plugin;
    private static Economy econ = null;
    public ArrayList<Player> worldgamemodechange = new ArrayList<>();
    public HashMap<Player, SlayerData> activeSlayer = new HashMap<>();
    public LinkedHashMap<String, SlayerData> allSlayers = new LinkedHashMap<>();
    public HashMap<Player, ArrayList<String>> distance = new HashMap<>();
    public ItemStack[] mainslayermenu = new ItemStack[54];
    public ArrayList<ItemStack> slayermenu;
    public HashMap<String, ArrayList<Integer>> LevelList = new HashMap<>();
    public HashMap<String, ArrayList<SlayerLevel>> Levels = new HashMap<>();
    public LinkedHashMap<Integer, String> slayermenuindex = new LinkedHashMap<>();
    public LinkedHashMap<String, ItemStack> slayersubmenu = new LinkedHashMap<>();

    public ArrayList<Player> pvpallowed = new ArrayList<>();

    public static void setTransmissionDefault(Player p){
        ArrayList<String> setvalues = new ArrayList<>();
        setvalues.add("8");
        setvalues.add("true");
        SedriPlugin.getPlugin().distance.put(p, setvalues);
    }
    public static SedriPlugin getPlugin(){
        return plugin;
    }
    public static Economy getEconomy() {
        return econ;
    }
    @Override
    public void onEnable() {
        plugin = this;
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Player p = getServer().getPlayer(UUID.fromString("0b0172c6-e10f-49dc-9f27-c9cf12e9ed7b"));
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
        saveResource("slayers.yml", false);
        SlayerConfig.setup();
        readySlayers();
        try {
            initDatabase();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            SlayerXpStorage.savePlayerSlayerXp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initDatabase() throws SQLException {
        Connection conn;
        ConfigurationSection database = getConfig().getConfigurationSection("database");
        if (database == null) return;
        String url = "jdbc:sqlite:plugins/Sedri/slayers.db";
        conn = DriverManager.getConnection(url);
        if (conn == null) {
            getLogger().severe("SQL database offline");
            return;
        }
        Statement statement = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS test(i int)";
        try {
            statement.execute(sql);
        } finally {
            statement.close();
            conn.close();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
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
        Objects.requireNonNull(getCommand("gui")).setExecutor(new TestGuiCommand());
        Objects.requireNonNull(getCommand("slayergui")).setExecutor(new SlayerGuiCommand());
        Objects.requireNonNull(getCommand("sedrireload")).setExecutor(new Reload());
        Objects.requireNonNull(getCommand("pvp")).setExecutor(new PvpToggle());
        Objects.requireNonNull(getCommand("test")).setExecutor(new testcommand());
        Objects.requireNonNull(getCommand("slayer")).setExecutor(new SlayerCommand());
        Objects.requireNonNull(getCommand("ssudo")).setExecutor(new SudeCommand());
        Objects.requireNonNull(getCommand("hyperion")).setExecutor(new HyperionCommand());
        Objects.requireNonNull(getCommand("rreforge")).setExecutor(new ReforgeCommand());
        Objects.requireNonNull(getCommand("beamrod")).setExecutor(new BeamRod());
    }

    public void readySlayers(){
        slayermenu = new ArrayList<>();
        mainslayermenu = new ItemStack[54];
        slayermenuindex = new LinkedHashMap<>();
        slayersubmenu = new LinkedHashMap<>();
        allSlayers = new LinkedHashMap<>();
        ItemStack fillitem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        fillitem.getItemMeta().setDisplayName("");
        Arrays.fill(mainslayermenu, fillitem);
        Set<String> keys = SlayerConfig.get().getKeys(false);
        mainslayermenu[mainslayermenu.length-5] = MainSlayerGui.createGuiItem(Material.BARRIER, "&cClose");
        int i = 10;
        for (String key: keys){
            SlayerCommand.keylist.add(key);
            ConfigurationSection slayer = SlayerConfig.get().getConfigurationSection(key);
            if (slayer == null) continue;
            Material mat = Material.ZOMBIE_HEAD;
            String mate = slayer.getString("material");
            if (mate != null) {
                mat = Material.valueOf(mate.toUpperCase());
            } else {
                getLogger().warning("Invalid material set in " + key);
            }
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            String slayername = slayer.getString("name");
            if (slayername == null){
                slayername = key;
            }
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', slayername));
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
                if (tier == null) continue;
                mat = Material.ZOMBIE_HEAD;
                mate = tier.getString("material");
                if (mate != null) {
                    mat = Material.valueOf(mate.toUpperCase());
                } else {
                    getLogger().warning("Invalid material set in " + tierkey + "in "+ key);
                }
                item = new ItemStack(mat);
                meta = item.getItemMeta();
                String name = tier.getString("name");
                if (name == null) {
                    name = tierkey;
                }
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                lorelist = tier.getStringList("description");
                lore = new ArrayList<>();
                for (String loreline : lorelist) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreline));
                }
                String perm = tier.getString("required-perm");
                Double money = tier.getDouble("required-coins");
                meta.setLore(lore);
                item.setItemMeta(meta);
                EntityType type = null;
                String mob = tier.getString("boss");
                if (mob != null) {
                    type = EntityType.valueOf(mob.toUpperCase());
                } else {
                    getLogger().warning("Invalid boss mob set in "+ tierkey + " in " + key);
                }
                Integer max_xp = tier.getInt("required-xp");
                Integer reward_xp = tier.getInt("reward-xp");
                String id = key + ":" + tierkey;
                if (type != null) {
                    slayersubmenu.put(id, item);
                    SlayerData data = new SlayerData(mobs, type, max_xp, reward_xp, id, name, slayername, lore, perm, money);
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
            ArrayList<SlayerLevel> slayerlevels = new ArrayList<>();
            for (String levelkey: levelkeys) {
                ConfigurationSection level = levels.getConfigurationSection(levelkey);
                if (level == null) continue;
                levelist.add(Integer.parseInt(levelkey));
                ArrayList<String> rewards = (ArrayList<String>) level.getStringList("rewards-lore");
                ArrayList<String> commands = (ArrayList<String>) level.getStringList("commands");
                ArrayList<String> permissions = (ArrayList<String>) level.getStringList("permissions");
                slayerlevels.add(new SlayerLevel(Integer.parseInt(levelkey), rewards, commands, permissions));
            }
            Levels.put(key, slayerlevels);
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

    public void removePermission(UUID uuid, String permission) {
        // remove the permission
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            user.data().remove(Node.builder(permission).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public static String TACC(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
