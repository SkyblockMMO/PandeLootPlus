package net.seyarada.pandeloot.flags.conditions;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.seyarada.pandeloot.drops.IDrop;
import net.seyarada.pandeloot.drops.LootDrop;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.FlagPack;
import net.seyarada.pandeloot.flags.types.ICondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@FlagEffect(id = "mmoclass", description = "Determines the active color of a drop")
public class MMOClassFlag implements ICondition {

    @Override
    public boolean onCheck(FlagPack.FlagModifiers values, LootDrop lootDrop, IDrop itemDrop) {
        if (lootDrop.p == null) return true;
        PlayerData playerData = PlayerData.get(lootDrop.p);
        return values.getString().equalsIgnoreCase(playerData.getProfess().getName());

    }

    @Override
    public boolean onCheckNoLootDrop(FlagPack.FlagModifiers values, Entity entity, Player player) {
        return true;
    }

}
