package me.hsgamer.bettercrates.reward;

import me.hsgamer.bettercrates.api.reward.RewardContent;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandReward implements RewardContent {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\[(\\w+)]\\W?(.*)");
    private final List<Command> commands = new ArrayList<>();

    private static Command parse(String command) {
        Matcher matcher = COMMAND_PATTERN.matcher(command);
        if (matcher.find()) {
            CommandType type = getCommandType(matcher.group(1));
            String commandString = matcher.group(2);
            return new Command(type, commandString);
        } else {
            return new Command(CommandType.PLAYER, command);
        }
    }

    private static CommandType getCommandType(String type) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.name().equalsIgnoreCase(type)) {
                return commandType;
            }
        }
        return CommandType.PLAYER;
    }

    @Override
    public void init(Map<String, Object> map) {
        if (map.containsKey("commands")) {
            List<String> list = CollectionUtils.createStringListFromObject(map.get("commands"), true);
            list.forEach(command -> this.commands.add(parse(command)));
        }
    }

    @Override
    public void reward(Player player) {
        commands.forEach(command -> command.execute(player));
    }

    private enum CommandType {
        CONSOLE((player, command) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)),
        PLAYER(Bukkit::dispatchCommand),
        MESSAGE(MessageUtils::sendMessage);

        private final BiConsumer<Player, String> consumer;

        CommandType(BiConsumer<Player, String> consumer) {
            this.consumer = consumer;
        }
    }

    private static class Command {
        private final CommandType type;
        private final String string;

        private Command(CommandType type, String string) {
            this.type = type;
            this.string = string;
        }

        public void execute(Player player) {
            String replaced = string.replace("{player}", player.getName());
            type.consumer.accept(player, replaced);
        }
    }
}
