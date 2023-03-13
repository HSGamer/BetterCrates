package me.hsgamer.bettercrates.config;

import com.google.common.reflect.TypeToken;
import me.hsgamer.bettercrates.builder.ItemStackBuilder;
import me.hsgamer.bettercrates.config.converter.StringListConverter;
import me.hsgamer.bettercrates.config.converter.StringObjectMapConverter;
import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.bettercrates.crate.Reward;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.annotated.AnnotatedConfig;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.hscore.config.annotation.converter.manager.DefaultConverterManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class MainConfig extends AnnotatedConfig {
    static {
        //noinspection UnstableApiUsage
        DefaultConverterManager.registerConverter(new TypeToken<List<String>>() {
        }.getType(), new StringListConverter());
        //noinspection UnstableApiUsage
        DefaultConverterManager.registerConverter(new TypeToken<Map<String, Object>>() {
        }.getType(), new StringObjectMapConverter());
    }

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
    public final @ConfigPath("message.set-block-success") String setBlockSuccess;
    public final @ConfigPath("preview.title") String previewTitle;
    public final @ConfigPath("preview.lore-template") List<String> previewLoreTemplate;
    public final @ConfigPath("preview.previous-item") Map<String, Object> previewPreviousItem;
    public final @ConfigPath("preview.next-item") Map<String, Object> previewNextItem;
    public final @ConfigPath("preview.fill-item") Map<String, Object> previewFillItem;
    public final @ConfigPath("crate.default-lines") List<String> crateDefaultLines;
    public final @ConfigPath("crate.key-item") Map<String, Object> crateKeyItem;

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
        setBlockSuccess = "&aYou have set the block to be a crate block";

        previewTitle = "&4&lPreview Crate {name}";
        previewLoreTemplate = List.of(
                "{lore}",
                "",
                "&e&lChance: &f{chance}/{total-chance}"
        );
        previewPreviousItem = Map.of(
                "material", Material.ARROW.name(),
                "name", "&e&lPrevious Page"
        );
        previewNextItem = Map.of(
                "material", Material.ARROW.name(),
                "name", "&e&lNext Page"
        );
        previewFillItem = Map.of(
                "material", Material.BLACK_STAINED_GLASS_PANE.name()
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

    public ItemStack getCrateKey(String crateId, String displayName) {
        return ItemStackBuilder.INSTANCE
                .build(crateKeyItem,
                        (original, uuid) -> original
                                .replace("{crate}", crateId)
                                .replace("{crate-id}", displayName)
                )
                .orElse(new ItemStack(Material.STONE));
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
