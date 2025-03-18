package net.seyarada.pandeloot.flags.effects;

import net.seyarada.pandeloot.Logger;
import net.seyarada.pandeloot.drops.ItemDropMeta;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.types.IGeneralEvent;
import org.bukkit.Bukkit;

import java.util.Collections;

@FlagEffect(id = "command", description = "Executes a command")
public class CommandFlag implements IGeneralEvent {

    @Override
    public void onCallGeneral(ItemDropMeta meta) {
        if (meta.getString() != null) {
            String[] commands = meta.getString().split("\\|");
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

}
