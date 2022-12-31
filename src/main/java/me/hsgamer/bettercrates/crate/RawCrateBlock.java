package me.hsgamer.bettercrates.crate;

import lombok.Value;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Value
public class RawCrateBlock {
    Location location;
    String crateId;
    long delay;

    public static RawCrateBlock deserialize(Map<String, Object> map) {
        return new RawCrateBlock(
                Location.deserialize(map),
                Objects.toString(map.get("id"), ""),
                Optional.ofNullable(map.get("delay")).map(Object::toString).map(Long::parseLong).orElse(0L)
        );
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", crateId);
        map.put("delay", delay);
        map.putAll(location.serialize());
        return map;
    }

    public boolean isValid() {
        return crateId != null && !crateId.isEmpty() && location != null && location.getWorld() != null && !location.getBlock().getType().isAir();
    }
}
