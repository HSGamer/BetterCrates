package me.hsgamer.bettercrates.api.hologram;

import me.hsgamer.bettercrates.crate.Reward;

public interface Hologram {
    default void init() {
        // EMPTY
    }

    default void clear() {
        // EMPTY
    }

    void reset();

    void setReward(Reward reward);
}
