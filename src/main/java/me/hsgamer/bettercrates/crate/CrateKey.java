package me.hsgamer.bettercrates.crate;

import lombok.Value;
import me.hsgamer.hscore.bukkit.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Value
public class CrateKey {
    String id;
    ItemStack item;

    public boolean checkAndTake(Player player) {
        ItemUtils.ItemCheckSession session = ItemUtils.createItemCheckSession(player.getInventory(), ItemUtils.getItemPredicate(item), item.getAmount());
        if (session.isAllMatched) {
            session.takeRunnable.run();
            return true;
        }
        return false;
    }
}
