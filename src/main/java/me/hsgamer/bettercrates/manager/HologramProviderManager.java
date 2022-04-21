package me.hsgamer.bettercrates.manager;

import me.hsgamer.bettercrates.api.hologram.HologramProvider;
import me.hsgamer.bettercrates.hologram.dh.DHHologramProvider;
import me.hsgamer.bettercrates.hologram.none.NoneHologramProvider;
import org.bukkit.Bukkit;

public final class HologramProviderManager {
    private static final HologramProvider hologramProvider;

    static {
        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            hologramProvider = new DHHologramProvider();
        } else {
            hologramProvider = new NoneHologramProvider();
        }
    }

    private HologramProviderManager() {
        throw new IllegalStateException("Utility class");
    }

    public static HologramProvider getHologramProvider() {
        return hologramProvider;
    }
}
