package me.sedri.sedri.Commands.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.sedri.sedri.SedriPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class FlowerOfTruthCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;

        if (!p.hasPermission("sedri.floweroftruth"))return false;
        ItemStack stick = new ItemStack(Material.POPPY, 1);
        ItemMeta meta = stick.getItemMeta();
        if (meta == null) return false;
        Multimap<Attribute, AttributeModifier> attr = ArrayListMultimap.create();
        attr.put(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        meta.setAttributeModifiers(attr);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Flower Of Truth"));
        meta.setAttributeModifiers(attr);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Gear score: &d460"));
        lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+100");
        lore.add(ChatColor.GRAY + "Strenght: " + ChatColor.RED + "+360");
        lore.add("");
        lore.add(TACC("&6Item Ability: Heat-Seaking Rose &e&lRIGHT CLICK"));
        lore.add(TACC("&7Shoots a rose that ricochets"));
        lore.add(TACC("&7between enemies, damaging up to"));
        lore.add(TACC("&a3 &7of your foes. Damage"));
        lore.add(TACC("&7multiplies as enemies are"));
        lore.add(TACC("&7hit."));
        lore.add(TACC("&8Cooldown: &31s"));
        lore.add(TACC("&7This mana cost of this item is"));
        lore.add(TACC("&a10% &7of your maximum mana."));
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "This item can be reforged!");
        lore.add(TACC("&6&lLEGENDARY DUNGEON SWORD"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(SedriPlugin.getPlugin(), "id"), PersistentDataType.STRING, "floweroftruth");
        stick.setItemMeta(meta);
        p.getInventory().addItem(stick);
        return false;
    }

    public String TACC(String str){
        return SedriPlugin.TACC(str);
    }

    public static void activate(Player p){
        ArrayList<LivingEntity> entities = (ArrayList<LivingEntity>) p.getLocation().getNearbyLivingEntities(15);
        double lowestDistanceSoFar = Double.MAX_VALUE;
        Entity closestEntity = null;
        for (Entity entity : entities) { // This loops through all the entities, setting the variable "entity" to each element.
            double distance = entity.getLocation().distance(p.getLocation());
            if (distance < lowestDistanceSoFar && !entity.equals(p) && !entity.getType().equals(EntityType.ARMOR_STAND) && !entity.getType().equals(EntityType.DROPPED_ITEM)) {
                lowestDistanceSoFar = distance;
                closestEntity = entity;
            }
        }
        if (closestEntity != null){
            ArmorStand armorstand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation() , EntityType.ARMOR_STAND);
            armorstand.setGravity(false);
            armorstand.setInvulnerable(true);
            //armorstand.setGlowing(true);
            armorstand.setVisible(false);
            armorstand.setItem(EquipmentSlot.HEAD, new ItemStack(Material.POPPY));
            armorstand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
            armorstand.setSmall(true);
            armorstand.setHeadPose(new EulerAngle(Math.toRadians(90),0,0));
            Vector vec = closestEntity.getLocation().toVector().clone().subtract(armorstand.getLocation().toVector());
            armorstand.teleport(armorstand.getLocation().setDirection(vec));
            final double finalLowestDistanceSoFar = lowestDistanceSoFar;
            final Entity finalClosestEntity = closestEntity;
            new BukkitRunnable(){
                int killed = 0;
                double distanceSoFar = 0;
                Entity[] previousEnts = new Entity[3];
                Entity ent = finalClosestEntity;
                double distance = finalLowestDistanceSoFar;

                double ticks = 0;

                @Override
                public void run() {
                    if (ticks % 3 == 0) {
                        Vector vec = ent.getLocation().toVector().clone().subtract(armorstand.getLocation().toVector());
                        armorstand.teleport(armorstand.getLocation().setDirection(vec));
                        distance = ent.getLocation().distance(p.getLocation())-0.5;
                    }
                    if (killed < 3){
                        if (distance > distanceSoFar){
                            armorstand.teleport(armorstand.getLocation().add(armorstand.getLocation().getDirection().normalize().multiply(0.5)));
                            distanceSoFar+=0.5;
                        } else {
                            distanceSoFar = 0;
                            ((LivingEntity) ent).damage(100);
                            distance = Double.MAX_VALUE;
                            previousEnts[killed] = ent;
                            ent = null;
                            ArrayList<LivingEntity> entities = (ArrayList<LivingEntity>) p.getLocation().getNearbyLivingEntities(30);
                            for (Entity entity : entities) {
                                double lowdistance = entity.getLocation().distance(p.getLocation());
                                boolean same = false;
                                if (entity.isDead()) continue;
                                /*for (Entity ents: previousEnts){
                                    if (ents == null) continue;
                                    if (ents.equals(entity)) {
                                        same = true;
                                        break;
                                    }
                                }*/
                                if (lowdistance < distance &&
                                        !entity.equals(p) &&
                                        !entity.getType().equals(EntityType.ARMOR_STAND) &&
                                        !entity.getType().equals(EntityType.DROPPED_ITEM) &&
                                        !same) {
                                    distance = lowdistance;
                                    ent = entity;
                                }
                            }
                            if (ent == null){
                                armorstand.remove();
                                cancel();
                            }
                            killed++;
                        }
                    } else {
                        armorstand.remove();
                        cancel();
                    }
                    ticks++;
                }
            }.runTaskTimer(SedriPlugin.getPlugin(), 0, 1);
        }
    }
}
