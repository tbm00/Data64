package dev.tbm00.spigot.data64.claimdata;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EntryManager {
    private static JavaPlugin javaPlugin;
    private static JSONHandler db;
    private static Set<UUID> entries;

    public EntryManager(JavaPlugin javaPlugin, JSONHandler db) {
        EntryManager.javaPlugin = javaPlugin;
        EntryManager.db = db;

        Set<UUID> loaded = db.loadEntries();
        Set<UUID> concurrentSet = ConcurrentHashMap.newKeySet();
        concurrentSet.addAll(loaded);
        EntryManager.entries = concurrentSet;
    }

    // returns if the uuid entry for username exists
    public static boolean entryExists(UUID uuid) {
        return entries.contains(uuid);
    }

    // creates uuid entry in json & map if DNE
    // updates uuid entry in json & map if it does exist
    public static void createEntry(UUID uuid) {
        if (entries.add(uuid)) {
            saveEntriesAsync();
        }
    }

    // removes uuid entry from json & map
    public static void deleteEntry(UUID uuid) {
        if (entries.remove(uuid)) {
            saveEntriesAsync();
        }
    }

    // get uuid entries from map
    public static Set<UUID> getEntries() {
        return entries;
    }

    private static void saveEntriesAsync() {
        Set<UUID> snapshot;
        synchronized (entries) {
            snapshot = new HashSet<>(entries);
        }
        Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> db.saveEntries(snapshot));
    }

    // on plugin disable
    public static void close() {
        Set<UUID> snapshot;
        synchronized (entries) {
            snapshot = new HashSet<>(entries);
        }
        db.saveEntries(snapshot);
    }
}