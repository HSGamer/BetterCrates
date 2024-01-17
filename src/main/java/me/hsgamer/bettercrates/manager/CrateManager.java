package me.hsgamer.bettercrates.manager;

import com.lewdev.probabilitylib.ProbabilityCollection;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.api.RewardContent;
import me.hsgamer.bettercrates.builder.ItemStackBuilder;
import me.hsgamer.bettercrates.builder.RewardContentBuilder;
import me.hsgamer.bettercrates.crate.Crate;
import me.hsgamer.bettercrates.crate.CrateBlock;
import me.hsgamer.bettercrates.crate.RawCrateBlock;
import me.hsgamer.bettercrates.crate.Reward;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.PathString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class CrateManager {
    private final File crateFolder;
    private final BukkitConfig blockConfig;
    private final Map<String, Crate> crateMap = new HashMap<>();
    private final Map<Location, CrateBlock> crateBlockMap = new HashMap<>();
    private final BetterCrates plugin;

    public CrateManager(BetterCrates plugin) {
        crateFolder = new File(plugin.getDataFolder(), "crates");
        if (crateFolder.mkdirs()) {
            plugin.getLogger().info("Crate folder created");
        }
        blockConfig = new BukkitConfig(plugin, "blocks.yml");
        blockConfig.setup();
        this.plugin = plugin;
    }

    public void init() {
        loadCrates();
        loadCrateBlocks();
        crateBlockMap.values().forEach(CrateBlock::init);
    }

    public void clear() {
        crateBlockMap.values().forEach(CrateBlock::clear);
        crateBlockMap.clear();
        crateMap.clear();
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
        String crateDisplayName = id;
        ProbabilityCollection<Reward> rewards = new ProbabilityCollection<>();
        List<String> lines = new ArrayList<>(plugin.getMainConfig().crateDefaultLines);
        double offsetY = 2.3;
        ItemStack crateKey = null;
        int crateKeyAmount = 1;
        for (PathString key : config.getKeys(false)) {
            if (key.equals("settings")) {
                Map<String, Object> settings = PathString.toPathMap(config.getNormalizedValues(key, false));
                if (settings.containsKey("lines")) {
                    lines.clear();
                    lines.addAll(CollectionUtils.createStringListFromObject(settings.get("lines"), false));
                }
                if (settings.containsKey("offset-y")) {
                    offsetY = Double.parseDouble(String.valueOf(settings.get("offset-y")));
                }
                if (settings.containsKey("display-name")) {
                    crateDisplayName = ColorUtils.colorize(String.valueOf(settings.get("display-name")));
                }
                if (Boolean.parseBoolean(Objects.toString(settings.get("use-crate-key")))) {
                    if (settings.containsKey("crate-key")) {
                        // noinspection unchecked
                        Map<String, Object> crateKeyMap = (Map<String, Object>) settings.get("crate-key");
                        crateKey = ItemStackBuilder.INSTANCE.build(crateKeyMap).orElse(null);
                    } else {
                        crateKey = plugin.getMainConfig().getCrateKey(id, crateDisplayName);
                    }
                }
                if (settings.containsKey("crate-key-amount")) {
                    crateKeyAmount = Integer.parseInt(String.valueOf(settings.get("crate-key-amount")));
                }
            } else {
                Map<String, Object> reward = PathString.toPathMap(config.getNormalizedValues(key, false));
                // noinspection unchecked
                Map<String, Object> displayItemMap = reward.containsKey("display-item") ? (Map<String, Object>) reward.get("display-item") : Collections.emptyMap();
                ItemStack displayItem = ItemStackBuilder.INSTANCE.build(displayItemMap).orElse(new ItemStack(Material.STONE));
                String rewardDisplayName;
                if (reward.containsKey("display-name")) {
                    rewardDisplayName = ColorUtils.colorize(String.valueOf(reward.get("display-name")));
                } else {
                    ItemMeta meta = displayItem.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {
                        rewardDisplayName = meta.getDisplayName();
                    } else {
                        rewardDisplayName = PathString.toPath(key);
                    }
                }
                int chance = reward.containsKey("chance") ? Integer.parseInt(String.valueOf(reward.get("chance"))) : 100;
                int fakeChance = reward.containsKey("fake-chance") ? Integer.parseInt(String.valueOf(reward.get("fake-chance"))) : chance;
                List<RewardContent> contents = new ArrayList<>();
                Object rawContents = reward.get("contents");
                if (rawContents instanceof List) {
                    for (Object rawContent : (List<?>) rawContents) {
                        if (rawContent instanceof Map) {
                            // noinspection unchecked
                            Map<String, Object> contentMap = (Map<String, Object>) rawContent;
                            RewardContentBuilder.INSTANCE.build(contentMap).ifPresent(contents::add);
                        }
                    }
                } else if (rawContents instanceof Map) {
                    // noinspection unchecked
                    Map<String, Object> contentMap = (Map<String, Object>) rawContents;
                    RewardContentBuilder.INSTANCE.build(contentMap).ifPresent(contents::add);
                }
                rewards.add(new Reward(PathString.toPath(key), rewardDisplayName, fakeChance, displayItem, contents), chance);
            }
        }
        lines.replaceAll(ColorUtils::colorize);
        String finalCrateDisplayName = crateDisplayName;
        lines.replaceAll(s -> s.replace("{display-name}", finalCrateDisplayName));
        if (rewards.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Crate(id, crateDisplayName, rewards, lines, offsetY, crateKey, crateKeyAmount));
        }
    }

    private void loadCrateBlocks() {
        blockConfig.reload();
        List<RawCrateBlock> rawCrateBlocks = new ArrayList<>();
        Object blocksObject = blockConfig.getNormalized(new PathString("blocks"));
        if (blocksObject instanceof List) {
            List<?> blocks = (List<?>) blocksObject;
            for (Object rawBlock : blocks) {
                if (!(rawBlock instanceof Map)) continue;
                Map<String, Object> blockMap = new HashMap<>();
                ((Map<?, ?>) rawBlock).forEach((key, value) -> blockMap.put(Objects.toString(key), value));
                rawCrateBlocks.add(RawCrateBlock.deserialize(blockMap));
            }
        }
        for (RawCrateBlock rawCrateBlock : rawCrateBlocks) {
            if (!rawCrateBlock.isValid()) continue;
            Crate crate = crateMap.get(rawCrateBlock.getCrateId());
            if (crate != null) {
                Location location = rawCrateBlock.getLocation();
                CrateBlock crateBlock = new CrateBlock(location, crate, rawCrateBlock.getDelay());
                crateBlockMap.put(location, crateBlock);
            }
        }
        saveCrateBlocks();
    }

    private void saveCrateBlocks() {
        List<Map<String, Object>> blocks = new ArrayList<>();
        for (CrateBlock crateBlock : crateBlockMap.values()) {
            blocks.add(crateBlock.toRaw().serialize());
        }
        blockConfig.set(new PathString("blocks"), blocks);
        blockConfig.save();
    }

    public Optional<CrateBlock> getCrateBlock(Location location) {
        return Optional.ofNullable(crateBlockMap.get(location.getBlock().getLocation()));
    }

    public void removeCrateBlock(Location location) {
        Optional.ofNullable(crateBlockMap.remove(location.getBlock().getLocation()))
                .ifPresent(crateBlock -> {
                    crateBlock.clear();
                    saveCrateBlocks();
                });
    }

    public void addCrateBlock(Location location, Crate crate, long delay) {
        CrateBlock crateBlock = new CrateBlock(location, crate, delay);
        crateBlock.init();
        crateBlockMap.put(location.getBlock().getLocation(), crateBlock);
        saveCrateBlocks();
    }

    public Optional<Crate> getCrate(String id) {
        return Optional.ofNullable(crateMap.get(id));
    }

    public List<String> getCrateIds() {
        return new ArrayList<>(crateMap.keySet());
    }
}
