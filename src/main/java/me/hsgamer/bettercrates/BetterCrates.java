package me.hsgamer.bettercrates;

import me.hsgamer.bettercrates.manager.CrateManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;

public final class BetterCrates extends BasePlugin {
    private final CrateManager crateManager = new CrateManager(this);

    @Override
    public void postEnable() {
        crateManager.init();
    }

    @Override
    public void disable() {
        crateManager.clear();
    }
}
