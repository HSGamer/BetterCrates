package me.hsgamer.bettercrates.hologram.dh;

import me.hsgamer.bettercrates.api.hologram.Hologram;
import me.hsgamer.bettercrates.api.hologram.HologramProvider;
import me.hsgamer.bettercrates.crate.CrateBlock;

public class DHHologramProvider implements HologramProvider {
    @Override
    public Hologram createHologram(CrateBlock crateBlock) {
        return new DHHologram(crateBlock);
    }
}
