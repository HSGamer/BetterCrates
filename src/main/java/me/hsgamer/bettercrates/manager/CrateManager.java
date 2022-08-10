package me.hsgamer.bettercrates.manager;

import com.lewdev.probabilitylib.ProbabilityCollection;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.api.reward.RewardContent;
import me.hsgamer.bettercrates.builder.ItemStackBuilder;
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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CrateManager {
    private final File crateFolder;
    private final File keyFolder;
    private final BukkitConfig blockConfig;
    private final Map<String, Crate> crateMap = new HashMap<>();
    private final Map<String, CrateKey> crateKeyMap = new HashMap<>();
    private final Map<Location, CrateBlock> crateBlockMap = new HashMap<>();
    private final BetterCrates plugin;

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
        this.plugin = plugin;
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
        Map<String, Object> itemMap = config.getNormalizedValues(false);
        return new CrateKey(file.getName(), ItemStackBuilder.INSTANCE.build(itemMap).orElse(new ItemStack(Material.STONE)));
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
        List<String> lines = new ArrayList<>(plugin.getMessageConfig().getDefaultLines());
        double offsetY = 2.3;
        CrateKey crateKey = null;
        for (String key : config.getKeys(false)) {
            if (key.equals("settings")) {
                Map<String, Object> settings = config.getNormalizedValues(key, false);
                if (settings.containsKey("lines")) {
                    lines.clear();
                    lines.addAll(CollectionUtils.createStringListFromObject(settings.get("lines"), false));
                }
                if (settings.containsKey("key")) {
                    crateKey = crateKeyMap.get(String.valueOf(settings.get("key")));
                }
                if (settings.containsKey("offset-y")) {
                    offsetY = Double.parseDouble(String.valueOf(settings.get("offset-y")));
                }
                if (settings.containsKey("display-name")) {
                    crateDisplayName = MessageUtils.colorize(String.valueOf(settings.get("display-name")));
                }
            } else {
                Map<String, Object> reward = config.getNormalizedValues(key, false);
                // noinspection unchecked
                Map<String, Object> displayItemMap = reward.containsKey("display-item") ? (Map<String, Object>) reward.get("display-item") : Collections.emptyMap();
                ItemStack displayItem = ItemStackBuilder.INSTANCE.build(displayItemMap).orElse(new ItemStack(Material.STONE));
                String rewardDisplayName;
                if (reward.containsKey("display-name")) {
                    rewardDisplayName = MessageUtils.colorize(String.valueOf(reward.get("display-name")));
                } else {
                    ItemMeta meta = displayItem.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {
                        rewardDisplayName = meta.getDisplayName();
                    } else {
                        rewardDisplayName = key;
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
                rewards.add(new Reward(key, rewardDisplayName, fakeChance, displayItem, contents), chance);
            }
        }
        lines.replaceAll(MessageUtils::colorize);
        String finalCrateDisplayName = crateDisplayName;
        lines.replaceAll(s -> s.replace("{display-name}", finalCrateDisplayName));
        if (rewards.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Crate(id, crateDisplayName, rewards, lines, offsetY, crateKey));
        }
    }

    private void loadCrateBlocks() {
        blockConfig.reload();
        List<String> blockIds = CollectionUtils.createStringListFromObject(blockConfig.get("blocks"), true);
        for (String string : blockIds) {
            String[] split = string.split(",", 6);
            if (split.length == 6) {
                Location location = new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                if (location.getBlock().getType().isAir()) continue;
                Crate crate = crateMap.get(split[4]);
                if (crate != null) {
                    int delay = Integer.parseInt(split[5]);
                    crateBlockMap.put(location, new CrateBlock(location, crate, delay));
                }
            }
        }
        saveCrateBlocks();
    }

    private void saveCrateBlocks() {
        blockConfig.set("blocks", crateBlockMap.values().stream().map(crateBlock -> Objects.requireNonNull(crateBlock.getLocation().getWorld()).getName() + "," + crateBlock.getLocation().getBlockX() + "," + crateBlock.getLocation().getBlockY() + "," + crateBlock.getLocation().getBlockZ() + "," + crateBlock.getCrate().getId() + "," + crateBlock.getDelay()).collect(Collectors.toList()));
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

    public Optional<CrateKey> getCrateKey(String id) {
        return Optional.ofNullable(crateKeyMap.get(id));
    }

    public List<String> getCrateKeyIds() {
        return new ArrayList<>(crateKeyMap.keySet());
    }
}
