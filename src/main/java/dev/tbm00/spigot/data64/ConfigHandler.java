package dev.tbm00.spigot.data64;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;

public class ConfigHandler {
    private final Data64 javaPlugin;
    private String chatPrefix;
    private boolean joinResetEnabled = false;
    private boolean dsResetEnabled = false;

    /**
     * Constructs a ConfigHandler instance.
     * Loads configuration values for the plugin, including settings for PvP toggling and fixes.
     *
     * @param javaPlugin the main plugin instance
     */
    public ConfigHandler(Data64 javaPlugin) {
        this.javaPlugin = javaPlugin;
        try {
            loadLanguageSection();
            loadResetOnJoinSec();
            loadDSResetSec();
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

    /**
     * Loads the "displayShopReset" section of the configuration.
     */
    private void loadDSResetSec() {
        ConfigurationSection section = javaPlugin.getConfig().getConfigurationSection("displayShopReset");
        if (section!=null)
            dsResetEnabled = section.contains("enabled") ? section.getBoolean("enabled") : false;
    }

    public String getChatPrefix() {
        return chatPrefix;
    }

    public boolean isJoinResetEnabled() {
        return joinResetEnabled;
    }

    public boolean isDSResetEnabled() {
        return dsResetEnabled;
    }
}