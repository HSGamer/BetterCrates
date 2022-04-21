package me.hsgamer.bettercrates;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions {
    public static final Permission BREAK = new Permission("bettercrates.break", PermissionDefault.OP);

    private Permissions() {
        throw new IllegalStateException("Utility class");
    }

    public static void register() {
        Bukkit.getPluginManager().addPermission(BREAK);
    }

    public static void unregister() {
        Bukkit.getPluginManager().removePermission(BREAK);
    }
}
