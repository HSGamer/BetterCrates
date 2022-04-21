package me.hsgamer.bettercrates.builder;

import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemBuilder extends Builder<Map<String, Object>, ItemStack> {
    public static final ItemBuilder INSTANCE = new ItemBuilder();

    private ItemBuilder() {
        register(map -> {
            Material material = Optional.ofNullable(map.get("material")).map(String::valueOf).map(Material::getMaterial).orElse(Material.STONE);
            int amount = Integer.parseInt(String.valueOf(map.getOrDefault("amount", "1")));
            String name = MessageUtils.colorize(String.valueOf(map.getOrDefault("name", "")));
            List<String> lore = Optional.ofNullable(map.get("lore"))
                    .map(o -> {
                        List<String> list = CollectionUtils.createStringListFromObject(o, false);
                        list.replaceAll(MessageUtils::colorize);
                        return list;
                    })
                    .orElse(null);
            ItemStack itemStack = new ItemStack(material, amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            return itemStack;
        }, "simple");
    }

    public static ItemStack buildItem(Map<String, Object> map) {
        String type = String.valueOf(map.getOrDefault("item-type", "simple"));
        return ItemBuilder.INSTANCE
                .build(type, map)
                .orElse(new ItemStack(Material.STONE));
    }
}
