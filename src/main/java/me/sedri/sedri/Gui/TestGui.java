package me.sedri.sedri.Gui;

import me.sedri.sedri.SedriPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class TestGui implements Listener {
    private final Inventory inv;
    private final SedriPlugin plugin = SedriPlugin.getPlugin();

    public TestGui(Player p) {
        inv = Bukkit.createInventory(p, 27);
        PluginManager m = plugin.getServer().getPluginManager();

        m.registerEvents(this, SedriPlugin.getPlugin());
        initializeItems();
    }

    public void initializeItems() {
        inv.clear();
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        }
        inv.setItem(12, createGuiItem(Material.DIAMOND_SWORD, "Example Sword", "&aFirst line of the lore", "&bSecond line of the lore"));
        inv.setItem(14, createGuiItem(Material.IRON_HELMET, "&bExample Helmet", "&aFirst line of the lore", "&bSecond line of the lore"));
        inv.setItem(13, createGuiItem(Material.IRON_SWORD, "&ea sword", "&athis is lore"));
        inv.setItem(22, createGuiItem(Material.ANVIL, "&aApply Lore", "&7Will apply some line of lore"));
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
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

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        if(e.getRawSlot() >= inv.getSize()) return;
        final Player p = (Player) e.getWhoClicked();

        if (e.getRawSlot() == 13){
            return;
        }
        if (e.getRawSlot() == 22){
            ItemStack item = e.getInventory().getItem(13);
            if (item == null) return;
            List<String> lore = item.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add("&7Added lore :)");
            item.setLore(lore);

        }
        p.updateInventory();
        e.setCancelled(true);

    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            for (Integer slot: e.getRawSlots()){
                if (slot < 27) {
                    e.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (e.getInventory().equals(inv)) {
            ItemStack item = e.getInventory().getItem(13);
            if (item == null) return;
            if (item.equals(createGuiItem(Material.IRON_SWORD, "&ea sword", "&athis is lore"))) return;
            Player p = (Player) e.getPlayer();
            p.getInventory().addItem(item);
        }
    }
}