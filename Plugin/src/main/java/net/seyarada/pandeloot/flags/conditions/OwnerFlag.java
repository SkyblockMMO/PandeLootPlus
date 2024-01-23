package net.seyarada.pandeloot.flags.conditions;

import net.seyarada.pandeloot.drops.IDrop;
import net.seyarada.pandeloot.drops.LootDrop;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.FlagPack;
import net.seyarada.pandeloot.flags.types.ICondition;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import pl.koral.mcskyblockcore.spigot.SkyblockCore;

@FlagEffect(id = "owner", description = "is drop to owner")
public class OwnerFlag implements ICondition {
    @Override
    public boolean onCheck(FlagPack.FlagModifiers values, LootDrop lootDrop, IDrop itemDrop) {
        if (lootDrop.p == null) return true;
        if(values.getBoolean()) {
            String ownerName = lootDrop.sourceEntity.getPersistentDataContainer().get(new NamespacedKey(SkyblockCore.getInstance(), "MMBlock"), PersistentDataType.STRING);
            return lootDrop.p.getName().equalsIgnoreCase(ownerName);
        }
        return true;
    }

    @Override
    public boolean onCheckNoLootDrop(FlagPack.FlagModifiers values, @Nullable Entity entity, Player player) {
        return true;
    }
}
