package me.hsgamer.bettercrates.crate;

import com.lewdev.probabilitylib.ProbabilityCollection;
import lombok.Value;
import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.hscore.bukkit.key.PluginKeyPair;
import me.hsgamer.hscore.bukkit.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Value
public class Crate {
    String id;
    String displayName;
    ProbabilityCollection<Reward> rewards;
    List<String> lines;
    double offSetY;
    PluginKeyPair<Byte> crateKey;
    int crateKeyAmount;

    public boolean checkAndTakeKey(Player player) {
        if (crateKey == null) return true;
        ItemUtils.ItemCheckSession session = ItemUtils.createItemCheckSession(player.getInventory(), itemStack -> {
            ItemMeta meta = itemStack.getItemMeta();
            return meta != null && crateKey.get(meta) != 0;
        }, crateKeyAmount);
        if (!session.isAllMatched) return false;
        session.takeRunnable.run();
        return true;
    }

    public boolean setCrateKey(ItemStack itemStack) {
        if (crateKey == null) return true;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;
        crateKey.set(meta, (byte) 1);
        itemStack.setItemMeta(meta);
        return true;
    }

    public Reward getRandomReward() {
        if (rewards.isEmpty()) {
            throw new IllegalStateException("Crate " + id + " has no rewards");
        }
        return rewards.get();
    }

    public void openPreview(Player player) {
        BetterCrates plugin = JavaPlugin.getPlugin(BetterCrates.class);
        CratePreview previewInv = new CratePreview(plugin, this);
        previewInv.open(player);
    }
}
