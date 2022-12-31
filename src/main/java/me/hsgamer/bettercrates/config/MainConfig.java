package me.hsgamer.bettercrates.config;

import me.hsgamer.bettercrates.builder.ItemStackBuilder;
import me.hsgamer.bettercrates.config.converter.StringListConverter;
import me.hsgamer.bettercrates.config.converter.StringObjectMapConverter;
import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.bettercrates.crate.Reward;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.annotated.AnnotatedConfig;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class MainConfig extends AnnotatedConfig {
    public final @ConfigPath("message.prefix") String prefix;
    public final @ConfigPath("message.player-only") String playerOnly;
    public final @ConfigPath("message.crate-not-found") String crateNotFound;
    public final @ConfigPath("message.player-not-found") String playerNotFound;
    public final @ConfigPath("message.block-required") String blockRequired;
    public final @ConfigPath("message.block-already-set") String blockAlreadySet;
    public final @ConfigPath("message.not-enough-key") String notEnoughKey;
    public final @ConfigPath("message.crate-delaying") String crateDelaying;
    public final @ConfigPath("message.reward") String rewardMessage;
    public final @ConfigPath("message.give-key-success") String giveKeySuccess;
    public final @ConfigPath("message.set-key-success") String setKeySuccess;
    public final @ConfigPath("message.set-key-fail") String setKeyFail;
    public final @ConfigPath("message.set-block-success") String setBlockSuccess;

    public final @ConfigPath("preview.title") String previewTitle;
    public final @ConfigPath(value = "preview.lore-template", converter = StringListConverter.class) List<String> previewLoreTemplate;
    public final @ConfigPath(value = "preview.previous-item", converter = StringObjectMapConverter.class) Map<String, Object> previewPreviousItem;
    public final @ConfigPath(value = "preview.next-item", converter = StringObjectMapConverter.class) Map<String, Object> previewNextItem;
    public final @ConfigPath(value = "preview.fill-item", converter = StringObjectMapConverter.class) Map<String, Object> previewFillItem;

    public final @ConfigPath(value = "crate.default-lines", converter = StringListConverter.class) List<String> crateDefaultLines;
    public final @ConfigPath(value = "crate.key-item", converter = StringObjectMapConverter.class) Map<String, Object> crateKeyItem;

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
        prefix = "&7[&bBetterCrates&7] &r";
        playerOnly = "&cPlayer only";
        crateNotFound = "&cCrate not found";
        playerNotFound = "&cPlayer not found";
        blockRequired = "&cYou have to look at a block to do that";
        blockAlreadySet = "&cThe block is already set";
        notEnoughKey = "&cNot enough key";
        crateDelaying = "&cCrate is being used by another player";
        rewardMessage = "&aYou have opened {crate} and received {reward}";
        giveKeySuccess = "&aYou have given {player} {amount} {name}";
        setKeySuccess = "&aYou have set the item in your hand as the key for {crate}";
        setKeyFail = "&cThe item in your hand is not valid as a key for {crate}";
        setBlockSuccess = "&aYou have set the block to be a crate block";

        previewTitle = "&4&lPreview Crate {name}";
        previewLoreTemplate = List.of(
                "{lore}",
                "",
                "&e&lChance: &f{chance}/{total-chance}"
        );
        previewPreviousItem = Map.of(
                "material", "ARROW",
                "name", "&e&lPrevious Page"
        );
        previewNextItem = Map.of(
                "material", "ARROW",
                "name", "&e&lNext Page"
        );
        previewFillItem = Map.of(
                "material", "AIR"
        );

        crateDefaultLines = List.of(
                "{display-name}",
                "",
                "&e&lLeft-click &fto preview",
                "&e&lRight-click &fto open"
        );
        crateKeyItem = Map.of(
                "material", Material.TRIPWIRE_HOOK.name(),
                "name", "&6&lKey {crate}",
                "lore", List.of(
                        "&fThis is a key to open {crate}",
                        "&7Id: {crate-id}"
                )
        );
    }

    public String getRewardMessage(Crate crate, Reward reward) {
        return this.rewardMessage
                .replace("{crate}", crate.getDisplayName())
                .replace("{reward}", reward.getDisplayName());
    }

    public ItemStack getCrateKey(Crate crate) {
        ItemStack key = ItemStackBuilder.INSTANCE
                .build(crateKeyItem,
                        (original, uuid) -> original
                                .replace("{crate}", crate.getDisplayName())
                                .replace("{crate-id}", crate.getId())
                )
                .orElse(new ItemStack(Material.STONE));
        crate.setCrateKey(key);
        return key;
    }

    public String getSetKeySuccess(Crate crate) {
        return this.setKeySuccess.replace("{crate}", crate.getDisplayName());
    }

    public String getSetKeyFail(Crate crate) {
        return this.setKeyFail.replace("{crate}", crate.getDisplayName());
    }

    public String getGiveKeySuccess(Player player, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        String displayName = itemMeta != null && itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemStack.getType().name();
        return giveKeySuccess
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(itemStack.getAmount()))
                .replace("{name}", displayName);
    }

    public String getPreviewTitle(Crate crate) {
        return previewTitle.replace("{name}", crate.getDisplayName());
    }

    public ItemStack getPreviewPreviousItem() {
        return ItemStackBuilder.INSTANCE.build(previewPreviousItem).orElse(new ItemStack(Material.ARROW));
    }

    public ItemStack getPreviewNextItem() {
        return ItemStackBuilder.INSTANCE.build(previewNextItem).orElse(new ItemStack(Material.ARROW));
    }

    public ItemStack getPreviewFillItem() {
        return ItemStackBuilder.INSTANCE.build(previewFillItem).orElse(new ItemStack(Material.AIR));
    }
}
