package me.hsgamer.bettercrates.crate;

import com.lewdev.probabilitylib.ProbabilityCollection;
import fr.mrmicky.fastinv.FastInv;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class CratePreview extends FastInv {
    private static final int ITEM_SIZE = 18;
    private static final int INV_SIZE = ITEM_SIZE + 9;
    private final BetterCrates plugin;
    private final Crate crate;
    private final List<ItemStack> displayItems;
    private final AtomicInteger currentPage = new AtomicInteger(0);
    private final int maxPage;

    public CratePreview(BetterCrates plugin, Crate crate) {
        super(INV_SIZE, ColorUtils.colorize(plugin.getMainConfig().getPreviewTitle(crate)));
        this.plugin = plugin;
        this.crate = crate;
        displayItems = initDisplayItems();
        maxPage = (int) Math.ceil((double) displayItems.size() / ITEM_SIZE);
        setupHotbar();
        update();
    }

    private List<ItemStack> initDisplayItems() {
        List<ItemStack> items = new ArrayList<>();
        ProbabilityCollection<Reward> rewards = crate.getRewards();
        int totalChance = rewards.getTotalProbability();
        AtomicInteger totalFakeChance = new AtomicInteger(0);
        rewards.iterator().forEachRemaining(reward -> totalFakeChance.addAndGet(reward.getObject().getFakeChance()));
        rewards.iterator().forEachRemaining(reward -> {
            ItemStack displayItem = reward.getObject().getDisplayItem().clone();
            int fakeChance = reward.getObject().getFakeChance();
            int chance = reward.getProbability();

            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                meta = meta.clone();
                List<String> lore = Optional.ofNullable(meta.getLore()).orElse(Collections.emptyList());
                List<String> finalLore = new ArrayList<>();
                for (String s : plugin.getMainConfig().previewLoreTemplate) {
                    if (s.equals("{lore}")) {
                        finalLore.addAll(lore);
                    } else {
                        String line = s
                                .replace("{chance}", Integer.toString(chance))
                                .replace("{fake-chance}", Integer.toString(fakeChance))
                                .replace("{total-chance}", Integer.toString(totalChance))
                                .replace("{total-fake-chance}", Integer.toString(totalFakeChance.get()));
                        finalLore.add(ColorUtils.colorize(line));
                    }
                }
                meta.setLore(finalLore.isEmpty() ? null : finalLore);
                displayItem.setItemMeta(meta);
            }
            items.add(displayItem);
        });
        return Collections.unmodifiableList(items);
    }

    private void setupHotbar() {
        setItem(INV_SIZE - 9, plugin.getMainConfig().getPreviewPreviousItem(), e -> {
            int page = currentPage.get();
            if (page > 0) {
                currentPage.set(page - 1);
                update();
            }
        });
        setItem(INV_SIZE - 1, plugin.getMainConfig().getPreviewNextItem(), e -> {
            int page = currentPage.get();
            if (page < maxPage - 1) {
                currentPage.set(page + 1);
                update();
            }
        });
        setItems(INV_SIZE - 8, INV_SIZE - 2, plugin.getMainConfig().getPreviewFillItem());
    }

    private void update() {
        int page = currentPage.get();
        for (int i = 0; i < ITEM_SIZE; i++) {
            int index = page * ITEM_SIZE + i;
            if (index < displayItems.size()) {
                setItem(i, displayItems.get(index));
            } else {
                setItem(i, null);
            }
        }
    }
}
