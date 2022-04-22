package me.hsgamer.bettercrates.hologram.none;

import me.hsgamer.bettercrates.api.hologram.Hologram;
import me.hsgamer.bettercrates.crate.CrateBlock;
import me.hsgamer.bettercrates.crate.Reward;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

public class NoneHologram implements Hologram {
    private final CrateBlock crateBlock;
    private final AtomicReference<Item> itemRef = new AtomicReference<>(null);

    public NoneHologram(CrateBlock crateBlock) {
        this.crateBlock = crateBlock;
    }

    @Override
    public void clear() {
        reset();
    }

    @Override
    public void reset() {
        Item item = itemRef.getAndSet(null);
        if (item != null && item.isValid()) {
            item.remove();
        }
    }

    @Override
    public void setReward(Reward reward) {
        Location location = crateBlock.getLocation().add(0.5, crateBlock.getCrate().getOffSetY(), 0.5);
        ItemStack displayItem = reward.getDisplayItem();
        World world = location.getWorld();
        assert world != null;
        Item item = world.spawn(location, Item.class, i -> {
            i.setItemStack(displayItem);
            i.setPickupDelay(Integer.MAX_VALUE);
            i.setGravity(false);
            i.setInvulnerable(true);
            i.setOwner(null);
            i.setSilent(true);
            i.setCustomNameVisible(true);
            i.setCustomName(reward.getDisplayName());
            i.setPersistent(false);
            i.setThrower(null);
        });
        itemRef.set(item);
    }
}
