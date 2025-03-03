

package dev.tbm00.spigot.reset64.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import dev.tbm00.spigot.reset64.Reset64;
import dev.tbm00.spigot.reset64.ConfigHandler;
import net.md_5.bungee.api.chat.TextComponent;

public class ResetCommand implements TabExecutor {
    private final Reset64 javaPlugin;
    private final ConfigHandler configHandler;
    private final String[] subCommands = new String[]{"sub"};

    public ResetCommand(Reset64 javaPlugin, ConfigHandler configHandler) {
        this.javaPlugin = javaPlugin;
        this.configHandler = configHandler;
    }

    /**
     * Handles the "/reset" command.
     * 
     * @param sender the command sender
     * @param consoleCommand the command being executed
     * @param label the label used for the command
     * @param args the arguments passed to the command
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return handleBaseCommand(sender);

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "sub":
                return handleSubCommand(sender, args);
            default:
                sendMessage(sender, ChatColor.RED + "Unknown subcommand!");
                return false;
        }
    }

    /**
     * Handles the base command for __.
     * 
     * @param sender the command sender
     * @param args the arguments passed to the command
     * @return true if command was processed successfully, false otherwise
     */
    private boolean handleBaseCommand(CommandSender sender) {
        if (hasPermission(sender, "reset64.cmd.base")) {
            sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        // do something
        return true;
    }

    /**
     * Handles the sub command for __.
     * 
     * @param sender the command sender
     * @param args the arguments passed to the command
     * @return true if command was processed successfully, false otherwise
     */
    private boolean handleSubCommand(CommandSender sender, String[] args) {
        if (hasPermission(sender, "reset64.cmd.sub")) {
            sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        // do something
        return true;
    }
    
    /**
     * Retrieves a player by their name.
     * 
     * @param arg the name of the player to retrieve
     * @return the Player object, or null if not found
     */
    private Player getPlayer(String arg) {
        return javaPlugin.getServer().getPlayer(arg);
    }

    /**
     * Checks if the sender has a specific permission.
     * 
     * @param sender the command sender
     * @param perm the permission string
     * @return true if the sender has the permission, false otherwise
     */
    private boolean hasPermission(CommandSender sender, String perm) {
        return sender.hasPermission(perm) || sender instanceof ConsoleCommandSender;
    }

    /**
     * Sends a message to a target CommandSender.
     * 
     * @param target the CommandSender to send the message to
     * @param string the message to send
     */
    private void sendMessage(CommandSender target, String string) {
        if (!string.isBlank())
            target.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getChatPrefix() + string)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.clear();
            for (String n : subCommands) {
                if (n!=null && n.startsWith(args[0])) 
                    list.add(n);
            }
        } else if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
        }
        return list;
    }
}