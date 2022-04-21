package me.hsgamer.bettercrates.config;

import me.hsgamer.bettercrates.config.converter.StringListConverter;
import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.bettercrates.crate.CrateKey;
import me.hsgamer.bettercrates.crate.Reward;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface MessageConfig {
    @ConfigPath("prefix")
    default String getPrefix() {
        return "&7[&bBetterCrates&7] &r";
    }

    @ConfigPath("player-only")
    default String getPlayerOnly() {
        return "&cPlayer only";
    }

    @ConfigPath("crate-not-found")
    default String getCrateNotFound() {
        return "&cCrate not found";
    }

    @ConfigPath("key-not-found")
    default String getKeyNotFound() {
        return "&cKey not found";
    }

    @ConfigPath("player-not-found")
    default String getPlayerNotFound() {
        return "&cPlayer not found";
    }

    @ConfigPath("block-required")
    default String getBlockRequired() {
        return "&cYou have to look at a block to do that";
    }

    @ConfigPath("block-already-set")
    default String getBlockAlreadySet() {
        return "&cThe block is already set";
    }

    @ConfigPath("not-enough-key")
    default String getNotEnoughKey() {
        return "&cNot enough key";
    }

    @ConfigPath("crate-delaying")
    default String getCrateDelaying() {
        return "&cCrate is being used by another player";
    }

    @ConfigPath("reward")
    default String getReward() {
        return "&aYou have opened {crate} and received {reward}";
    }

    default String getRewardMessage(Crate crate, Reward reward) {
        return getReward()
                .replace("{crate}", crate.getDisplayName())
                .replace("{reward}", reward.getDisplayName());
    }

    @ConfigPath("give-key-success")
    default String getGiveKeySuccess() {
        return "&aYou have given {player} {amount} {name} ({key})";
    }

    default String getGiveKeySuccess(Player player, CrateKey crateKey, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String displayName = itemMeta != null && itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemStack.getType().name();
        return getGiveKeySuccess()
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(itemStack.getAmount()))
                .replace("{name}", displayName)
                .replace("{key}", crateKey.getId());
    }

    @ConfigPath("set-block-success")
    default String getSetBlockSuccess() {
        return "&aYou have set the block to be a crate block";
    }

    @ConfigPath("preview-title")
    default String getPreviewTitle() {
        return "&4&lPreview Crate {name}";
    }

    default String getPreviewTitle(Crate crate) {
        return getPreviewTitle()
                .replace("{name}", crate.getDisplayName());
    }

    @ConfigPath(value = "preview-lore-template", converter = StringListConverter.class)
    default List<String> getPreviewLoreTemplate() {
        return List.of(
                "{lore}",
                "",
                "&e&lChance: &f{chance}/{total-chance}"
        );
    }
}
