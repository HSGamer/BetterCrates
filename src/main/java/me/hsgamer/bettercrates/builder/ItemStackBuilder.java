package me.hsgamer.bettercrates.builder;

import com.google.gson.Gson;
import fr.mrmicky.fastinv.ItemBuilder;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

import static org.bukkit.enchantments.Enchantment.DURABILITY;

public class ItemStackBuilder extends MassBuilder<Map.Entry<Map<String, Object>, StringReplacer>, ItemStack> {
    public static final ItemStackBuilder INSTANCE = new ItemStackBuilder();
    private static final Gson GSON = new Gson();

    private ItemStackBuilder() {
        register(entry -> {
            Map<String, Object> map = entry.getKey();
            StringReplacer replacer = entry.getValue();
            Material material = Optional.ofNullable(map.get("material")).map(String::valueOf).map(Material::getMaterial).orElse(Material.STONE);
            ItemStack itemStack = new ItemStack(material);
            Optional<String> nbtOptional = Optional.ofNullable(map.get("nbt")).map(o -> {
                if (o instanceof Map) {
                    Map<?, ?> nbtMap = (Map<?, ?>) o;
                    return GSON.toJson(nbtMap);
                } else {
                    return Objects.toString(o, null);
                }
            }).map(replacer::replace);
            if (nbtOptional.isPresent()) {
                try {
                    itemStack = Bukkit.getUnsafe().modifyItemStack(itemStack, nbtOptional.get());
                } catch (Exception e) {
                    // EMPTY
                }
            }
            ItemBuilder builder = new ItemBuilder(itemStack);
            builder.amount(Optional.ofNullable(map.get("amount")).map(String::valueOf).map(Integer::valueOf).orElse(1));
            Optional.ofNullable(map.get("name"))
                    .map(String::valueOf)
                    .map(replacer::replace)
                    .map(MessageUtils::colorize)
                    .ifPresent(builder::name);
            Optional.ofNullable(map.get("lore"))
                    .map(o -> {
                        List<String> list = CollectionUtils.createStringListFromObject(o, false);
                        list.replaceAll(replacer::replace);
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
        }, "simple", "");
    }

    public void register(Function<Map.Entry<Map<String, Object>, StringReplacer>, ItemStack> creator, String... name) {
        register(new Element<>() {
            @Override
            public boolean canBuild(Map.Entry<Map<String, Object>, StringReplacer> input) {
                String type = Objects.toString(input.getKey().get("item-type"), "");
                for (String s : name) {
                    if (s.equalsIgnoreCase(type)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public ItemStack build(Map.Entry<Map<String, Object>, StringReplacer> input) {
                return creator.apply(input);
            }
        });
    }

    public Optional<ItemStack> build(Map<String, Object> map, StringReplacer replacer) {
        return build(new AbstractMap.SimpleEntry<>(map, replacer));
    }

    public Optional<ItemStack> build(Map<String, Object> map) {
        return build(map, (original, uuid) -> original);
    }
}
