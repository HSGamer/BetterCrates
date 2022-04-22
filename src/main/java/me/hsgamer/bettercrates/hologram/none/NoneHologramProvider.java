package me.hsgamer.bettercrates.hologram.none;

import me.hsgamer.bettercrates.api.hologram.Hologram;
import me.hsgamer.bettercrates.api.hologram.HologramProvider;
import me.hsgamer.bettercrates.crate.CrateBlock;

public class NoneHologramProvider implements HologramProvider {
    @Override
    public Hologram createHologram(CrateBlock crateBlock) {
        return new NoneHologram(crateBlock);
    }
}
