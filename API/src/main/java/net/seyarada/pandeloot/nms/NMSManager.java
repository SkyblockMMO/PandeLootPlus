package net.seyarada.pandeloot.nms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NMSManager {

    public static final String CONSOLE_ACCENT = "\u001b[38;5;180m";
    public static final String CONSOLE_ACCENT_RESET = "\u001b[0m";
    public static final String PLUGIN_NAME = "PandeLoot";
    public static final String DECORATED_NAME = CONSOLE_ACCENT + "[" + PLUGIN_NAME + "] " + CONSOLE_ACCENT_RESET;

    static final Map<Integer, List<UUID>> hiddenItems = new ConcurrentHashMap<>();
    static NMSMethods nms;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // Get full package string of CraftServer.
        // org.bukkit.craftbukkit.version (or org.bukkit.craftbukkit for 1.20.5+)
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        // Get the last element of the package

        Bukkit.getLogger().info(DECORATED_NAME + "Detected package version: " + version);

        // Handle versions without package suffix (1.20.5+)
        if (version.equals("craftbukkit") || version.equals("craftserver")) {
            String mcVersion = Bukkit.getVersion();
            Bukkit.getLogger().info(DECORATED_NAME + "Minecraft version: " + mcVersion);
            // Map Minecraft version to NMS version format
            // 1.21.10 -> v1_21_R6
            if (mcVersion != null) {
                if (mcVersion.startsWith("1.21.10") || mcVersion.equals("1.21.10")) {
                    version = "v1_21_R6";
                } else if (mcVersion.startsWith("1.21")) {
                    version = "v1_21_R6"; // Default to R6 for 1.21.x
                } else {
                    // Fallback: try to parse version
                    version = "v" + mcVersion.replace(".", "_") + "_R1";
                }
            } else {
                // If getMinecraftVersion() returns null, try to detect from server version string
                String serverVersion = Bukkit.getVersion();
                Bukkit.getLogger().info(DECORATED_NAME + "Server version: " + serverVersion);
                if (serverVersion != null && serverVersion.contains("1.21.10")) {
                    version = "v1_21_R6";
                }
            }
            Bukkit.getLogger().info(DECORATED_NAME + "Mapped to NMS version: " + version);
        }

        String className = "net.seyarada.pandeloot.nms." + version + "." + version.toUpperCase();
        Bukkit.getLogger().info(DECORATED_NAME + "Attempting to load class: " + className);

        try {

            // Check if we have a NMSHandler class at that location.

            nms = new v1_21_R6();
            Bukkit.getLogger().info(DECORATED_NAME + "Loading support for " + version);

        } catch (final Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe(DECORATED_NAME + "Could not find support for this CraftBukkit version: " + className);
        }
    }


    public static void addHiddenItem(int id, List<UUID> players) {
        hiddenItems.put(id, players);
    }

    public static void removeHiddenItem(int id) {
        hiddenItems.remove(id);
    }

    public static boolean isHiddenFor(int id, UUID p) {
        if (hiddenItems.containsKey(id)) {
            return !hiddenItems.get(id).contains(p);
        }
        return false;
    }

    public static NMSMethods get() {
        return nms;
    }

}
