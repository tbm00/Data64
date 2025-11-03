package dev.tbm00.spigot.data64;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.TextComponent;

import com.earth2me.essentials.Essentials;
import com.olziedev.playerwarps.api.PlayerWarpsAPI;
import net.milkbowl.vault.economy.Economy;
import net.brcdev.gangs.GangsPlugin;
import de.Keyle.MyPet.MyPetPlugin;
import me.pulsi_.bankplus.BankPlus;
import net.slipcor.pvpstats.PVPStats;

import dev.tbm00.spigot.data64.hook.*;
import dev.tbm00.papermc.playershops64.PlayerShops64;
import dev.tbm00.spigot.data64.command.DataCommand;
import dev.tbm00.spigot.data64.listener.PlayerConnection;
import dev.tbm00.spigot.logger64.Logger64;

public class Data64 extends JavaPlugin {
    private ConfigHandler configHandler;
    public static PlayerShops64 psHook;
    public static GDHook gdHook;
    public static Essentials essHook;
    public static PlayerWarpsAPI pwHook;
    public static GangsPlugin gangHook;
    public static PetHook petHook;
    public static BankPlus bankHook;
    public static PVPStats pvpHook;
    public static Economy ecoHook;
    public Logger64 logHook;

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
        }
    }

    /**
     * Sets up the required hooks for plugin integration.
     * Disables the plugin if any required hook fails.
     */
    private void setupHooks() {
        if (!setupVault()) {
            getLogger().severe("Vault hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupGriefDefender()) {
            getLogger().severe("GriefDefender hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupPlayerShops64()) {
            getLogger().severe("PlayerShops64 hook failed -- disabling plugin!");
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

        if (!setupGangsPlus()) {
            getLogger().severe("GangsPlus hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupMyPet()) {
            getLogger().severe("MyPet hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupBankPlus()) {
            getLogger().severe("BankPlus hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupPVPStats()) {
            getLogger().severe("PVPStats hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupLogger64()) {
            getLogger().severe("Logger64 hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }
    }

    private boolean setupVault() {
        if (!isPluginAvailable("Vault")) return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        ecoHook = rsp.getProvider();
        if (ecoHook == null) return false;

        log(ChatColor.GREEN, "Vault hooked.");
        return true;
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
     * Attempts to hook into the PlayerShops64 plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupPlayerShops64() {
        if (!isPluginAvailable("PlayerShops64")) return false;

        Data64.psHook = (PlayerShops64) getServer().getPluginManager().getPlugin("PlayerShops64");

        log(ChatColor.GREEN, "PlayerShops64 hooked.");
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
     * Attempts to hook into the GangsPlus plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupGangsPlus() {
        if (!isPluginAvailable("GangsPlus")) return false;

        Plugin gangp = Bukkit.getPluginManager().getPlugin("GangsPlus");
        if (gangp.isEnabled() && gangp instanceof GangsPlugin)
            gangHook = (GangsPlugin) gangp;
        else return false;

        log(ChatColor.GREEN, "GangsPlus hooked.");
        return true;
    }

    /**
     * Attempts to hook into the MyPet plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupMyPet() {
        if (!isPluginAvailable("MyPet")) return false;

        Plugin petp = Bukkit.getPluginManager().getPlugin("MyPet");
        if (petp.isEnabled()) {
            petHook = new PetHook((MyPetPlugin) petp);
        } else return false;

        log(ChatColor.GREEN, "MyPet hooked.");
        return true;
    }

    /**
     * Attempts to hook into the BankPlus plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupBankPlus() {
        if (!isPluginAvailable("BankPlus")) return false;

        Plugin bankp = Bukkit.getPluginManager().getPlugin("BankPlus");
        if (bankp.isEnabled()) {
            bankHook = (BankPlus) bankp;
        } else return false;

        log(ChatColor.GREEN, "BankPlus hooked.");
        return true;
    }

    /**
     * Attempts to hook into the PVPStats plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupPVPStats() {
        if (!isPluginAvailable("PVPStats")) return false;

        Plugin pvpp = Bukkit.getPluginManager().getPlugin("PVPStats");
        if (pvpp.isEnabled() && pvpp instanceof PVPStats)
            pvpHook = (PVPStats) pvpp;
        else return false;

        log(ChatColor.GREEN, "PVPStats hooked.");
        return true;
    }

    /**
     * Attempts to hook into the Logger64 plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupLogger64() {
        if (!isPluginAvailable("Logger64")) return false;

        Plugin logger64 = Bukkit.getPluginManager().getPlugin("Logger64");
        if (logger64.isEnabled() && logger64 instanceof Logger64)
            logHook = (Logger64) logger64;
        else return false;

        log(ChatColor.GREEN, "Logger64 hooked.");
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

    /**
     * Executes a command as the console after waiting.
     * 
     * 
     * @param command the command to execute
     * @param delay the tick delay
     * @return {@code true} if the command was successfully executed, {@code false} otherwise
     */
    public boolean runCommandDelayed(String command, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runCommand(command);
            }
        }.runTaskLater(this, delay);
        return true;
    }
}