package me.hsgamer.bettercrates;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions {
    public static final Permission BREAK = new Permission("bettercrates.break", PermissionDefault.OP);
    public static final Permission GIVE_KEY = new Permission("bettercrates.givekey", PermissionDefault.OP);
    public static final Permission SET = new Permission("bettercrates.set", PermissionDefault.OP);

    private Permissions() {
        throw new IllegalStateException("Utility class");
    }

    public static void register() {
        Bukkit.getPluginManager().addPermission(BREAK);
        Bukkit.getPluginManager().addPermission(GIVE_KEY);
        Bukkit.getPluginManager().addPermission(SET);
    }

    public static void unregister() {
        Bukkit.getPluginManager().removePermission(BREAK);
        Bukkit.getPluginManager().removePermission(GIVE_KEY);
        Bukkit.getPluginManager().removePermission(SET);
    }
}
