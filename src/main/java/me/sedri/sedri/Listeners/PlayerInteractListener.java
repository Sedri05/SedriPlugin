package me.sedri.sedri.Listeners;

import me.sedri.sedri.Data.SlayerData;
import me.sedri.sedri.Data.SlayerXp;
import me.sedri.sedri.Data.SlayerXpStorage;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class PlayerInteractListener implements Listener {

    private final SedriPlugin plugin = SedriPlugin.getPlugin();

    private ArrayList<Player> reduceDamage = new ArrayList<>();

    private ArrayList<Player> canHyp = new ArrayList<>();

    @EventHandler
    public void playerInteract(PlayerInteractEvent e){
        int k = 0;
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (e.getHand() == EquipmentSlot.HAND) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer data = meta.getPersistentDataContainer();
                String id = data.get(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING);
                if (id == null) return;
                if (!(a.equals(Action.PHYSICAL))) {
                    if (id.equals("tpstick")) {
                        int d = 8;
                        boolean c = true;
                        if (SedriPlugin.getPlugin().distance.containsKey(p)) {
                            d = Integer.parseInt(SedriPlugin.getPlugin().distance.get(p).get(0));
                            c = Boolean.parseBoolean(SedriPlugin.getPlugin().distance.get(p).get(1));
                        }
                        Location loc = p.getLocation();
                        Vector dir = loc.getDirection();

                        RayTraceResult trace = p.rayTraceBlocks(d);
                        while (trace != null && c) {
                            d--;
                            trace = p.rayTraceBlocks(d);
                        }
                        dir.normalize();
                        dir.multiply(d);
                        loc.add(dir);
                        if (d == 0 && c) {
                            p.sendMessage(ChatColor.DARK_GRAY + "You can't teleport there!");
                        } else if (c){
                            loc.setX(Math.floor(loc.getX())+0.5);
                            loc.setZ(Math.floor(loc.getZ())+0.5);
                            Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
                            loc2.setY(loc2.getY() + 1);
                            if (p.getLocation().getY() > loc.getY()) {
                                if (loc2.getBlock().getBlockData().getMaterial() != Material.AIR) {
                                    loc.setY(Math.round(loc.getY() + 1));
                                }
                                if (loc.getBlock().getBlockData().getMaterial() != Material.AIR) {
                                    loc.setY(Math.round(loc.getY() + 1));
                                }
                            } else {
                                if (loc2.getBlock().getBlockData().getMaterial() != Material.AIR) {
                                    loc.setY(Math.round(loc.getY() - 2));
                                }
                            }
                            p.teleport(loc);
                        }
                    }
                }
                if ((a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK))
                        && id.equals("hyperion") && !canHyp.contains(p)) {
                    e.setCancelled(true);
                    try {
                        canHyp.add(p);
                    } finally {
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                canHyp.remove(p);
                            }
                        }.runTaskLater(plugin, 2);
                    }
                    Location loc = p.getLocation();
                    Vector dir = loc.getDirection();
                    int d = 10;
                    RayTraceResult trace = p.rayTraceBlocks(d);
                    while (trace != null) {
                        d--;
                        trace = p.rayTraceBlocks(d);
                    }
                    if (d == 0) {
                        p.sendMessage(ChatColor.DARK_GRAY + "You can't teleport there!");
                    } else {
                        dir.normalize().multiply(d);
                        loc.add(dir);
                        loc.setX(Math.floor(loc.getX())+0.5);
                        loc.setZ(Math.floor(loc.getZ())+0.5);
                        Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY()+1, loc.getZ());
                        if (p.getLocation().getY() > loc.getY()) {
                            if (loc2.getBlock().getType() != Material.AIR) {
                                loc.setY(Math.round(loc.getY() + 1));
                            }
                            if (loc.getBlock().getType() != Material.AIR) {
                                loc.setY(Math.round(loc.getY() + 1));
                            }
                        } else {
                            if (loc2.getBlock().getType() != Material.AIR) {
                                loc.setY(Math.round(loc.getY() - 2));
                                if (loc.getBlock().getType() != Material.AIR){
                                    loc.setY(loc.getY()+3);
                                }
                            }
                        }
                        p.teleport(loc);
                    }
                    Collection<Entity> entities = p.getWorld().getNearbyEntities(p.getLocation(), 4.5, 4.5, 4.5);

                    for (Entity entity : entities) {
                        if (entity.equals(p)) {
                            continue;
                        }
                        if (!(entity instanceof LivingEntity)) {
                            continue;
                        }

                        ((LivingEntity) entity).damage(20000);
                    }
                    p.spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation(), 10);
                    p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.15f, 1);
                    if (!reduceDamage.contains(p)) {
                        double health = Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                        p.setAbsorptionAmount(health / 2);
                        try {
                            reduceDamage.add(p);
                        } finally {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    double abs = p.getAbsorptionAmount() / 2;
                                    p.setAbsorptionAmount(0);
                                    try {
                                        p.setHealth(p.getHealth() + abs);
                                    } catch (IllegalArgumentException exception) {
                                        p.setHealth(p.getMaxHealth());
                                    }
                                    reduceDamage.remove(p);
                                }
                            }.runTaskLater(plugin, 100);
                        }
                    }
                }
                if (a.equals(Action.LEFT_CLICK_AIR) || a.equals(Action.LEFT_CLICK_BLOCK) && p.hasPermission("sedri.shorbow")) {
                    if (id.equals("shortbow")) {
                        Arrow arrow = p.launchProjectile(Arrow.class);
                        arrow.setDamage(100);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                        e.setCancelled(true);
                    }
                }
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
                    p.sendMessage("");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour " + slayer.getName() + " &cis spawning!"));
                    p.sendMessage("");
                    e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), bosstype);
                }
            } else {
                if (e.getEntity().getType().equals(slayer.getBoss())){
                    String tier = slayer.getTier().split(":")[0];
                    SlayerXp slayerplayer = SlayerXpStorage.createPlayer(p, tier);
                    slayerplayer.addXp(slayer.getReward());
                    SlayerXpStorage.updatePlayerSlayerXp(slayerplayer);
                    String bar = slayerplayer.getBar();
                    p.sendMessage("");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lSLAYER DEFEATED!"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have gained &e" + slayer.getReward() + " " +slayer.getSlayername() + " &aXP!"));
                    if (!slayerplayer.reachedMaxLevel()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aProgress to level " + slayerplayer.getNextLevel() + " " + bar + " &e" + slayerplayer.getPercent()));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lMAX LEVEL &e" + slayerplayer.getPercent()));
                    }
                    p.sendMessage("");
                    plugin.activeSlayer.remove(p);
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

    @EventHandler
    public void playerDamageEvent(EntityDamageEvent e){
        if (e.getEntity() instanceof Player p){
            if (reduceDamage.contains(p)){
                e.setDamage(e.getDamage()*0.9);
            }
        }
    }
}
