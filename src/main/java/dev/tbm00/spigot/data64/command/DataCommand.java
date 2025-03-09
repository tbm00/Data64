

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
import dev.tbm00.spigot.data64.process.ResetProcess;

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
        if (!hasPermission(sender, "data64.cmd")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (args.length == 0) return false;

        String subCmd = args[0].toLowerCase();
        switch (subCmd) {
            case "reset":
                return handleResetCmd(sender, args[0]);
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
        Player target = getPlayer(name);
        if (target == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target player!");
            return true;
        } 

        new ResetProcess(javaPlugin, sender, target);
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
        if (hasPermission(sender, "data64.cmd") && args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
        }
        return list;
    }
}