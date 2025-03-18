package net.seyarada.pandeloot.flags.conditions;

import net.seyarada.pandeloot.drops.IDrop;
import net.seyarada.pandeloot.drops.LootDrop;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.FlagPack;
import net.seyarada.pandeloot.flags.types.ICondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.kaps3l.pandacore.PandaCore;
import pl.kaps3l.pandacore.modules.quests.handlers.models.TagPlayer;

@FlagEffect(id="questtag", description="Determines questtag that player needs to have")
public class QuestTagFlag implements ICondition {

    @Override
    public boolean onCheck(FlagPack.FlagModifiers values, LootDrop lootDrop, IDrop itemDrop) {
        if(lootDrop.p==null) return true;
        TagPlayer tp = PandaCore.getPlugin().getModuleManager().getQuestManager().getPlayerHandler().getPlayers().get(lootDrop.p.getUniqueId());
        return  tp.getPlayerTags().contains(values.getString());
    }

    @Override
    public boolean onCheckNoLootDrop(FlagPack.FlagModifiers values, Entity entity, Player player) {
        return true;
    }
}
