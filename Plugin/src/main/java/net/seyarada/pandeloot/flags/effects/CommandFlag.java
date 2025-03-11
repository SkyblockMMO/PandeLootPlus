package net.seyarada.pandeloot.flags.effects;

import net.seyarada.pandeloot.Logger;
import net.seyarada.pandeloot.drops.ItemDropMeta;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.types.IGeneralEvent;
import org.bukkit.Bukkit;

import java.util.Collections;

@FlagEffect(id="command", description="Executes a command")
public class CommandFlag implements IGeneralEvent {

	@Override
	public void onCallGeneral(ItemDropMeta meta) {
		if(meta.getString()!=null) {
			double chances = meta.iDrop().getChance(meta.lootDrop());

			int howManyToDrop = getAmountFromChancesMoreThanOne(chances);

			for (int i = 0; i < howManyToDrop; i++) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), meta.getString());
			}
		}
	}


	public int getAmountFromChancesMoreThanOne(double chances) {
		if (chances < 1) return 1;

		double reszta = chances % 1;
		double calosci = chances - reszta;

		if (Math.random() <= reszta) {
			calosci = calosci + 1;
		}
		return (int) calosci;
	}

}
