package me.hsgamer.bettercrates.command;

import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.Permissions;
import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetKeyCommand extends Command {
    private final BetterCrates plugin;

    public SetKeyCommand(BetterCrates plugin) {
        super("setcratekey", "Set the item in player's hand to be a crate key", "/setcratekey <key>", Collections.singletonList("setkey"));
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

        Optional<Crate> optionalCrate = plugin.getCrateManager().getCrate(args[0]);
        if (optionalCrate.isEmpty()) {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().crateNotFound);
            return false;
        }
        Crate crate = optionalCrate.get();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (crate.setCrateKey(itemStack)) {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().getSetKeySuccess(crate));
        } else {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().getSetKeyFail(crate));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String keyName = args[0];
            return plugin.getCrateManager().getCrateIds().stream()
                    .filter(keyId -> keyName.isEmpty() || keyId.toLowerCase().startsWith(keyName.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
