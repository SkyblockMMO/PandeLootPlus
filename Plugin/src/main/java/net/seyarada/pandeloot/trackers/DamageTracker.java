package net.seyarada.pandeloot.trackers;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import net.seyarada.pandeloot.PandeLoot;
import net.seyarada.pandeloot.compatibility.citizens.CitizensCompatibility;
import net.seyarada.pandeloot.config.Config;
import net.seyarada.pandeloot.drops.LootDrop;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;

public class DamageTracker implements Listener {


    private final Map<UUID, Long> deathTimeMap = new WeakHashMap<>();


    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamaged(EntityDamageByEntityEvent e) {

        UUID mob = e.getEntity().getUniqueId();
        if (!DamageBoard.contains(mob)) return;
        Player player;
        if (e.getDamager() instanceof Player damager) {
            player = damager;
        } else if (e.getDamager() instanceof Projectile p && p.getShooter() instanceof Player damager) {
            player = damager;
        } else return;
        if (CitizensCompatibility.isFromCitizens(player)) return;
        ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(mob).orElse(null);
        if (mythicMob == null) {
            System.out.println("MM niby null");
            return;
        }
        if (deathTimeMap.containsKey(player.getUniqueId())) {
            if (deathTimeMap.get(player.getUniqueId()) > System.currentTimeMillis()) {
                return;
            } else deathTimeMap.remove(player.getUniqueId());
        }
        double maxPercentDamage = mythicMob.getType().getConfig().getDouble("maxPercentDamage");
        double maxHp = mythicMob.getEntity().getMaxHealth();
        double damage = e.getFinalDamage();
        double formatedDamage = maxHp * maxPercentDamage;
        if (maxPercentDamage > 0 && damage > formatedDamage) {
            damage = formatedDamage;
            e.setCancelled(true);
            LivingEntity livingEntity = (LivingEntity) mythicMob.getEntity().getBukkitEntity();
            double healthToSet = livingEntity.getHealth() - formatedDamage <= 0 ? 0 : livingEntity.getHealth() - formatedDamage;
            DamageBoard.addPlayerDamage(mob, player, damage);
            livingEntity.setHealth(healthToSet);
        } else DamageBoard.addPlayerDamage(mob, player, damage);

    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof LivingEntity entity) {
            ConfigurationSection config = Config.getMob(entity);
            if (config != null) {
                boolean shouldTrack = config.contains("Rewards");
                boolean sortWithParty = config.getBoolean("Options.SortWithParty");
                if (!shouldTrack) shouldTrack = config.getBoolean("Options.ScoreHologram");
                if (!shouldTrack) shouldTrack = config.getBoolean("Options.ScoreMessage");

                if (shouldTrack) new DamageBoard(entity, sortWithParty);
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        deathTimeMap.put(e.getEntity().getUniqueId(), System.currentTimeMillis() + 8000);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent e) {
        UUID mob = e.getEntity().getUniqueId();
        if (!DamageBoard.contains(mob)) return;

        ConfigurationSection config = Config.getMob(e.getEntity());
        if (config == null) return;
        boolean scoreMessage = config.getBoolean("Options.ScoreMessage");
        boolean scoreHologram = config.getBoolean("Options.ScoreHologram");

        List<String> strings = config.getStringList("Rewards");

        DamageBoard damageBoard = DamageBoard.get(mob);
        damageBoard.compileInformation();


        for (UUID uuid : damageBoard.playersAndDamage.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            LootDrop lootDrop = new LootDrop(strings, player, e.getEntity().getLocation())
                    .setDamageBoard(damageBoard)
                    .setSourceEntity(e.getEntity())
                    .build();

            if (scoreHologram) lootDrop.displayScoreHolograms();
            if (scoreMessage) lootDrop.displayScoreMessage();

            lootDrop.drop();
        }

        DamageBoard.remove(mob);
    }

}


























