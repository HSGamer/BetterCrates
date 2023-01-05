package me.hsgamer.bettercrates.reward;

import me.hsgamer.bettercrates.api.RewardContent;
import me.hsgamer.bettercrates.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemReward implements RewardContent {
    private final ItemStack item;

    public ItemReward(Map<String, Object> map) {
        item = ItemStackBuilder.INSTANCE.build(map).orElse(new ItemStack(Material.STONE));
    }

    @Override
    public void reward(Player player) {
        player.getInventory().addItem(item.clone());
    }
}
