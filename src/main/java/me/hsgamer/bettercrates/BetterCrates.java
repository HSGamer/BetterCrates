package me.hsgamer.bettercrates;

import me.hsgamer.bettercrates.command.GiveKeyCommand;
import me.hsgamer.bettercrates.command.SetBlockCommand;
import me.hsgamer.bettercrates.config.MessageConfig;
import me.hsgamer.bettercrates.hooks.Hooks;
import me.hsgamer.bettercrates.listener.CrateListener;
import me.hsgamer.bettercrates.listener.InteractListener;
import me.hsgamer.bettercrates.manager.CrateManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;

public final class BetterCrates extends BasePlugin {
    private final CrateManager crateManager = new CrateManager(this);
    private final MessageConfig messageConfig = ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "message.yml"));

    @Override
    public void enable() {
        MessageUtils.setPrefix(messageConfig::getPrefix);
        Permissions.register();

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

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }
}
