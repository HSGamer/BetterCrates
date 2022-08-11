package me.hsgamer.bettercrates.hooks;

import com.jojodmo.itembridge.ItemBridge;
import lombok.experimental.UtilityClass;
import me.hsgamer.bettercrates.builder.ItemStackBuilder;
import org.bukkit.Bukkit;

import java.util.Optional;

@UtilityClass
public final class Hooks {
    public static void register() {
        if (Bukkit.getPluginManager().isPluginEnabled("ItemBridge")) {
            ItemStackBuilder.INSTANCE.register(entry ->
                            Optional.ofNullable(entry.getKey().get("itembridge"))
                                    .map(String::valueOf)
                                    .map(ItemBridge::getItemStack)
                                    .orElse(null),
                    "itembridge",
                    "item-bridge", "ib"
            );
        }
    }
}
