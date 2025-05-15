

package dev.tbm00.spigot.data64.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import dev.tbm00.spigot.data64.Data64;
import dev.tbm00.spigot.data64.process.*;

public class DataCommand implements TabExecutor {
    private final Data64 javaPlugin;

    public DataCommand(Data64 javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Handles the "/reset" command.
     * 
     * @param sender the command sender
     * @param consoleCommand the command being executed
     * @param label the label used for the command
     * @param args the arguments passed to the command
     * @return {@code true} if the command was handled successfully, {@code false} otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        String subCmd = args[0].toLowerCase();
        switch (subCmd) {
            case "reset":
                return handleResetCmd(sender, args[1]);
            case "transfer":
                return handleTransferCmd(sender, args[1], args[2]);
            case "dsreset":
                return handleDSResetCmd(sender);
            default:
                return false;
        }
    }

    /**
     * Handles the reset command.
     * 
     * @param sender the command sender
     * @param name the player passed to the command
     */
    private boolean handleResetCmd(CommandSender sender, String name) {
        if (!hasPermission(sender, "data64.cmd.reset")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        Player target = getPlayer(name);
        if (target == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target player!");
            return true;
        } 

        new ResetProcess(javaPlugin, sender, target);
        return true;
    }

    /**
     * Handles the displayshop reset command.
     * 
     * @param sender the command sender
     */
    private boolean handleDSResetCmd(CommandSender sender) {
        if (!hasPermission(sender, "data64.cmd.dsreset")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        new DSProcess(javaPlugin);
        return true;
    }

    /**
     * Handles the reset command.
     * 
     * @param sender the command sender
     * @param nameA the username passed to the command (data pulled from)
     * @param nameB the username passed to the command (data pasted on)
     */
    private boolean handleTransferCmd(CommandSender sender, String nameA, String nameB) {
        if (!hasPermission(sender, "data64.cmd.transfer")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        Player playerA = getPlayer(nameA);
        if (playerA == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target (from) player!");
            return true;
        } 

        Player playerB = getPlayer(nameB);
        if (playerB == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target (to) player!");
            return true;
        } 

        new TransferProcess(javaPlugin, sender, playerA, playerB);
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
     * @return {@code true} if the sender has the permission, {@code false} otherwise
     */
    private boolean hasPermission(CommandSender sender, String perm) {
        return sender.hasPermission(perm) || sender instanceof ConsoleCommandSender;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            if (hasPermission(sender, "data64.cmd.reset")) {
                list.add("reset");
            }
            if (hasPermission(sender, "data64.cmd.reset")) {
                list.add("transfer");
            }
        } else if (args.length == 2) {
            if (hasPermission(sender, "data64.cmd.reset") || hasPermission(sender, "data64.cmd.transfer")) {
                Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
            }
        } else if (args.length == 3) {
            if (hasPermission(sender, "data64.cmd.transfer")) {
                Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
            }
        }
        return list;
    }
}