package me.sedri.sedri.Commands.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SpiritSceptreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        if (!p.hasPermission("sedri.spiritsceprtre")) return false;
        ItemStack stick = new ItemStack(Material.ALLIUM, 1);
        ItemMeta meta = stick.getItemMeta();
        if (meta == null) return false;
        Multimap<Attribute, AttributeModifier> attr = ArrayListMultimap.create();
        attr.put(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        meta.setAttributeModifiers(attr);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Spirit Sceptre"));
        meta.setAttributeModifiers(attr);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Gear score: &d460"));
        lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+180");
        lore.add("");
        lore.add(ChatColor.RED + "Intelligence: " + ChatColor.GREEN + "+300");
        lore.add("");
        lore.add(TACC("&6Item Ability: Guided Bat &e&lRIGHT CLICK"));
        lore.add(TACC("&7Shoots a spirit guided bat,"));
        lore.add(TACC("&7following your aim and exploding"));
        lore.add(TACC("&7for &c2000&7 damage."));
        lore.add(TACC("&8Mana Cost: &b250"));
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "This item can be reforged!");
        lore.add(TACC("&6&lLEGENDARY DUNGEON SWORD"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING, "spiritsceptre");
        stick.setItemMeta(meta);
        p.getInventory().addItem(stick);

        return false;
    }

    public String TACC(String str){
        return SedriPlugin.TACC(str);
    }

    public static void activate(Player p){
        LivingEntity bat = (LivingEntity) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.BAT);
        ((Bat) bat).setAwake(true);
        bat.setInvulnerable(true);
        bat.setAI(false);
        bat.setGravity(false);
        bat.getLocation().setDirection(p.getLocation().getDirection());
        new BukkitRunnable(){
            int ticks = 0;
            double distance = 0;
            @Override
            public void run() {
                if (ticks%3==0){
                    Vector pLookingAt;
                    distance = p.getLocation().distance(bat.getLocation())+20;
                    double ab = Math.toRadians(p.getEyeLocation().clone().getYaw() + 90 + 45);
                    Location loc = p.getLocation().clone().add(Math.cos(ab) * .5, 1.5, Math.sin(ab) * .5);
                    loc.add(loc.getDirection().normalize().multiply(0.5));
                    RayTraceResult result = p.getWorld().rayTrace(loc, loc.getDirection(), distance, FluidCollisionMode.NEVER, true, 0.2, null);
                    if (result == null){
                        pLookingAt = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(distance)).toVector();
                    } else {
                        Entity hitEntity = result.getHitEntity();
                        if (hitEntity == null || !hitEntity.getType().equals(EntityType.BAT)) {
                            pLookingAt = result.getHitPosition();
                            pLookingAt.add(pLookingAt.clone().normalize().multiply(0.5));
                        } else {
                            pLookingAt = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(distance)).toVector();
                        }
                    }
                    Vector vec = pLookingAt.clone().subtract(bat.getLocation().toVector());
                    bat.teleport(bat.getLocation().setDirection(vec));
                    /*distance = 50; // p.getLocation().distance(bat.getLocation())+5;
                    Location pLookingAt = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(distance));
                    bat.teleport(bat.getLocation().setDirection(pLookingAt.toVector().subtract(bat.getLocation().toVector())));*/
                }
                Collection<LivingEntity> entities = bat.getLocation().getNearbyLivingEntities(0.7);
                boolean canExplode = false;
                for (Entity entity: entities){
                    if (!entity.equals(p) && !entity.getType().equals(EntityType.BAT)) {
                        canExplode = true;
                        break;
                    }
                }
                Block block = bat.getLocation().getBlock();
                if (!(block.getType() == Material.AIR || block.getType() == Material.WATER)){
                    canExplode = true;
                }
                if (canExplode || ticks>200){
                    Collection<Entity> ents = p.getWorld().getNearbyEntities(bat.getLocation(), 4.5, 4.5, 4.5);
                    for (Entity entity : ents) {
                        if (entity.equals(p)) continue;
                        if (entity.getType().equals(EntityType.BAT)) continue;
                        if (!(entity instanceof LivingEntity)) continue;
                        ((LivingEntity) entity).damage(2000);
                    }
                    p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, bat.getLocation(), 10);
                    p.getWorld().playSound(bat.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.15f, 1);
                    bat.remove();
                    cancel();
                } else {
                    bat.teleport(bat.getLocation().add(bat.getLocation().getDirection().normalize().multiply(0.7)));
                }
                ticks++;
            }
        }.runTaskTimer(SedriPlugin.getPlugin(), 0 ,1);
    }
}
