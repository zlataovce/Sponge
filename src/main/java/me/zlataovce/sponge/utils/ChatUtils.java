package me.zlataovce.sponge.utils;

import lombok.NonNull;
import org.bukkit.ChatColor;

public class ChatUtils {
    public static String color(@NonNull String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
