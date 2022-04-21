package me.hsgamer.bettercrates.hologram.none;

import me.hsgamer.bettercrates.api.hologram.Hologram;
import me.hsgamer.bettercrates.api.hologram.HologramProvider;
import me.hsgamer.bettercrates.crate.CrateBlock;
import me.hsgamer.bettercrates.crate.Reward;

public class NoneHologramProvider implements HologramProvider {
    private final Hologram hologram = new Hologram() {
        @Override
        public void reset() {
            // EMPTY
        }

        @Override
        public void setReward(Reward reward) {
            // EMPTY
        }
    };

    @Override
    public Hologram createHologram(CrateBlock crateBlock) {
        return hologram;
    }
}
