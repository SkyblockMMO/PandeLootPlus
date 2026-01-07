package net.seyarada.pandeloot.flags.conditions;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.seyarada.pandeloot.Logger;
import net.seyarada.pandeloot.drops.IDrop;
import net.seyarada.pandeloot.drops.LootDrop;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.FlagPack;
import net.seyarada.pandeloot.flags.types.ICondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Random;

@FlagEffect(id = "mmolevel", description = "Determines the active color of a drop")
public class MMOLevelFlag implements ICondition {

    @Override
    public boolean onCheck(FlagPack.FlagModifiers values, LootDrop lootDrop, IDrop itemDrop) {
        if (lootDrop.p == null) return true;
        PlayerData playerData = PlayerData.get(lootDrop.p);
        return hasLevel(values.getString(), playerData.getLevel());
    }

    @Override
    public boolean onCheckNoLootDrop(FlagPack.FlagModifiers values, Entity entity, Player player) {
        return true;
    }


    public static boolean hasLevel(String strAmount, int playerLevel) {
        if (strAmount.contains("to")) {
            final String[] n = strAmount.split("to");
            if (!n[0].isEmpty() && !n[1].isEmpty()) {
                final double rangeMin = Double.parseDouble(n[0]);
                final double rangeMax = Double.parseDouble(n[1]);
                return playerLevel >= rangeMin && playerLevel <= rangeMax;
            }
        }
        if (strAmount.startsWith("-")) {
            return playerLevel <= Integer.parseInt(strAmount.replace("-", ""));
        }
        if (strAmount.startsWith("+")) {
            return playerLevel >= Integer.parseInt(strAmount.replace("+", ""));
        }
        return playerLevel == Integer.parseInt(strAmount);

    }


}
