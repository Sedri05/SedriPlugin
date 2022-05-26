package me.sedri.sedri.Gui;

import me.sedri.sedri.Data.SlayerData;
import me.sedri.sedri.Data.SlayerLevel;
import me.sedri.sedri.Data.SlayerXp;
import me.sedri.sedri.Data.SlayerXpStorage;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class MainSlayerGui implements Listener {
    private Inventory inv;
    public String menu = "main";
    private final SedriPlugin plugin;
    private String[] currentmenu;

    private boolean reward = false;

    private final Player p;

    public MainSlayerGui(Player p) {
        this.plugin = SedriPlugin.getPlugin();
        this.p = p;
        inv = Bukkit.createInventory(p, 54, ChatColor.RED + "Slayer Gui");
        PluginManager m = plugin.getServer().getPluginManager();

        m.registerEvents(this, SedriPlugin.getPlugin());
        initializeItems();
    }
    public MainSlayerGui(Player p, String me) {
        this.plugin = SedriPlugin.getPlugin();
        this.p = p;
        menu = me;
        inv = Bukkit.createInventory(p, 54, ChatColor.RED + "Slayer Gui");
        PluginManager m = plugin.getServer().getPluginManager();
        m.registerEvents(this, SedriPlugin.getPlugin());
        initializeItems();
    }
    public void initializeItems() {
        inv.clear();
        if (menu == null || menu.equals("main")) {
            inv = Bukkit.createInventory(p, 54, ChatColor.RED + "Slayer Gui");
            inv.setContents(plugin.mainslayermenu);
            openInventory();
            return;
        } else if (reward){
            ItemStack[] stack = new ItemStack[inv.getSize()];
            Arrays.fill(stack, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
            inv.setContents(stack);
            inv.setItem(inv.getSize()-5, createGuiItem(Material.BARRIER, "&cBack"));
            Set<String> keys = plugin.Levels.keySet();
            int i = 10;
            ArrayList<SlayerLevel> levels = plugin.Levels.get(menu);
            for (SlayerLevel lvl: levels) {
                ArrayList<String> rewards = lvl.getRewards();
                String[] str = new String[rewards.size()]; // = String[rewards.size()];
                int j = 0;
                for (String rew : rewards) {
                    str[j] = rew;
                    j++;
                }
                inv.setItem(i, createGuiItem(Material.GOLD_INGOT, "&6Level " + (i - 9), str));
                i++;
            }
            for (; i < 17; i++) {
                inv.setItem(i, createGuiItem(Material.COAL, "&4No Level", "&7This Level is", "&7not available."));
            }
            return;
        }
        inv = Bukkit.createInventory(p, 45, ChatColor.RED + "Slayer Gui");
        ItemStack[] stack = new ItemStack[inv.getSize()];
        Arrays.fill(stack, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inv.setContents(stack);
        inv.setItem(inv.getSize()-5, createGuiItem(Material.BARRIER, "&cBack"));
        Set<String> keys = plugin.slayersubmenu.keySet();
        int i = 10;
        currentmenu = new String[inv.getSize()];
        for (String key: keys){
            plugin.getLogger().info(key);
            String[] id = key.split(":");
            if (id[0].equalsIgnoreCase(menu)){
                if (i == 17) break;
                inv.setItem(i, plugin.slayersubmenu.get(key));
                currentmenu[i] = key;
                i++;
            }
        }
        for (; i < 17; i++) {
            inv.setItem(i, createGuiItem(Material.COAL, "&4No Boss", "&7This boss is", "&7not available."));
        }
        SlayerXp player = SlayerXpStorage.createPlayer(p, menu);
        int plvl = (int) player.getLevel();
        int pxp = (int) p.getExp();
        int maxlvl = 0;
        int maxxp = 0;
        try {
            ArrayList<Integer> lvllist = plugin.LevelList.get(menu);
            maxlvl = lvllist.size();
            maxxp = lvllist.get(plvl);
        } catch (NullPointerException e){
            plugin.getLogger().warning("No level list has been set for " + menu);
        }
        inv.setItem(29, createGuiItem(Material.DIAMOND_SWORD, "&bSlayer Info", "&aLevel: "+plvl+"&e/"+maxlvl,
                "&aExp: "+pxp+"&e/"+maxxp));
        inv.setItem(33, createGuiItem(Material.GOLD_BLOCK, "&6Rewards", " ", "&aClick for more info"));
        openInventory();
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        ArrayList<String> lore2 = new ArrayList<>();
        for(String line: lore){
            lore2.add(ChatColor.translateAlternateColorCodes('&',line));
        }
        // Set the lore of the item
        meta.setLore(lore2);
        item.setItemMeta(meta);

        return item;
    }

    public void openInventory() {
        p.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        if(e.getRawSlot() >= inv.getSize() && e.getClick() != ClickType.DOUBLE_CLICK) return;
        final Player p = (Player) e.getWhoClicked();
        p.sendMessage(e.getRawSlot() + "");
        if (menu == null || menu.equals("main")){
            if(plugin.slayermenuindex.containsKey(e.getRawSlot())){
                menu = plugin.slayermenuindex.get(e.getRawSlot());
                initializeItems();
            }
        } else {
            if (e.getRawSlot() == inv.getSize()-5) {
                if (reward){
                    reward = false;
                } else {
                    menu = "main";
                }
                initializeItems();
            } else if(e.getRawSlot() == 33){
                reward = true;
                initializeItems();
            } else {
                String s = currentmenu[e.getRawSlot()];
                if (plugin.allSlayers.containsKey(s)) {
                    SlayerData data = new SlayerData(plugin.allSlayers.get(s));
                    if (!plugin.activeSlayer.containsKey(p)) {
                        plugin.activeSlayer.put(p, data);
                        p.closeInventory();
                        String name = data.getName();
                        String str = "&cYou have started a " + name;
                        ArrayList<String> ls = data.getDescription();
                        p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.STRIKETHROUGH + "============================");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
                        for (String st: ls){
                            if (ChatColor.stripColor(st).equals("Click to start")) continue;
                            p.sendMessage(st);
                        }
                        p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.STRIKETHROUGH + "============================");
                    } else {
                        plugin.activeSlayer.remove(p);
                        p.sendMessage(ChatColor.RED + "You're previous Slayer has been canceled.");
                    }
                }
            }
        }
        p.updateInventory();
        e.setCancelled(true);
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            for (Integer slot: e.getRawSlots()){
                if (slot < inv.getSize()) {
                    e.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (e.getInventory().equals(inv)) {
            return;
            /*ItemStack item = e.getInventory().getItem(13);
            if (item == null) return;
            Player p = (Player) e.getPlayer();
            p.getInventory().addItem(item);*/
        }
    }
}
