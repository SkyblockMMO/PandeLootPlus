package net.seyarada.pandeloot.flags.effects;

import net.seyarada.pandeloot.drops.ItemDropMeta;
import net.seyarada.pandeloot.flags.FlagEffect;
import net.seyarada.pandeloot.flags.enums.FlagPriority;
import net.seyarada.pandeloot.flags.types.IItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.koral.mcskyblockcore.api.model.SkyblockPlayer;
import pl.koral.mcskyblockcore.spigot.SkyblockCore;
import pl.koral.mcskyblockcore.spigot.event.CustomPickupEvent;

import java.text.DecimalFormat;


@FlagEffect(id = "toinventory", description = "Gives the item directly to the player", priority = FlagPriority.LOW)
public class ToInventoryFlag implements IItemEvent {


    private final DecimalFormat df = new DecimalFormat("##.####");


    @Override
    public void onCallItem(Item item, ItemDropMeta meta) {
        if (meta.lootDrop() == null) return;
        if (meta.lootDrop().p == null) return;

        if (meta.getBoolean()) {


            //meta.lootDrop().p.getInventory().addItem(item.getItemStack());
            Player player = meta.lootDrop().p;

            ItemStack newItem = item.getItemStack().clone();
            newItem.setAmount(1);
            SkyblockPlayer skyblockPlayer = SkyblockCore.getInstance().getSkyblockCoreAPI().getLocalSkyblockPLayer(player.getUniqueId());
            double chances = meta.iDrop().getChance(meta.lootDrop());

            int howManyToDrop = getDropWithFortune(chances);

            boolean hasFlag = meta.iDrop().getFlagPack().hasFlag(DisplayDropOnChatFlag.class);
            if (hasFlag && item.getItemStack().hasItemMeta()) {

                String chatFormat ="§eOtrzymałeś "+ item.getItemStack().getItemMeta().getDisplayName() + " §fx" + howManyToDrop + " §6(§e" + df.format (chances*100) + "%§6)";
                skyblockPlayer.getPlayer().sendMessage(chatFormat);
            }
            for (int i = 0; i < howManyToDrop; i++) {
                Bukkit.getPluginManager().callEvent(new CustomPickupEvent(skyblockPlayer, newItem, item.getItemStack().getAmount(), null));
            }
            item.remove();
        }

    }

    public int getDropWithFortune(double chances) {
        if (chances < 1) return 1;

        double reszta = chances % 1;
        double calosci = chances - reszta;

        if (Math.random() <= reszta) {
            calosci = calosci + 1;
        }
        return (int) calosci;
    }

}
