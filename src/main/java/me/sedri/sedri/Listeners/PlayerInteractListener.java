package me.sedri.sedri.Listeners;

import me.sedri.sedri.Data.SlayerData;
import me.sedri.sedri.Data.SlayerXp;
import me.sedri.sedri.Data.SlayerXpStorage;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
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

    private ArrayList<Player> canTp = new ArrayList<>();

    @EventHandler
    public void playerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (e.getHand() == EquipmentSlot.HAND) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
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
                            loc.set(Math.floor(loc.getX())+0.5, Math.floor(loc.getY()),Math.floor(loc.getZ())+0.5);
                            Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY()+1, loc.getZ());
                            if (!((loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.WATER)
                                    && (loc2.getBlock().getType() == Material.AIR || loc2.getBlock().getType() == Material.WATER))) {
                                if (p.getLocation().getPitch() < 0) {
                                    loc.setY(loc.getY() - 1);
                                } else {
                                    loc.setY(loc.getY() + 1);
                                }
                            }
                        }
                        p.teleport(loc);

                        return;
                    }
                    if (id.equals("beam")){
                        e.setCancelled(true);
                        Location loc = p.getEyeLocation();
                        Vector dir = loc.getDirection();
                        Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 1);

                        for (int i = 0; i < 40; i++) {
                            dir.normalize().multiply(0.5);
                            loc.add(dir);
                            dir = loc.getDirection();
                            p.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dust);
                        }

                        RayTraceResult result = p.getWorld().rayTraceEntities(p.getEyeLocation(),
                                p.getEyeLocation().getDirection(),
                                20, 3);
                        if (result.getHitEntity() instanceof LivingEntity ent){
                            ent.damage(10);
                        }

                        return;
                    }
            }
            if ((a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK))
                    && id.equals("hyperion") && !canTp.contains(p)) {
                e.setCancelled(true);
                try {
                    canTp.add(p);
                } finally {
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            canTp.remove(p);
                        }
                    }.runTaskLater(plugin, 2);
                }
                Location loc = p.getEyeLocation();
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
                    loc.set(Math.floor(loc.getX())+0.5, Math.floor(loc.getY()),Math.floor(loc.getZ())+0.5);
                    Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY()+1, loc.getZ());
                    if (!((loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.WATER)
                            && (loc2.getBlock().getType() == Material.AIR || loc2.getBlock().getType() == Material.WATER))) {
                        if (p.getLocation().getPitch() < 0) {
                            loc.setY(loc.getY() - 1);
                        } else {
                            loc.setY(loc.getY() + 1);
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

                p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation(), 10);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.15f, 1);
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
                return;
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
            if (e.getEntity() instanceof  Player){
                if (SedriPlugin.getPlugin().pvpallowed.contains(p)){
                    e.setCancelled(false);
                }
            }
            if (e.isCancelled()) return;
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            PersistentDataContainer data = meta.getPersistentDataContainer();
            String id = data.get(new NamespacedKey(SedriPlugin.getPlugin(), "reforge"), PersistentDataType.STRING);
            if (id == null) return;
            if (id.equalsIgnoreCase("test")){
                e.setDamage(e.getDamage()*1.1);
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
