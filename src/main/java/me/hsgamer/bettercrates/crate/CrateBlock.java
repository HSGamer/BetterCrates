package me.hsgamer.bettercrates.crate;

import lombok.Getter;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.api.hologram.Hologram;
import me.hsgamer.bettercrates.manager.HologramProviderManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class CrateBlock {
    @Getter
    private final Location location;
    @Getter
    private final Crate crate;
    private final long delay;
    @Getter
    private final Hologram hologram;
    private final AtomicBoolean isOpen = new AtomicBoolean(false);

    public CrateBlock(Location location, Crate crate, long delay) {
        this.location = location;
        this.crate = crate;
        this.delay = delay;
        hologram = HologramProviderManager.getHologramProvider().createHologram(this);
    }

    public void init() {
        hologram.init();
    }

    public void clear() {
        setBlockLid(false);
        hologram.clear();
    }

    private void setBlockLid(boolean open) {
        BlockState blockState = location.getBlock().getState();
        if (blockState instanceof Lidded) {
            if (open) {
                ((Lidded) blockState).open();
            } else {
                ((Lidded) blockState).close();
            }
            blockState.update(true, false);
        }
    }

    public boolean open(Player player) {
        if (isOpen.get()) {
            return false;
        }
        setBlockLid(true);
        isOpen.set(true);

        Reward reward = crate.getRandomReward();
        hologram.setReward(reward);
        reward.getContents().forEach(rewardType -> rewardType.reward(player));

        BetterCrates plugin = JavaPlugin.getPlugin(BetterCrates.class);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            setBlockLid(false);
            hologram.reset();
            isOpen.lazySet(false);
        }, delay);
        return true;
    }
}
