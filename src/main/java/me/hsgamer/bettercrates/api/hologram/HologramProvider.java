package me.hsgamer.bettercrates.api.hologram;

import me.hsgamer.bettercrates.crate.CrateBlock;

public interface HologramProvider {
    Hologram createHologram(CrateBlock crateBlock);
}
