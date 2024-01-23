package net.seyarada.pandeloot.trackers;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
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

import java.util.List;
import java.util.UUID;

public class DamageTracker implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamaged(PlayerAttackEvent e) {

        UUID mob = e.getEntity().getUniqueId();
        if (!DamageBoard.contains(mob)) return;

        Player player = e.getPlayer();
        if (CitizensCompatibility.isFromCitizens(player)) return;
        ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(mob).orElse(null);
        if(mythicMob == null){
            System.out.println("MM niby null");
            return;
        }
        double maxPercentDamage = mythicMob.getType().getConfig().getDouble("maxPercentDamage");
        double maxHp = mythicMob.getEntity().getMaxHealth();
        double damage = e.getDamage().getDamage();
        double formatedDamage = maxHp*maxPercentDamage;
        if(maxPercentDamage > 0 &&  damage > formatedDamage){
            damage = formatedDamage;
            e.setCancelled(true);
            LivingEntity livingEntity = (LivingEntity) mythicMob.getEntity().getBukkitEntity();
            double healthToSet = livingEntity.getHealth() - formatedDamage <=0? 0 : livingEntity.getHealth() - formatedDamage;
            //Dodawanie do boarda zanim mob umrze bo inaczej nullpointer
            DamageBoard.addPlayerDamage(mob, player, damage);
            livingEntity.setHealth(healthToSet);
            livingEntity.damage(formatedDamage,e.getPlayer());
        }else DamageBoard.addPlayerDamage(mob, player, damage);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof LivingEntity entity) {
            ConfigurationSection config = Config.getMob(entity);
            if (config != null) {
                boolean shouldTrack = config.contains("Rewards");
                if (!shouldTrack) shouldTrack = config.getBoolean("Options.ScoreHologram");
                if (!shouldTrack) shouldTrack = config.getBoolean("Options.ScoreMessage");

                if (shouldTrack) new DamageBoard(entity);
            }
        }

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


























