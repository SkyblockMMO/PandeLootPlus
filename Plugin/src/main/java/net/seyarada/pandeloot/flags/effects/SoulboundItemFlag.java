package net.seyarada.pandeloot.flags.effects;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import net.seyarada.pandeloot.drops.ItemDropMeta;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.enums.FlagPriority;
import net.seyarada.pandeloot.flags.types.IItemEvent;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FlagEffect(id="soulbound", description="Gives the item directly to the player", priority = FlagPriority.LOWEST)
public class SoulboundItemFlag implements IItemEvent {

    @Override
    public void onCallItem(Item item, ItemDropMeta meta) {
        if(meta.lootDrop()==null) return;
        if(meta.lootDrop().p==null) return;

        if(meta.getBoolean()) {

            //meta.lootDrop().p.getInventory().addItem(item.getItemStack());
            Player player = meta.lootDrop().p;

           var target = MythicLib.plugin.getVersion().getWrapper().getNBTItem(item.getItemStack());
           MMOItem targetMMOa = new LiveMMOItem(target);
           targetMMOa.setData(ItemStats.SOULBOUND, new SoulboundData(player, 1));
           ItemStack output = targetMMOa.newBuilder().build();
           item.getItemStack().setItemMeta(output.getItemMeta());
        }

    }

}
