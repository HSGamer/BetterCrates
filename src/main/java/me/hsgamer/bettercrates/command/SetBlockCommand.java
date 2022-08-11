package me.hsgamer.bettercrates.command;

import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.Permissions;
import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetBlockCommand extends Command {
    private final BetterCrates plugin;

    public SetBlockCommand(BetterCrates plugin) {
        super("setcrateblock", "Set a block to be a crate block", "/setcrateblock <crate> [delay]", Collections.singletonList("crateblock"));
        this.plugin = plugin;
        setPermission(Permissions.SET.getName());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().playerOnly);
            return false;
        }
        Player player = (Player) sender;

        Block targetBlock = player.getTargetBlockExact(5, FluidCollisionMode.NEVER);
        if (targetBlock == null || targetBlock.getType().isAir()) {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().blockRequired);
            return false;
        }
        if (plugin.getCrateManager().getCrateBlock(targetBlock.getLocation()).isPresent()) {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().blockAlreadySet);
            return false;
        }

        Optional<Crate> optionalCrate = plugin.getCrateManager().getCrate(args[0]);
        if (optionalCrate.isEmpty()) {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().crateNotFound);
            return false;
        }
        Crate crate = optionalCrate.get();

        long delay = 60;
        if (args.length > 1) {
            try {
                delay = Long.parseLong(args[1]);
            } catch (NumberFormatException ignored) {
                // IGNORED
            }
        }

        plugin.getCrateManager().addCrateBlock(targetBlock.getLocation(), crate, delay);
        MessageUtils.sendMessage(sender, plugin.getMainConfig().setBlockSuccess);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String crateName = args[0];
            return plugin.getCrateManager().getCrateIds().stream()
                    .filter(crateId -> crateName.isEmpty() || crateId.toLowerCase().startsWith(crateName.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("20", "40", "60", "80", "100");
        }
        return Collections.emptyList();
    }
}
