package dev.tbm00.spigot.data64.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.tbm00.spigot.data64.Data64;
import dev.tbm00.spigot.data64.process.ResetProcess;

public class PlayerConnection implements Listener {
    private final Data64 javaPlugin;

    public PlayerConnection(Data64 javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Handles the player join event.
     * Triggers reset process if applicable.
     * (not applicable if already checked, reset, or newbie)
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new ResetProcess(javaPlugin, javaPlugin.getServer().getConsoleSender(), event.getPlayer(), false);
        if (event.getPlayer().hasPermission("group.one")) {
            javaPlugin.runCommand("lp user " + event.getPlayer().getName() + " permission unset group.default");
        }
    }
}