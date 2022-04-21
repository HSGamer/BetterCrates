package me.hsgamer.bettercrates.crate;

import com.lewdev.probabilitylib.ProbabilityCollection;
import fr.mrmicky.fastinv.FastInv;
import lombok.Value;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Value
public class Crate {
    String id;
    String displayName;
    ProbabilityCollection<Reward> rewards;
    List<String> lines;
    double offSetY;
    CrateKey crateKey;

    private static int getChestSize(int rawSize) {
        return (rawSize / 9) + (rawSize % 9 == 0 ? 0 : 9);
    }

    public Reward getRandomReward() {
        if (rewards.isEmpty()) {
            throw new IllegalStateException("Crate " + id + " has no rewards");
        }
        return rewards.get();
    }

    public void openPreview(Player player) {
        List<ItemStack> displayItems = new ArrayList<>();
        rewards.iterator().forEachRemaining(reward -> displayItems.add(reward.getObject().getDisplayItem()));
        int chestSize = getChestSize(displayItems.size());
        FastInv previewInv = new FastInv(chestSize, MessageUtils.colorize(JavaPlugin.getPlugin(BetterCrates.class).getMessageConfig().getPreviewTitle(this)));
        displayItems.forEach(previewInv::addItem);
        previewInv.open(player);
    }
}
