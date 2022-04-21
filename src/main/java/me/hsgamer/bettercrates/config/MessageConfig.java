package me.hsgamer.bettercrates.config;

import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.bettercrates.crate.Reward;
import me.hsgamer.hscore.config.annotation.ConfigPath;

public interface MessageConfig {
    @ConfigPath("prefix")
    default String getPrefix() {
        return "&7[&bBetterCrates&7] &r";
    }

    @ConfigPath("crate-not-found")
    default String getCrateNotFound() {
        return "&cCrate not found";
    }

    @ConfigPath("not-enough-key")
    default String getNotEnoughKey() {
        return "&cNot enough key";
    }

    @ConfigPath("crate-delaying")
    default String getCrateDelaying() {
        return "&cCrate is being used by another player";
    }

    @ConfigPath("reward")
    default String getReward() {
        return "&aYou have opened {crate} and received {reward}";
    }

    default String getRewardMessage(Crate crate, Reward reward) {
        return getReward()
                .replace("{crate}", crate.getDisplayName())
                .replace("{reward}", reward.getDisplayName());
    }
}
