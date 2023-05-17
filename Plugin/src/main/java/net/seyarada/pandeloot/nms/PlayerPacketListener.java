package net.seyarada.pandeloot.nms;

import net.seyarada.pandeloot.trackers.DamageBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerPacketListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        NMSManager.get().injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        DamageBoard.damageBoards.forEach( (entity, damageBoard) ->{
            damageBoard.playersAndDamage.remove(e.getPlayer().getUniqueId());
        });
        NMSManager.get().removePlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent e) {
        NMSManager.removeHiddenItem(e.getItem().getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDespawn(ItemDespawnEvent e) {
        NMSManager.removeHiddenItem(e.getEntity().getEntityId());
    }

}