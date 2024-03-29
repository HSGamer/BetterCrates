package me.hsgamer.bettercrates.listener;

import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.crate.CrateBlock;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public class InteractListener implements Listener {
    private final BetterCrates plugin;

    public InteractListener(BetterCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        assert block != null;

        Optional<CrateBlock> optionalCrateBlock = plugin.getCrateManager().getCrateBlock(block.getLocation());
        if (optionalCrateBlock.isEmpty()) return;
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND) return; // WHY??
        CrateBlock crateBlock = optionalCrateBlock.get();

        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (crateBlock.open(player)) {
                case DELAYING:
                    MessageUtils.sendMessage(player, plugin.getMainConfig().crateDelaying);
                    break;
                case NOT_AFFORD:
                    MessageUtils.sendMessage(player, plugin.getMainConfig().notEnoughKey);
                    break;
                default:
                    break;
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                event.setCancelled(false);
            } else {
                crateBlock.getCrate().openPreview(player);
            }
        }
    }
}
