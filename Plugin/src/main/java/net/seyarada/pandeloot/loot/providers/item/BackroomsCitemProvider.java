package net.seyarada.pandeloot.loot.providers.item;

import me.buch.backroomscore.customItem.ItemBase;
import net.seyarada.pandeloot.api.ItemProvider;
import net.seyarada.pandeloot.drops.LootDrop;
import net.seyarada.pandeloot.flags.FlagPack;
import net.seyarada.pandeloot.flags.effects.TypeFlag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BackroomsCitemProvider implements ItemProvider {
    @Override
    public ItemStack getItem(String id, FlagPack pack, Player player, LootDrop drop) {
        FlagPack.FlagModifiers citemData = pack.getFlag(TypeFlag.class);
        String typeStr = citemData.getString("type");
        return ItemBase.getCustomItem(typeStr, id).getItem();
    }

    @Override
    public boolean isPresent(String id, FlagPack pack, Player player, LootDrop drop) {
        if(!pack.hasFlag(TypeFlag.class)) return false;
        FlagPack.FlagModifiers citemData = pack.getFlag(TypeFlag.class);
        System.out.println("STRINGPANDELOOT");
        System.out.println(citemData.getString());
        System.out.println(citemData.getString("type"));
        return ItemBase.getCustomItem(citemData.getString("type"),id).getMaterial() != null;

    }
}
