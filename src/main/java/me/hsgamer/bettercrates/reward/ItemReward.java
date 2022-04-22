package me.hsgamer.bettercrates.reward;

import me.hsgamer.bettercrates.api.reward.RewardContent;
import me.hsgamer.bettercrates.builder.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemReward implements RewardContent {
    private ItemStack item;

    @Override
    public void init(Map<String, Object> map) {
        item = ItemStackBuilder.buildItem(map);
    }

    @Override
    public void reward(Player player) {
        player.getInventory().addItem(item.clone());
    }
}
