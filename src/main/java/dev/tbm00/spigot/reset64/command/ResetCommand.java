

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

import net.md_5.bungee.api.chat.TextComponent;

import dev.tbm00.spigot.reset64.Reset64;
import dev.tbm00.spigot.reset64.process.ResetProcess;
import dev.tbm00.spigot.reset64.ConfigHandler;

public class ResetCommand implements TabExecutor {
    private final Reset64 javaPlugin;
    private final ConfigHandler configHandler;

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
     * @return {@code true} if the command was handled successfully, {@code false} otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender, "reset64.cmd")) {
            sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (args.length == 0) return false;

        Player target = getPlayer(args[0]);
        if (target == null) {
            sendMessage(sender, ChatColor.RED + "Could not find target player!");
            return true;
        } 
        
        handleResetCmd(sender, target);
        return true;

    }

    /**
     * Handles the reset command.
     * 
     * @param sender the command sender
     * @param player the player passed to the command
     */
    private void handleResetCmd(CommandSender sender, Player player) {
        new ResetProcess(javaPlugin, sender, player);
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
        if (hasPermission(sender, "reset64.cmd") && args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
        }
        return list;
    }
}