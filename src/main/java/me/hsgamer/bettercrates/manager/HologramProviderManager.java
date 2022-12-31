package me.hsgamer.bettercrates.manager;

import lombok.experimental.UtilityClass;
import me.hsgamer.unihologram.spigot.SpigotHologramProvider;

@UtilityClass
public final class HologramProviderManager {
    private static final SpigotHologramProvider hologramProvider;

    static {
        hologramProvider = new SpigotHologramProvider();
    }

    public static SpigotHologramProvider getHologramProvider() {
        return hologramProvider;
    }
}
