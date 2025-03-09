package dev.tbm00.spigot.data64;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.TextComponent;

import dev.tbm00.spigot.data64.command.DataCommand;
import dev.tbm00.spigot.data64.listener.PlayerConnection;
import dev.tbm00.spigot.data64.process.DSProcess;

public class Data64 extends JavaPlugin {
    private ConfigHandler configHandler;

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
        if (!setupBlankHook()) {
            getLogger().severe("BlankHook failed -- disabling plugin!");
            disablePlugin();
            return;
        }
    }

    /**
     * Attempts to hook into the BlankHook plugin.
     *
     * @return {@code true} if the hook was successful, {@code false} otherwise
     */
    private boolean setupBlankHook() {
        //if (getServer().getPluginManager().getPlugin("BlankHook")==null) return false;

        //BlankHook = new BlankHook();
        
        log(ChatColor.GREEN, "BlankHook hooked.");
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