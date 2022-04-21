package me.hsgamer.bettercrates.listener;

import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.Permissions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CrateListener implements Listener {
    private final BetterCrates plugin;

    public CrateListener(BetterCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.getCrateManager().getCrateBlock(block.getLocation()).isPresent());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.getCrateManager().getCrateBlock(block.getLocation()).isPresent());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getCrateManager().getCrateBlock(event.getBlock().getLocation()).isPresent()) {
            if (event.getPlayer().hasPermission(Permissions.BREAK)) {
                plugin.getCrateManager().removeCrateBlock(event.getBlock().getLocation());
            } else {
                event.setCancelled(true);
            }
        }
    }
}
