package me.hsgamer.bettercrates.crate;

import com.lewdev.probabilitylib.ProbabilityCollection;
import fr.mrmicky.fastinv.FastInv;
import lombok.Value;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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
        BetterCrates plugin = JavaPlugin.getPlugin(BetterCrates.class);
        int chestSize = getChestSize(rewards.size());
        FastInv previewInv = new FastInv(chestSize, MessageUtils.colorize(plugin.getMessageConfig().getPreviewTitle(this)));

        int totalChance = rewards.getTotalProbability();
        AtomicInteger totalFakeChance = new AtomicInteger(0);
        rewards.iterator().forEachRemaining(reward -> totalFakeChance.addAndGet(reward.getObject().getFakeChance()));
        rewards.iterator().forEachRemaining(reward -> {
            ItemStack displayItem = reward.getObject().getDisplayItem().clone();
            int fakeChance = reward.getObject().getFakeChance();
            int chance = reward.getProbability();

            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                List<String> lore = Optional.ofNullable(meta.getLore()).orElse(Collections.emptyList());
                List<String> finalLore = new ArrayList<>();
                for (String s : plugin.getMessageConfig().getPreviewLoreTemplate()) {
                    if (s.equals("{lore}")) {
                        finalLore.addAll(lore);
                    } else {
                        String line = s
                                .replace("{chance}", Integer.toString(chance))
                                .replace("{fake-chance}", Integer.toString(fakeChance))
                                .replace("{total-chance}", Integer.toString(totalChance))
                                .replace("{total-fake-chance}", Integer.toString(totalFakeChance.get()));
                        finalLore.add(MessageUtils.colorize(line));
                    }
                }
                meta.setLore(finalLore.isEmpty() ? null : finalLore);
                displayItem.setItemMeta(meta);
            }
            previewInv.addItem(displayItem);
        });
        previewInv.open(player);
    }
}
