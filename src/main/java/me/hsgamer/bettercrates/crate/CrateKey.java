package me.hsgamer.bettercrates.crate;

import lombok.Value;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Value
public class CrateKey {
    String id;
    ItemStack item;

    public boolean checkAndTake(Player player) {
        if (player.getInventory().contains(item)) {
            player.getInventory().remove(item);
            return true;
        }
        return false;
    }
}
