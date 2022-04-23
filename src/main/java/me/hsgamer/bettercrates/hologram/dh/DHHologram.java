package me.hsgamer.bettercrates.hologram.dh;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import me.hsgamer.bettercrates.api.hologram.Hologram;
import me.hsgamer.bettercrates.crate.CrateBlock;
import me.hsgamer.bettercrates.crate.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class DHHologram implements Hologram {
    private final CrateBlock crateBlock;
    private eu.decentsoftware.holograms.api.holograms.Hologram hologram;

    public DHHologram(CrateBlock crateBlock) {
        this.crateBlock = crateBlock;
    }

    private static String toLine(ItemStack item) {
        return HologramItem.fromItemStack(item).getContent();
    }

    @Override
    public void init() {
        hologram = DHAPI.createHologram(crateBlock.getCrate().getId() + "-" + UUID.randomUUID(), crateBlock.getLocation().add(0.5, crateBlock.getCrate().getOffSetY(), 0.5));
        reset();
    }

    @Override
    public void clear() {
        if (hologram != null) {
            hologram.delete();
        }
    }

    @Override
    public void reset() {
        DHAPI.setHologramLines(hologram, crateBlock.getCrate().getLines());
    }

    @Override
    public void setReward(Reward reward) {
        List<String> lines = List.of(
                reward.getDisplayName(),
                "#ICON: " + toLine(reward.getDisplayItem())
        );
        DHAPI.setHologramLines(hologram, lines);
    }
}
