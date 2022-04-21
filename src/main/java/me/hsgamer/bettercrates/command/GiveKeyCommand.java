package me.hsgamer.bettercrates.command;

import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.Permissions;
import me.hsgamer.bettercrates.crate.CrateKey;
import me.hsgamer.hscore.bukkit.utils.ItemUtils;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GiveKeyCommand extends Command {
    private final BetterCrates plugin;

    public GiveKeyCommand(BetterCrates plugin) {
        super("givecratekey", "Give crate key to player", "/givecratekey <key> <player> [amount]", Collections.singletonList("givekey"));
        this.plugin = plugin;
        setPermission(Permissions.GIVE_KEY.getName());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }

        Optional<CrateKey> optionalCrateKey = plugin.getCrateManager().getCrateKey(args[0]);
        if (optionalCrateKey.isEmpty()) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getKeyNotFound());
            return false;
        }
        CrateKey crateKey = optionalCrateKey.get();
        ItemStack itemStack = crateKey.getItem().clone();

        Player player = plugin.getServer().getPlayer(args[1]);
        if (player == null) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlayerNotFound());
            return false;
        }

        if (args.length > 2) {
            try {
                int amount = Integer.parseInt(args[2]);
                itemStack.setAmount(amount);
            } catch (NumberFormatException ignored) {
                // IGNORED
            }
        }

        ItemUtils.giveItem(player, itemStack);
        MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGiveKeySuccess(player, crateKey, itemStack));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String keyName = args[0];
            return plugin.getCrateManager().getCrateKeyIds().stream()
                    .filter(keyId -> keyName.isEmpty() || keyId.toLowerCase().startsWith(keyName.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String playerName = args[1];
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> playerName.isEmpty() || name.toLowerCase().startsWith(playerName.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }
        return Collections.emptyList();
    }
}
