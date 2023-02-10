package me.hsgamer.bettercrates;

import fr.mrmicky.fastinv.FastInvManager;
import lombok.Getter;
import me.hsgamer.bettercrates.command.GiveKeyCommand;
import me.hsgamer.bettercrates.command.SetBlockCommand;
import me.hsgamer.bettercrates.config.MainConfig;
import me.hsgamer.bettercrates.hooks.Hooks;
import me.hsgamer.bettercrates.listener.CrateListener;
import me.hsgamer.bettercrates.listener.InteractListener;
import me.hsgamer.bettercrates.manager.CrateManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;

@Getter
public final class BetterCrates extends BasePlugin {
    private final CrateManager crateManager = new CrateManager(this);
    private final MainConfig mainConfig = new MainConfig(this);

    @Override
    public void load() {
        mainConfig.setup();
    }

    @Override
    public void enable() {
        MessageUtils.setPrefix(() -> mainConfig.prefix);
        Permissions.register();

        FastInvManager.register(this);
        registerListener(new CrateListener(this));
        registerListener(new InteractListener(this));
        registerCommand(new GiveKeyCommand(this));
        registerCommand(new SetBlockCommand(this));
        Hooks.register();
    }

    @Override
    public void postEnable() {
        crateManager.init();
    }

    @Override
    public void disable() {
        crateManager.clear();
        Permissions.unregister();
    }
}
