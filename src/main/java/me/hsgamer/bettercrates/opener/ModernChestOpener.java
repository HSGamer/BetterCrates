package me.hsgamer.bettercrates.opener;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;

public class ModernChestOpener implements ChestOpener {
    @Override
    public void setBlockLid(Block block, boolean open) {
        BlockState blockState = block.getState();
        if (blockState instanceof Lidded) {
            if (open) {
                ((Lidded) blockState).open();
            } else {
                ((Lidded) blockState).close();
            }
            blockState.update(true, false);
        }
    }
}
