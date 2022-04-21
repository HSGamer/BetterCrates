package me.hsgamer.bettercrates.api.reward;

import org.bukkit.entity.Player;

import java.util.Map;

public interface RewardContent {
    void init(Map<String, Object> map);

    void reward(Player player);
}
