package me.hsgamer.bettercrates.crate;

import lombok.Value;
import me.hsgamer.hscore.bukkit.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@Value
public class CrateKey {
    String id;
    ItemStack item;

    public boolean checkAndTake(Player player) {
        Collection<ItemStack> items = ItemUtils.getMatchedItemsInInventory(player.getInventory(), ItemUtils.getItemPredicate(item), item.getAmount());
        int matchedAmount = items.stream().mapToInt(ItemStack::getAmount).sum();
        if (matchedAmount >= item.getAmount()) {
            ItemUtils.removeItemInInventory(player.getInventory(), items);
            return true;
        }
        return false;
    }
}
