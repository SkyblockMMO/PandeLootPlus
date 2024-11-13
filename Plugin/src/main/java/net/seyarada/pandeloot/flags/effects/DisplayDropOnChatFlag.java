package net.seyarada.pandeloot.flags.effects;

import net.seyarada.pandeloot.drops.ItemDropMeta;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.enums.FlagPriority;
import net.seyarada.pandeloot.flags.types.IItemEvent;
import org.bukkit.entity.Item;

@FlagEffect(id="displaydroponchat", description="Gives the item directly to the player", priority = FlagPriority.LOWEST)
public class DisplayDropOnChatFlag implements IItemEvent {

    @Override
    public void onCallItem(Item item, ItemDropMeta meta) {

    }

}
