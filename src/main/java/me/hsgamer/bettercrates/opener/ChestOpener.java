package me.hsgamer.bettercrates.opener;

import me.hsgamer.hscore.common.Validate;
import org.bukkit.block.Block;

public interface ChestOpener {
    ChestOpener INSTANCE = getInstance();

    private static ChestOpener getInstance() {
        if (Validate.isClassLoaded("org.bukkit.block.Lidded")) {
            return new ModernChestOpener();
        } else {
            return (block, open) -> {
            };
        }
    }

    void setBlockLid(Block block, boolean open);
}
