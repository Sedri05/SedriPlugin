package me.sedri.sedri.Listeners;

import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
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

import javax.annotation.Nullable;
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
                else if (id.equals("beam")){
                    e.setCancelled(true);
                    Location loc = p.getEyeLocation();
                    Location modloc = p.getEyeLocation();
                    Vector dir = loc.getDirection();
                    Vector moddir = modloc.getDirection();
                    double bana = Math.toRadians(p.getEyeLocation().getYaw() + 90 + 45);
                    modloc = p.getLocation().add(Math.cos(bana) * .5, 1.5, Math.sin(bana) * .5);
                    moddir = modloc.getDirection();
                    Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 1);
                    p.getWorld().spawnParticle(Particle.REDSTONE, modloc, 1, dust);
                    for (double i = 0; i<30; i += 0.5) {
                        moddir.normalize().multiply(0.5);
                        modloc.add(moddir);
                        moddir = modloc.getDirection();
                        p.getWorld().spawnParticle(Particle.REDSTONE, modloc, 1, dust);
                    }
                    /*for (int i = 0; i < 40; i++) {
                        dir.normalize().multiply(0.5);
                        loc.add(dir);
                        dir = loc.getDirection();
                        p.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dust);
                    }*/
                    double ab = Math.toRadians(p.getEyeLocation().getYaw() + 90 + 45 * (e.getHand() == EquipmentSlot.HAND ? 1 : -1));
                    loc = p.getLocation().add(Math.cos(ab) * .5, 1.5, Math.sin(ab) * .5);
                    loc.add(loc.getDirection().normalize().multiply(0.5));
                    @Nullable RayTraceResult result = loc.getWorld().rayTrace(loc, p.getEyeLocation().getDirection(), 30, FluidCollisionMode.NEVER,  true,0.2, null);
                    LivingEntity hitEntity = result == null || result.getHitEntity() == null ? null : (LivingEntity) result.getHitEntity();
                    if (hitEntity != null){
                        hitEntity.damage(50);
                    }
                    result.getHitPosition().toLocation(p.getWorld());

                    /*RayTraceResult result = p.getWorld().rayTraceEntities(p.getEyeLocation(),
                            p.getEyeLocation().getDirection(),
                            20, 3);
                    if (result.getHitEntity() instanceof LivingEntity ent){
                        ent.damage(10);
                    }*/
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
                while (trace != null && d != 0) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public  void WorldGuardPvPEvent(DisallowedPVPEvent e){
        if (SedriPlugin.getPlugin().pvpallowed.contains(e.getAttacker())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlockEvent(BlockPlaceEvent e){
        if (e.getBlock().getType() != Material.BARRIER) return;
        if (e.getPlayer().hasPermission("sedri.place.barrier")) return;
        e.setCancelled(true);
    }
}
