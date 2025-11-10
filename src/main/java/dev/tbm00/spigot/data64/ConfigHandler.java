package dev.tbm00.spigot.data64;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;

public class ConfigHandler {
    private final Data64 javaPlugin;
    private String chatPrefix;
    private boolean joinResetEnabled = false;

    /**
     * Constructs a ConfigHandler instance.
     *
     * @param javaPlugin the main plugin instance
     */
    public ConfigHandler(Data64 javaPlugin) {
        this.javaPlugin = javaPlugin;
        try {
            loadLanguageSection();
            loadResetOnJoinSec();
        } catch (Exception e) {
            javaPlugin.log(ChatColor.RED, "Caught exception loading config: " + e.getMessage());
        }
    }

    /**
     * Loads the "lang" section of the configuration.
     */
    private void loadLanguageSection() {
        ConfigurationSection section = javaPlugin.getConfig().getConfigurationSection("lang");
        if (section!=null)
            chatPrefix = section.contains("prefix") ? section.getString("prefix") : null;
    }

    /**
     * Loads the "resetOnJoin" section of the configuration.
     */
    private void loadResetOnJoinSec() {
        ConfigurationSection section = javaPlugin.getConfig().getConfigurationSection("resetOnJoin");
        if (section!=null)
            joinResetEnabled = section.contains("enabled") ? section.getBoolean("enabled") : false;
    }


    public String getChatPrefix() {
        return chatPrefix;
    }

    public boolean isJoinResetEnabled() {
        return joinResetEnabled;
    }
}