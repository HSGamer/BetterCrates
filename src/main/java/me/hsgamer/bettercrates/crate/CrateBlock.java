package me.hsgamer.bettercrates.crate;

import lombok.Getter;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.opener.ChestOpener;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.unihologram.common.api.Hologram;
import me.hsgamer.unihologram.common.line.TextHologramLine;
import me.hsgamer.unihologram.spigot.SpigotHologramProvider;
import me.hsgamer.unihologram.spigot.common.line.ItemHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CrateBlock {
    private static SpigotHologramProvider hologramProvider;
    private final Location location;
    @Getter
    private final Crate crate;
    @Getter
    private final long delay;
    @Getter
    private final Hologram<Location> hologram;
    private final AtomicReference<BukkitTask> currentTask = new AtomicReference<>();

    public CrateBlock(Location location, Crate crate, long delay) {
        this.location = location;
        this.crate = crate;
        this.delay = delay;
        hologram = getHologramProvider().createHologram(
                crate.getId() + "-" + UUID.randomUUID(),
                location.clone().add(0.5, crate.getOffSetY(), 0.5)
        );
    }

    private static SpigotHologramProvider getHologramProvider() {
        if (hologramProvider == null) {
            hologramProvider = new SpigotHologramProvider();
        }
        return hologramProvider;
    }

    public void init() {
        hologram.init();
        hologram.setLines(crate.getLines().stream().map(TextHologramLine::new).collect(Collectors.toList()));
    }

    public void clear() {
        setBlockLid(false);
        hologram.clear();
        BukkitTask task = currentTask.getAndSet(null);
        if (task != null) {
            try {
                task.cancel();
            } catch (Exception ignored) {
                // IGNORED
            }
        }
    }

    private void setBlockLid(boolean open) {
        ChestOpener.INSTANCE.setBlockLid(location.getBlock(), open);
    }

    public CrateResponse open(Player player) {
        if (currentTask.get() != null) {
            return CrateResponse.DELAYING;
        }
        if (!crate.checkAndTakeKey(player)) {
            return CrateResponse.NOT_AFFORD;
        }

        BetterCrates plugin = JavaPlugin.getPlugin(BetterCrates.class);

        setBlockLid(true);
        Reward reward = crate.getRandomReward();
        hologram.setLines(Arrays.asList(
                new TextHologramLine(reward.getDisplayName()),
                new ItemHologramLine(reward.getDisplayItem())
        ));
        MessageUtils.sendMessage(player, plugin.getMainConfig().getRewardMessage(crate, reward));
        reward.getContents().forEach(rewardType -> rewardType.reward(player));

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            setBlockLid(false);
            hologram.setLines(crate.getLines().stream().map(TextHologramLine::new).collect(Collectors.toList()));
            currentTask.lazySet(null);
        }, delay);
        currentTask.set(task);

        return CrateResponse.SUCCESS;
    }

    public RawCrateBlock toRaw() {
        return new RawCrateBlock(location, crate.getId(), delay);
    }
}
