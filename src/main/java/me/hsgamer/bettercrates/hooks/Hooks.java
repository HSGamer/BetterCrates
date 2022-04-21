package me.hsgamer.bettercrates.hooks;

import com.jojodmo.itembridge.ItemBridge;
import me.hsgamer.bettercrates.builder.ItemBuilder;
import org.bukkit.Bukkit;

import java.util.Optional;

public final class Hooks {
    private Hooks() {
        throw new IllegalStateException("Utility class");
    }

    public static void register() {
        if (Bukkit.getPluginManager().isPluginEnabled("ItemBridge")) {
            ItemBuilder.INSTANCE.register(map ->
                            Optional.ofNullable(map.get("itembridge"))
                                    .map(String::valueOf)
                                    .map(ItemBridge::getItemStack)
                                    .orElse(null),
                    "itembridge",
                    "item-bridge", "ib"
            );
        }
    }
}
