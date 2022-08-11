package me.hsgamer.bettercrates.manager;

import lombok.experimental.UtilityClass;
import me.hsgamer.bettercrates.api.hologram.HologramProvider;
import me.hsgamer.bettercrates.hologram.dh.DHHologramProvider;
import me.hsgamer.bettercrates.hologram.none.NoneHologramProvider;
import org.bukkit.Bukkit;

@UtilityClass
public final class HologramProviderManager {
    private static final HologramProvider hologramProvider;

    static {
        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            hologramProvider = new DHHologramProvider();
        } else {
            hologramProvider = new NoneHologramProvider();
        }
    }

    public static HologramProvider getHologramProvider() {
        return hologramProvider;
    }
}
