package me.sedri.sedri.Listeners;

import me.sedri.sedri.Data.SlayerData;
import me.sedri.sedri.Data.SlayerXp;
import me.sedri.sedri.Data.SlayerXpStorage;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class PlayerInteractListener implements Listener {

    private final SedriPlugin plugin = SedriPlugin.getPlugin();

    @EventHandler
    public void playerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();

        if (!(a.equals(Action.PHYSICAL)) && p.hasPermission("sedri.tpstick")){
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null){
                PersistentDataContainer data = meta.getPersistentDataContainer();
                String id = data.get(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING);
                if (id != null && id.equals("tpstick")) {
                    int d = 8;
                    boolean c = true;
                    if (SedriPlugin.getPlugin().distance.containsKey(p)) {
                        d = Integer.parseInt(SedriPlugin.getPlugin().distance.get(p).get(0));
                        c = Boolean.parseBoolean(SedriPlugin.getPlugin().distance.get(p).get(1));
                    }
                    Location loc = p.getLocation();
                    Vector dir = loc.getDirection();

                    RayTraceResult trace = p.rayTraceBlocks(d);
                    while (trace != null && c){
                        d--;
                        trace = p.rayTraceBlocks(d);
                    }
                    dir.normalize();
                    dir.multiply(d);
                    loc.add(dir);
                    Location loc2 = loc;
                    loc2.setY(loc2.getY()-1);
                    if (loc2.getBlock().getBlockData().getMaterial() != Material.AIR){
                        loc.setY(loc.getY()+2);
                    }
                    if (d == 0 && c) {
                        p.sendMessage(ChatColor.DARK_GRAY + "You can't teleport there!");
                    } else {
                        p.teleport(loc);
                    }
                    e.setCancelled(true);
                }
            }
        }
        if (a.equals(Action.LEFT_CLICK_AIR) || a.equals(Action.LEFT_CLICK_BLOCK) && p.hasPermission("sedri.shorbow")){
            PersistentDataContainer data = SedriPlugin.getDataMainHand(p);
            if (data == null){
                return;
            }

            String id = data.get(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING);
            if (id != null && id.equals("shortbow")) {
                Arrow arrow = p.launchProjectile(Arrow.class);
                arrow.setDamage(100);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerKillEvent(EntityDeathEvent e){
        Player p = e.getEntity().getKiller();
        if (p == null) return;
        if (plugin.activeSlayer.containsKey(p)){
            SlayerData slayer = plugin.activeSlayer.get(p);
            if (!slayer.isBossSpawned()) {
                Integer xp = slayer.getMobs().get(e.getEntity().getType());
                if (xp != null) {
                    slayer.addXp(xp);
                }
                if (slayer.reachedMaxXp()) {
                    slayer.setBossSpawned(true);
                    EntityType bosstype = slayer.getBoss();
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour " + slayer.getName() + " &cis spawning!"));
                    e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), bosstype);
                }
            } else {
                p.sendMessage(slayer.getBoss().toString());
                if (e.getEntity().getType().equals(slayer.getBoss())){
                    String tier = slayer.getTier().split(":")[0];
                    SlayerXp slayerplayer = SlayerXpStorage.createPlayer(p, tier);
                    slayerplayer.addXp(slayer.getReward());
                    String bar = slayerplayer.getBar();
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lSLAYER DEFEATED"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have gained &e" + slayer.getReward() + " " +slayer.getSlayername() + " &aXP!"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', bar));
                    plugin.activeSlayer.remove(p);
                    SlayerXpStorage.updatePlayerSlayerXp(slayerplayer);
                }
            }
        }
    }

    @EventHandler
    public void playerHitEvent(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player p){
            if (e.getEntity() instanceof  Player ent){
                if (SedriPlugin.getPlugin().pvpallowed.contains(p)){
                    e.setCancelled(false);
                }
            }
        }
    }
}
