package me.sedri.sedri;

import me.sedri.sedri.Commands.Gui.TestGuiCommand;
import me.sedri.sedri.Commands.Misc.PvpToggle;
import me.sedri.sedri.Commands.Misc.SudeCommand;
import me.sedri.sedri.Commands.ReforgeCommand;
import me.sedri.sedri.Commands.Reload;
import me.sedri.sedri.Commands.items.*;
import me.sedri.sedri.Commands.testcommand;
import me.sedri.sedri.Listeners.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class SedriPlugin extends JavaPlugin{
    private static SedriPlugin plugin;
    private static Economy econ = null;
    public ArrayList<Player> worldgamemodechange = new ArrayList<>();
    public HashMap<Player, ArrayList<String>> distance = new HashMap<>();

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
        if (p != null) {
            worldgamemodechange.add(p);
        }
        readyEvents();
        readyCommands();
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
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
        //m.registerEvents(new EnchantListener(), this);
        //m.registerEvents(new AnvilListener(), this);
        //m.registerEvents(new PlayerHealthListener(), this);
    }

    private void readyCommands(){
        Objects.requireNonNull(getCommand("shortbow")).setExecutor(new shortbow());
        Objects.requireNonNull(getCommand("keepgamemode")).setExecutor(new WorldChangeGameMode());
        Objects.requireNonNull(getCommand("tpbow")).setExecutor(new TpBow());
        Objects.requireNonNull(getCommand("tpstick")).setExecutor(new TransmissionStick());
        Objects.requireNonNull(getCommand("swordofthestars")).setExecutor(new SwordOfTheStars());
        Objects.requireNonNull(getCommand("maxhealpot")).setExecutor(new FullHealPotion());
        //Objects.requireNonNull(getCommand("gui")).setExecutor(new TestGuiCommand());
        //Objects.requireNonNull(getCommand("sedrireload")).setExecutor(new Reload());
        Objects.requireNonNull(getCommand("pvp")).setExecutor(new PvpToggle());
        //Objects.requireNonNull(getCommand("test")).setExecutor(new testcommand());
        Objects.requireNonNull(getCommand("ssudo")).setExecutor(new SudeCommand());
        Objects.requireNonNull(getCommand("hyperion")).setExecutor(new HyperionCommand());
        Objects.requireNonNull(getCommand("rreforge")).setExecutor(new ReforgeCommand());
        Objects.requireNonNull(getCommand("beamrod")).setExecutor(new BeamRod());
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
