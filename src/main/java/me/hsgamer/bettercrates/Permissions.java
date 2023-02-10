package me.hsgamer.bettercrates;

import lombok.experimental.UtilityClass;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

@UtilityClass
public final class Permissions {
    public static final Permission BREAK = new Permission("bettercrates.break", PermissionDefault.OP);
    public static final Permission GIVE_KEY = new Permission("bettercrates.givekey", PermissionDefault.OP);
    public static final Permission SET = new Permission("bettercrates.set", PermissionDefault.OP);
}
