package me.hsgamer.bettercrates.builder;

import fr.mrmicky.fastinv.ItemBuilder;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.bukkit.enchantments.Enchantment.DURABILITY;

public class ItemStackBuilder extends Builder<Map<String, Object>, ItemStack> {
    public static final ItemStackBuilder INSTANCE = new ItemStackBuilder();

    private ItemStackBuilder() {
        register(map -> {
            ItemBuilder builder = new ItemBuilder(Optional.ofNullable(map.get("material")).map(String::valueOf).map(Material::getMaterial).orElse(Material.STONE));
            builder.amount(Optional.ofNullable(map.get("amount")).map(String::valueOf).map(Integer::valueOf).orElse(1));
            Optional.ofNullable(map.get("name"))
                    .map(String::valueOf)
                    .map(MessageUtils::colorize)
                    .ifPresent(builder::name);
            Optional.ofNullable(map.get("lore"))
                    .map(o -> {
                        List<String> list = CollectionUtils.createStringListFromObject(o, false);
                        list.replaceAll(MessageUtils::colorize);
                        return list;
                    })
                    .ifPresent(builder::lore);
            Optional.ofNullable(map.get("data"))
                    .map(String::valueOf)
                    .map(Integer::parseInt)
                    .ifPresent(builder::data);
            Optional.ofNullable(map.get("durability"))
                    .map(String::valueOf)
                    .map(Short::parseShort)
                    .ifPresent(builder::durability);
            Optional.ofNullable(map.get("glow"))
                    .map(String::valueOf)
                    .map(Boolean::parseBoolean)
                    .ifPresent(b -> {
                        if (Boolean.TRUE.equals(b)) {
                            builder.enchant(DURABILITY, 1);
                            builder.flags(ItemFlag.HIDE_ENCHANTS);
                        }
                    });
            Optional.ofNullable(map.get("hide"))
                    .map(String::valueOf)
                    .map(Boolean::parseBoolean)
                    .ifPresent(b -> {
                        if (Boolean.TRUE.equals(b)) {
                            builder.flags(ItemFlag.values());
                        }
                    });
            return builder.build();
        }, "simple");
    }

    public static ItemStack buildItem(Map<String, Object> map) {
        String type = String.valueOf(map.getOrDefault("item-type", "simple"));
        return ItemStackBuilder.INSTANCE
                .build(type, map)
                .orElse(new ItemStack(Material.STONE));
    }
}
