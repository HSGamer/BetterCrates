package me.hsgamer.bettercrates.manager;

import com.lewdev.probabilitylib.ProbabilityCollection;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.api.reward.RewardContent;
import me.hsgamer.bettercrates.builder.ItemBuilder;
import me.hsgamer.bettercrates.builder.RewardContentBuilder;
import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.bettercrates.crate.CrateBlock;
import me.hsgamer.bettercrates.crate.CrateKey;
import me.hsgamer.bettercrates.crate.Reward;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class CrateManager {
    private final File crateFolder;
    private final File keyFolder;
    private final BukkitConfig blockConfig;
    private final Map<String, Crate> crateMap = new HashMap<>();
    private final Map<String, CrateKey> crateKeyMap = new HashMap<>();
    private final Map<Location, CrateBlock> crateBlockMap = new HashMap<>();

    public CrateManager(BetterCrates plugin) {
        crateFolder = new File(plugin.getDataFolder(), "crates");
        if (crateFolder.mkdirs()) {
            plugin.getLogger().info("Crate folder created");
        }
        keyFolder = new File(plugin.getDataFolder(), "keys");
        if (keyFolder.mkdirs()) {
            plugin.getLogger().info("Key folder created");
        }
        blockConfig = new BukkitConfig(plugin, "blocks.yml");
        blockConfig.setup();
    }

    public void init() {
        loadKeys();
        loadCrates();
        loadCrateBlocks();
        crateBlockMap.values().forEach(CrateBlock::init);
    }

    public void clear() {
        crateBlockMap.values().forEach(CrateBlock::init);
        crateBlockMap.clear();
        crateMap.clear();
        crateKeyMap.clear();
    }

    private void loadKeys() {
        for (File file : Objects.requireNonNull(keyFolder.listFiles())) {
            if (file.isFile()) {
                CrateKey key = loadKey(file);
                crateKeyMap.put(key.getId(), key);
            }
        }
    }

    private CrateKey loadKey(File file) {
        BukkitConfig config = new BukkitConfig(file);
        config.setup();
        Map<String, Object> itemMap = config.getNormalizedValues("item", false);
        return new CrateKey(file.getName(), ItemBuilder.buildItem(itemMap));
    }

    private void loadCrates() {
        for (File file : Objects.requireNonNull(crateFolder.listFiles())) {
            if (file.isFile()) {
                loadCrate(file).ifPresent(crate -> crateMap.put(crate.getId(), crate));
            }
        }
    }

    private Optional<Crate> loadCrate(File file) {
        BukkitConfig config = new BukkitConfig(file);
        config.setup();
        String id = file.getName();
        ProbabilityCollection<Reward> rewards = new ProbabilityCollection<>();
        List<String> lines = new ArrayList<>();
        double offsetY = 1.5;
        CrateKey crateKey = null;
        for (String key : config.getKeys(false)) {
            if (key.equals("settings")) {
                Map<String, Object> settings = config.getNormalizedValues(key, false);
                if (settings.containsKey("lines")) {
                    lines.addAll(CollectionUtils.createStringListFromObject(settings.get("lines"), false));
                } else if (settings.containsKey("key")) {
                    crateKey = crateKeyMap.get(String.valueOf(settings.get("key")));
                } else if (settings.containsKey("offset-y")) {
                    offsetY = Double.parseDouble(String.valueOf(settings.get("offset-y")));
                }
            } else {
                Map<String, Object> reward = config.getNormalizedValues(key, false);
                String displayName = MessageUtils.colorize(reward.containsKey("display-name") ? String.valueOf(reward.get("display-name")) : key);
                // noinspection unchecked
                Map<String, Object> displayItemMap = reward.containsKey("display-item") ? (Map<String, Object>) reward.get("display-item") : Collections.emptyMap();
                ItemStack displayItem = ItemBuilder.buildItem(displayItemMap);
                int chance = reward.containsKey("chance") ? Integer.parseInt(String.valueOf(reward.get("chance"))) : 100;
                List<RewardContent> contents = new ArrayList<>();
                Object rawContents = reward.get("contents");
                if (rawContents instanceof List) {
                    for (Object rawContent : (List<?>) rawContents) {
                        if (rawContent instanceof Map) {
                            // noinspection unchecked
                            Map<String, Object> contentMap = (Map<String, Object>) rawContent;
                            RewardContent content = RewardContentBuilder.buildContent(contentMap);
                            contents.add(content);
                        }
                    }
                } else if (rawContents instanceof Map) {
                    // noinspection unchecked
                    Map<String, Object> contentMap = (Map<String, Object>) rawContents;
                    RewardContent content = RewardContentBuilder.buildContent(contentMap);
                    contents.add(content);
                }
                rewards.add(new Reward(key, displayName, displayItem, contents), chance);
            }
        }
        lines.replaceAll(MessageUtils::colorize);
        if (rewards.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Crate(id, rewards, lines, offsetY, crateKey));
        }
    }

    private void loadCrateBlocks() {
        blockConfig.reload();
        List<String> blockIds = CollectionUtils.createStringListFromObject(blockConfig.get("blocks"), true);
        for (String string : blockIds) {
            String[] split = string.split(",", 6);
            if (split.length == 6) {
                Location location = new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                Crate crate = crateMap.get(split[4]);
                if (crate != null) {
                    int chance = Integer.parseInt(split[5]);
                    crateBlockMap.put(location, new CrateBlock(location, crate, chance));
                }
            }
        }
    }
}
