package net.seyarada.pandeloot.trackers;

import net.seyarada.pandeloot.Logger;
import net.seyarada.pandeloot.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import pl.koral.mcskyblockcore.api.model.SkyblockPlayer;
import pl.koral.mcskyblockcore.spigot.SkyblockCore;

import java.util.*;

public class DamageBoard {

    public static HashMap<UUID, DamageBoard> damageBoards = new HashMap<>();

    public HashMap<UUID, Double> playersAndDamage = new HashMap<>();


    public Map<UUID, Long> noDamageTicksMap = new HashMap<>();

    protected UUID mobUUID;
    protected LivingEntity mobLiving;
    public double baseHealth;

    public UUID lastHit;
    public UUID firstHit;
    public double damageReceived;
    public boolean sortWithParty;

    public LinkedList<UUID> playerRanks = new LinkedList<>();
    public LinkedList<Double> playerDamages = new LinkedList<>();
    public HashMap<String, String> placeholders = new HashMap<>();

    List<Map.Entry<UUID, Double>> sortedPlayers;
    Comparator<Map.Entry<UUID, Double>> comparator = Map.Entry.comparingByValue();

    public DamageBoard(LivingEntity mob, boolean sortWithParty) {
        this.mobUUID = mob.getUniqueId();
        this.mobLiving = mob;
        this.sortWithParty = sortWithParty;
        damageBoards.put(mobUUID, this);
    }

    public void compileInformation(boolean killLog) {
        baseHealth = mobLiving.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        damageReceived = playersAndDamage.values().stream().mapToDouble(Double::valueOf).sum();
        sort(playersAndDamage, sortWithParty);

        Map<Integer,String> soutMap = new HashMap<>();

        placeholders.put("mob.uuid", mobUUID.toString());
        placeholders.put("mob.hp", String.valueOf((int) baseHealth));
        placeholders.put("mob.tanked", String.valueOf((int) damageReceived));
        placeholders.put("mob.name", (mobLiving.isCustomNameVisible()) ? mobLiving.getCustomName() : mobLiving.getName());

        int i = 1;
        for (Map.Entry<UUID, Double> entry : sortedPlayers) {
            playerRanks.add(entry.getKey());
            playerDamages.add(entry.getValue());
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null)
                placeholders.put(i + ".name", p.getDisplayName());
            String visualDamage = MathUtils.dd.format(entry.getValue());

            var percent = getPercent(i - 1, false);

            placeholders.put(i + ".damage", visualDamage);
            placeholders.put(i + ".dmg", getPercent(i - 1, false));
            placeholders.put(i + ".percent", percent);
            placeholders.put(i + ".ratio", getPercent(i - 1, true));
            if(killLog){
                String merged = p.getName()+" "+percent+" "+visualDamage;
                soutMap.put(i,merged);
            }

            i++;
        }
        if (killLog) {
            System.out.println("Damage Board Results for " + mobLiving.getName() + ":");
            System.out.println(soutMap);

        }
    }

    public String getPercent(int rank, boolean isRatio) {
        int ratio = (isRatio) ? 1 : 100;
        return String.valueOf(MathUtils.dd.format(playerDamages.get(rank) / damageReceived * ratio));
    }

    public void sort(HashMap<UUID, Double> players, boolean sortWithParty) {

        if (!sortWithParty) {
            sortedPlayers = new ArrayList<>(players.entrySet());
            sortedPlayers.sort(comparator.reversed());
            return;
        }

        HashMap<UUID, Double> partyDamages = new HashMap<>();
        HashMap<UUID, Integer> playerCountInParty = new HashMap<>();
        HashMap<UUID, Double> adjustedDamages = new HashMap<>();

        // Step 1: Calculate party damages and counts
        for (Map.Entry<UUID, Double> entry : players.entrySet()) {
            UUID playerUUID = entry.getKey();
            double damage = entry.getValue();
            SkyblockPlayer skyblockPlayer = SkyblockCore.getInstance().getSkyblockCoreAPI().getLocalSkyblockPLayer(playerUUID);

            if (skyblockPlayer.getPartyUUID() != null) {
                UUID partyUUID = skyblockPlayer.getPartyUUID();
                partyDamages.put(partyUUID, partyDamages.getOrDefault(partyUUID, 0.0) + damage);
                playerCountInParty.put(partyUUID, playerCountInParty.getOrDefault(partyUUID, 0) + 1);
            } else {
                adjustedDamages.put(playerUUID, damage);
            }
        }

        // Step 2: Distribute damages
        for (Map.Entry<UUID, Double> entry : players.entrySet()) {
            UUID playerUUID = entry.getKey();
            SkyblockPlayer skyblockPlayer = SkyblockCore.getInstance().getSkyblockCoreAPI().getLocalSkyblockPLayer(playerUUID);

            if (skyblockPlayer.getPartyUUID() != null) {
                UUID partyUUID = skyblockPlayer.getPartyUUID();
                double totalPartyDamage = partyDamages.get(partyUUID);
                int partySize = playerCountInParty.get(partyUUID);
                double averageDamage = totalPartyDamage / partySize;
                adjustedDamages.put(playerUUID, averageDamage);
            }
        }

        // Step 3: Sort players by adjusted damages
        sortedPlayers = new ArrayList<>(adjustedDamages.entrySet());
        Collections.shuffle(sortedPlayers);
        sortedPlayers.sort(comparator.reversed());


    }


    public static void addPlayerDamage(UUID damagedEntity, Player player, double damage) {
        if (Bukkit.getEntity(damagedEntity) instanceof LivingEntity mob) {
            damage = Math.min(mob.getHealth(), damage);
            UUID playerUUID = player.getUniqueId();

            DamageBoard board = damageBoards.get(damagedEntity);
            board.playersAndDamage.merge(playerUUID, damage, Double::sum);

            board.lastHit = playerUUID;
            if (board.firstHit == null) board.firstHit = playerUUID;
        }
    }


    public static boolean canPlayerAttack(UUID damagedEntity, Player player) {
        if (Bukkit.getEntity(damagedEntity) instanceof LivingEntity mob) {

            UUID playerUUID = player.getUniqueId();
            DamageBoard board = damageBoards.get(damagedEntity);
            long lastHitTime = board.noDamageTicksMap.getOrDefault(playerUUID, 0L);
            return lastHitTime <= System.currentTimeMillis();

        }
        return false;
    }

    public static void addToNoDmgMsMap(UUID damagedEntity, Player player, int ms) {
        if (Bukkit.getEntity(damagedEntity) instanceof LivingEntity mob) {

            UUID playerUUID = player.getUniqueId();
            DamageBoard board = damageBoards.get(damagedEntity);
            board.noDamageTicksMap.put(playerUUID, ms + System.currentTimeMillis());
        }
    }


    public static boolean contains(UUID uuid) {
        return damageBoards.containsKey(uuid);
    }

    public static DamageBoard get(UUID uuid) {
        return damageBoards.get(uuid);
    }

    public static void remove(UUID uuid) {
        damageBoards.remove(uuid);
    }


}