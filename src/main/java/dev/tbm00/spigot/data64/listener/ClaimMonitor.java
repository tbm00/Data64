package dev.tbm00.spigot.data64.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

import dev.tbm00.spigot.data64.Data64;
import dev.tbm00.spigot.data64.claimdata.EntryManager;

public class ClaimMonitor implements Listener {
    private final Data64 javaPlugin;

    public ClaimMonitor(Data64 javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("data64.cmd.claims")) return;

        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();

        Claim claimFrom = GriefDefender.getCore().getClaimAt(fromLocation.getWorld().getUID(), fromLocation.getBlockX(), fromLocation.getBlockY(), fromLocation.getBlockZ());
        Claim claimTo = GriefDefender.getCore().getClaimAt(toLocation.getWorld().getUID(), toLocation.getBlockX(), toLocation.getBlockY(), toLocation.getBlockZ());

        boolean fromMonitored = isMonitoredClaim(claimFrom);
        boolean toMonitored = isMonitoredClaim(claimTo);


        if (!fromMonitored && !toMonitored) return;
        else if (claimFrom == null && toMonitored) {
            alertStaffAndConsole(player, claimTo, true);
            return;
        } else if (claimTo == null && fromMonitored) {
            alertStaffAndConsole(player, claimFrom, false);
            return;
        } else if (claimFrom.getUniqueId().equals(claimTo.getUniqueId())) return;


        if (toMonitored) alertStaffAndConsole(player, claimTo, true);
        if (fromMonitored) alertStaffAndConsole(player, claimFrom, false);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("data64.cmd.claims")) return;

        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();

        Claim claimFrom = GriefDefender.getCore().getClaimAt(fromLocation.getWorld().getUID(), fromLocation.getBlockX(), fromLocation.getBlockY(), fromLocation.getBlockZ());
        Claim claimTo = GriefDefender.getCore().getClaimAt(toLocation.getWorld().getUID(), toLocation.getBlockX(), toLocation.getBlockY(), toLocation.getBlockZ());

        boolean fromMonitored = isMonitoredClaim(claimFrom);
        boolean toMonitored = isMonitoredClaim(claimTo);


        if (!fromMonitored && !toMonitored) return;
        else if (claimFrom == null && toMonitored) {
            alertStaffAndConsole(player, claimTo, true);
            return;
        } else if (claimTo == null && fromMonitored) {
            alertStaffAndConsole(player, claimFrom, false);
            return;
        } else if (claimFrom.getUniqueId().equals(claimTo.getUniqueId())) return;


        if (toMonitored) alertStaffAndConsole(player, claimTo, true);
        if (fromMonitored) alertStaffAndConsole(player, claimFrom, false);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Claim claim = GriefDefender.getCore().getClaimAt(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (claim!=null && !player.hasPermission("data64.cmd.claims") && isMonitoredClaim(claim)) {
            alertStaffAndConsole(player, claim, true);
        }
    }

    private void alertStaffAndConsole(Player player, Claim claim, boolean entry) {
        String string;
        if (entry) string = "Detected "+player.getName()+" in monitored claim: "+claim.getOwnerName() + "'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName();
        else string = "Detected "+player.getName()+" leaving monitored claim: "+claim.getOwnerName() + "'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName();
        
        String tpText = javaPlugin.getTpText(claim.getUniqueId());

        for (Player onlinePlayer : javaPlugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("data64.cmd.claims")) {
                TextComponent msg = new TextComponent(string);
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claim.getUniqueId()));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(tpText).create()));
                onlinePlayer.spigot().sendMessage(msg);
            }
        }
        javaPlugin.log(ChatColor.AQUA, string + " in " +javaPlugin.getClaimLocationText(claim.getUniqueId()));
    }

    private boolean isMonitoredClaim(Claim claim) {
        return claim != null && EntryManager.entryExists(claim.getUniqueId());
    }
}