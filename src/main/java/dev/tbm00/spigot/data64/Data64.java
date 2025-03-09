package dev.tbm00.spigot.data64;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.TextComponent;

import xzot1k.plugins.ds.DisplayShops;
import xzot1k.plugins.ds.DisplayShopsAPI;

import com.olziedev.playerwarps.api.PlayerWarpsAPI;

import dev.tbm00.spigot.data64.command.DataCommand;
import dev.tbm00.spigot.data64.hook.GDHook;
import dev.tbm00.spigot.data64.listener.PlayerConnection;
import dev.tbm00.spigot.data64.process.DSProcess;

public class Data64 extends JavaPlugin {
    private ConfigHandler configHandler;
    public static DisplayShopsAPI dsHook;
    public static GDHook gdHook;
    public static Essentials essHook;
    public static PlayerWarpsAPI pwHook;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final PluginDescriptionFile pdf = this.getDescription();

		log(ChatColor.LIGHT_PURPLE,
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-",
            pdf.getName() + " v" + pdf.getVersion() + " created by tbm00",
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
		);

        if (getConfig().contains("enabled") && getConfig().getBoolean("enabled")) {
            configHandler = new ConfigHandler(this);
                
            setupHooks();

            // Register Command
            getCommand("dataadmin").setExecutor(new DataCommand(this));

            if (configHandler.isJoinResetEnabled()) {
                // Register Listener
                getServer().getPluginManager().registerEvents(new PlayerConnection(this), this);
            }

            if (configHandler.isDSResetEnabled()) {
                // Register Listener
                new DSProcess(this);
            }
        }
    }

    /**
     * Sets up the required hooks for plugin integration.
     * Disables the plugin if any required hook fails.
     */
    private void setupHooks() {
        if (!setupGriefDefender()) {
            getLogger().severe("GriefDefender hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupDisplayShops()) {
            getLogger().severe("DisplayShops hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupEssentials()) {
            getLogger().severe("Essentials hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupPlayerWarps()) {
            getLogger().severe("PlayerWarps hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }
    }

    /**
     * Attempts to hook into the GriefDefender plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupGriefDefender() {
        if (!isPluginAvailable("GriefDefender")) return false;

        Data64.gdHook = new GDHook(this);

        log(ChatColor.GREEN, "GriefDefender hooked.");
        return true;
    }

    /**
     * Attempts to hook into the DisplayShops plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupDisplayShops() {
        if (!isPluginAvailable("DisplayShops")) return false;

        Data64.dsHook = (DisplayShops) getServer().getPluginManager().getPlugin("DisplayShops");
        
        log(ChatColor.GREEN, "DisplayShops hooked.");
        return true;
    }

    /**
     * Attempts to hook into the Essentials plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupEssentials() {
        if (!isPluginAvailable("Essentials")) return false;

        Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials.isEnabled() && essentials instanceof Essentials)
            essHook = (Essentials) essentials;
        else return false;

        log(ChatColor.GREEN, "Essentials hooked.");
        return true;
    }

    /**
     * Attempts to hook into the PlayerWarps plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupPlayerWarps() {
        if (!isPluginAvailable("PlayerWarps")) return false;

        pwHook = PlayerWarpsAPI.getInstance();

        log(ChatColor.GREEN, "PlayerWarps hooked.");
        return true;
    }

    /**
     * Checks if the specified plugin is available and enabled on the server.
     *
     * @param pluginName the name of the plugin to check
     * @return {@code true} if the plugin is available and enabled, {@code false} otherwise
     */
    private boolean isPluginAvailable(String pluginName) {
		final Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
		return plugin != null && plugin.isEnabled();
	}

    /**
     * Disables the plugin.
     */
    private void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        log(ChatColor.RED, "Data64 disabled..!");
    }

    /**
     * Logs one or more messages to the server console with the prefix & specified chat color.
     *
     * @param chatColor the chat color to use for the log messages
     * @param strings one or more message strings to log
     */
    public void log(ChatColor chatColor, String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage("[Data64] " + chatColor + s);
	}

    /**
     * Sends a message to a target CommandSender.
     * 
     * @param target the CommandSender to send the message to
     * @param string the message to send
     */
    public void sendMessage(CommandSender target, String string) {
        if (!string.isBlank())
            target.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getChatPrefix() + string)));
    }

    /**
     * Executes a command as the console.
     * 
     * @param command the command to execute
     * @return {@code true} if the command was successfully executed, {@code false} otherwise
     */
    public boolean runCommand(String command) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        try {
            return Bukkit.dispatchCommand(console, command);
        } catch (Exception e) {
            log(ChatColor.RED, "Caught exception running command " + command + ": " + e.getMessage());
            return false;
        }
    }
}