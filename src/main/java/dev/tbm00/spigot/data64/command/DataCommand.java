package dev.tbm00.spigot.data64.command;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.lib.flowpowered.math.vector.Vector3i;

import dev.tbm00.spigot.data64.Data64;
import dev.tbm00.spigot.data64.claimdata.EntryManager;
import dev.tbm00.spigot.data64.process.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
            case "force-reset":
                return handleForceResetCmd(sender, args[1]);
            case "transfer":
                return handleTransferCmd(sender, args[1], args[2]);
            case "find-trusted":
                return handleFindTrustedCmd(((Player) sender), args[1]);
            case "find-trusts":
                return handleFindTrustsCmd(((Player) sender), args[1]);
            case "find-claims":
                return handleFindClaimsCmd(((Player) sender), args[1]);
            case "monitor-add":
                return handleAddCommand(sender, args);
            case "monitor-remove":
                return handleRemoveCommand(sender, args);
            case "monitor-check":
                return handleCheckCommand(sender, args);
            case "monitor-list":
                return handleListCommand(sender);
            case "claim-id":
                return handleClaimUuidCommand(((Player) sender));
            case "claim-tp":
                return handleClaimTpCmd(((Player) sender), args[1]);
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
    private boolean handleForceResetCmd(CommandSender sender, String name) {
        if (!hasPermission(sender, "data64.cmd.reset")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (name==null || name.isBlank()) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin force-reset <player>");
            return true;
        }

        Player target = getPlayer(name);
        if (target == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target player!");
            return true;
        } 

        new ResetProcess(javaPlugin, sender, target, true);
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

    private boolean handleAddCommand(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (!javaPlugin.configHandler.isClaimMonitorEnabled()) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Claim monitoring is disabled!");
            return true;
        }

        if (args.length!=2) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin monitor-add <player>/<claimUuid>/current");
            return true;
        }

        if (args[1].equalsIgnoreCase("current")) {
            Location playerLoc = ((Player) sender).getLocation();
            Claim claim = GriefDefender.getCore().getClaimAt(playerLoc.getWorld().getUID(), playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());

            if (claim!=null && !claim.isWilderness()) {
                UUID claimUuid = claim.getUniqueId();
                if (!EntryManager.entryExists(claimUuid)) {
                    EntryManager.createEntry(claimUuid);
                    javaPlugin.sendMessage(sender, "Added 1 claim to the monitor-list!");
                } else {
                    javaPlugin.sendMessage(sender, "This claim is already being monitored!");
                }
            } else {
                javaPlugin.sendMessage(sender, "You're not standing in a claim!");
            }
            return true;
        }

        UUID targetClaim = null;
        OfflinePlayer targetPlayer = null;
        try {
            targetClaim = UUID.fromString(args[1]);
        } catch (Exception e) {
            targetClaim = null;
            try {
                targetPlayer = javaPlugin.getServer().getOfflinePlayer(args[1]);
            } catch (Exception e2) {
                javaPlugin.sendMessage(sender, "Couldn't find player or claim from "+args[1]);
                return true;
            }
        }

        if (targetClaim==null && targetPlayer!=null) {
            List<Claim> claims = GriefDefender.getCore().getAllPlayerClaims(targetPlayer.getUniqueId());
            int count = 0;
            for (Claim claim : claims) {
                UUID claimUuid = claim.getUniqueId();
                if (!EntryManager.entryExists(claimUuid)) {
                    EntryManager.createEntry(claimUuid);
                    count++;
                }
            }
            javaPlugin.sendMessage(sender, "Added "+count+" claims to the monitor-list!");
            return true;
        } else if (targetClaim!=null && targetPlayer==null) {
            Claim claim = GriefDefender.getCore().getClaim(targetClaim);

            if (claim!=null) {
                UUID claimUuid = claim.getUniqueId();
                if (!EntryManager.entryExists(claimUuid)) {
                    EntryManager.createEntry(claimUuid);
                    javaPlugin.sendMessage(sender, "Added 1 claim to the monitor-list!");
                    return true;
                } else {
                    javaPlugin.sendMessage(sender, "This claim is already being monitored!");
                    return true;
                }
            }
        }

        javaPlugin.sendMessage(sender, "Couldn't find player or claim from "+args[1]);
        return true;
    }

    private boolean handleRemoveCommand(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (!javaPlugin.configHandler.isClaimMonitorEnabled()) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Claim monitoring is disabled!");
            return true;
        }

        if (args.length!=2) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin monitor-remove <player>/<claimUuid>/current");
            return true;
        }

        if (args[1].equalsIgnoreCase("current")) {
            Location playerLoc = ((Player) sender).getLocation();
            Claim claim = GriefDefender.getCore().getClaimAt(playerLoc.getWorld().getUID(), playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());

            if (claim!=null && !claim.isWilderness()) {
                UUID claimUuid = claim.getUniqueId();
                if (EntryManager.entryExists(claimUuid)) {
                    EntryManager.deleteEntry(claimUuid);
                    javaPlugin.sendMessage(sender, "Deleted 1 claim from the monitor-list!");
                } else {
                    javaPlugin.sendMessage(sender, "This claim is not being monitored already!");
                }
            } else {
                javaPlugin.sendMessage(sender, "You're not standing in a claim!");
            }
            return true;
        }

        UUID targetClaim = null;
        OfflinePlayer targetPlayer = null;
        try {
            targetClaim = UUID.fromString(args[1]);
        } catch (Exception e) {
            targetClaim = null;
            try {
                targetPlayer = javaPlugin.getServer().getOfflinePlayer(args[1]);
            } catch (Exception e2) {
                javaPlugin.sendMessage(sender, "Couldn't find player or claim from "+args[1]);
                return true;
            }
        }

        if (targetClaim==null && targetPlayer!=null) {
            List<Claim> claims = GriefDefender.getCore().getAllPlayerClaims(targetPlayer.getUniqueId());
            int count = 0;
            for (Claim claim : claims) {
                UUID claimUuid = claim.getUniqueId();
                if (EntryManager.entryExists(claimUuid)) {
                    EntryManager.deleteEntry(claimUuid);
                    count++;
                }
            }
            javaPlugin.sendMessage(sender, "Deleted "+count+" claims from the monitor-list!");
            return true;
        } else if (targetClaim!=null && targetPlayer==null) {
            Claim claim = GriefDefender.getCore().getClaim(targetClaim);

            if (claim!=null) {
                UUID claimUuid = claim.getUniqueId();
                if (EntryManager.entryExists(claimUuid)) {
                    EntryManager.deleteEntry(claimUuid);
                    javaPlugin.sendMessage(sender, "Deleted 1 claim from the monitor-list!");
                    return true;
                } else {
                    javaPlugin.sendMessage(sender, "This claim is not being monitored already!");
                    return true;
                }
            }
        }

        javaPlugin.sendMessage(sender, "Couldn't find player or claim from "+args[1]);
        return true;
    }

    private boolean handleCheckCommand(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (!javaPlugin.configHandler.isClaimMonitorEnabled()) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Claim monitoring is disabled!");
            return true;
        }

        if (args.length!=2) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin monitor-check <player>/<claimUuid>/current");
            return true;
        }

        if (args[1].equalsIgnoreCase("current")) {
            Location playerLoc = ((Player) sender).getLocation();
            Claim claim = GriefDefender.getCore().getClaimAt(playerLoc.getWorld().getUID(), playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());

            if (claim!=null && !claim.isWilderness()) {
                UUID claimUuid = claim.getUniqueId();
                if (EntryManager.entryExists(claimUuid)) {
                    TextComponent msg = new TextComponent("Monitoring " + claim.getOwnerName() + "'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName());
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claimUuid));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(javaPlugin.getTpText(claimUuid)).create()));
                    sender.spigot().sendMessage(msg);
                } else {
                    javaPlugin.sendMessage(sender, "This claim is not being monitored!");
                }
            } else {
                javaPlugin.sendMessage(sender, "You're not standing in a claim!");
            }
            return true;
        }

        UUID targetClaim = null;
        OfflinePlayer targetPlayer = null;
        try {
            targetClaim = UUID.fromString(args[1]);
        } catch (Exception e) {
            targetClaim = null;
            try {
                targetPlayer = javaPlugin.getServer().getOfflinePlayer(args[1]);
            } catch (Exception e2) {
                javaPlugin.sendMessage(sender, "Couldn't find player or claim from "+args[1]);
                return true;
            }
        }

        if (targetClaim==null && targetPlayer!=null) {
            List<Claim> claims = GriefDefender.getCore().getAllPlayerClaims(targetPlayer.getUniqueId());
            
            javaPlugin.sendMessage(sender, ChatColor.GREEN + "Monitoring "+targetPlayer.getName()+"'s claims:");
            for (Claim claim : claims) {
                UUID claimUuid = claim.getUniqueId();
                if (EntryManager.entryExists(claimUuid)) {
                    TextComponent msg = new TextComponent("- "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName());
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claimUuid));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(javaPlugin.getTpText(claimUuid)).create()));
                    sender.spigot().sendMessage(msg);
                }
            }
            return true;
        } else if (targetClaim!=null && targetPlayer==null) {
            Claim claim = GriefDefender.getCore().getClaim(targetClaim);

            if (claim!=null) {
                UUID claimUuid = claim.getUniqueId();
                if (EntryManager.entryExists(claimUuid)) {
                    TextComponent msg = new TextComponent("Monitoring " + claim.getOwnerName() + "'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName());
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claimUuid));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(javaPlugin.getTpText(claimUuid)).create()));
                    sender.spigot().sendMessage(msg);
                    return true;
                } else {
                    javaPlugin.sendMessage(sender, "This claim is not being monitored!");
                    return true;
                }
            }
        }

        javaPlugin.sendMessage(sender, "Couldn't find player or claim from "+args[1]);
        return true;
    }

    private boolean handleListCommand(CommandSender sender) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (!javaPlugin.configHandler.isClaimMonitorEnabled()) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Claim monitoring is disabled!");
            return true;
        }

        javaPlugin.sendMessage(sender, ChatColor.GREEN + "All Monitored Claims:");

        List<Claim> claims = GriefDefender.getCore().getAllClaims();
        for (Claim claim : claims) {
            UUID claimUuid = claim.getUniqueId();
            if (EntryManager.entryExists(claimUuid)) {
                TextComponent msg = new TextComponent("- "+ claim.getOwnerName() + "'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName());
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claimUuid));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(javaPlugin.getTpText(claimUuid)).create()));
                sender.spigot().sendMessage(msg);
            }
        }
        return true;
    }

    /**
     * Handles the find-trusts command.
     * 
     * @param sender the command sender
     * @param name the player passed to the command
     */
    private boolean handleFindTrustedCmd(Player sender, String name) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (name==null || name.isBlank()) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin find-trusted <player>");
            return true;
        }

        OfflinePlayer target = javaPlugin.getServer().getOfflinePlayer(name);
        if (target == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target player!");
            return true;
        } 

        UUID targetUuid = target.getUniqueId();

        Core gd = GriefDefender.getCore();
        List<Claim> claims = gd.getAllPlayerClaims(targetUuid);

        javaPlugin.sendMessage(sender, ChatColor.GREEN + "Players that "+name+" has trusted in their claims:");
        for (Claim claim : claims) {
            Set<UUID> trustedUuids = claim.getUserTrusts();

            UUID claimUuid = claim.getUniqueId();
            TextComponent msg = new TextComponent("- "+claim.getOwnerName()+"'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claimUuid));
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(javaPlugin.getTpText(claimUuid)).create()));
            sender.spigot().sendMessage(msg);

            for (UUID trustedUuid : trustedUuids) {
                sender.sendMessage(ChatColor.GRAY + "  - "+javaPlugin.getServer().getOfflinePlayer(trustedUuid).getName());
            }
        }

        return true;
    }

    /**
     * Handles the find-trusts command.
     * 
     * @param sender the command sender
     * @param name the player passed to the command
     */
    private boolean handleFindTrustsCmd(Player sender, String name) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (name==null || name.isBlank()) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin find-trusts <player>");
            return true;
        }

        OfflinePlayer target = javaPlugin.getServer().getOfflinePlayer(name);
        if (target == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target player!");
            return true;
        } 

        UUID targetUuid = target.getUniqueId();

        Core gd = GriefDefender.getCore();
        List<Claim> claims = gd.getAllClaims();

        javaPlugin.sendMessage(sender, ChatColor.GREEN + "Claims that "+name+" is trusted in:");
        for (Claim claim : claims) {
            Set<UUID> trustedUuids = claim.getUserTrusts();
            UUID claimUuid = claim.getUniqueId();
            if (trustedUuids.contains(targetUuid)) {
                TextComponent msg = new TextComponent("- trusted in " + claim.getOwnerName() + "'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName());
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claimUuid));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(javaPlugin.getTpText(claimUuid)).create()));
                sender.spigot().sendMessage(msg);
            }
        }

        return true;
    }

    /**
     * Handles the find-claims command.
     * 
     * @param sender the command sender
     * @param name the player passed to the command
     */
    private boolean handleFindClaimsCmd(Player sender, String name) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (name==null || name.isBlank()) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin find-claims <player>");
            return true;
        }

        OfflinePlayer target = javaPlugin.getServer().getOfflinePlayer(name);
        if (target == null) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Could not find target player!");
            return true;
        } 

        UUID targetUuid = target.getUniqueId();

        Core gd = GriefDefender.getCore();
        List<Claim> claims = gd.getAllPlayerClaims(targetUuid);

        javaPlugin.sendMessage(sender, ChatColor.GREEN +name+"'s Claims:");
        for (Claim claim : claims) {
            UUID claimUuid = claim.getUniqueId();
            TextComponent msg = new TextComponent("- " + claim.getOwnerName() + "'s "+claim.getType().toString().replace("griefdefender:", "")+" claim: " + claim.getDisplayName());
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dataadmin claim-tp " + claimUuid));
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(javaPlugin.getTpText(claimUuid)).create()));
            sender.spigot().sendMessage(msg);
        }

        return true;
    }

    private boolean handleClaimTpCmd(Player sender, String arg) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        if (arg==null || arg.isBlank()) {
            javaPlugin.sendMessage(sender, "Usage: /dataadmin claim-tp <claimUuid>");
            return true;
        }

        UUID claimUuid = UUID.fromString(arg);
        Claim claim = GriefDefender.getCore().getClaim(claimUuid);
        Vector3i claimVec1 = GriefDefender.getCore().getClaim(claimUuid).getGreaterBoundaryCorner();
        Vector3i claimVec2 = GriefDefender.getCore().getClaim(claimUuid).getLesserBoundaryCorner();

        Location location = new Location(javaPlugin.getServer().getWorld(claim.getWorldUniqueId()), (claimVec1.getX()+claimVec2.getX())/2, (claimVec1.getY()+claimVec2.getY())/2, (claimVec1.getZ()+claimVec2.getZ())/2);

        sender.teleport(location);
        return true;
    }

    private boolean handleClaimUuidCommand(Player sender) {
        if (!hasPermission(sender, "data64.cmd.claims")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "No permission!");
            return true;
        }

        Location playerLoc = ((Player) sender).getLocation();
        Claim claim = GriefDefender.getCore().getClaimAt(playerLoc.getWorld().getUID(), playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());

        if (claim!=null && !claim.isWilderness()) {
            javaPlugin.sendMessage(sender, "Claim UUID: "+claim.getUniqueId().toString());
        } else {
            javaPlugin.sendMessage(sender, "You're not standing in a claim!");
        }
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
                list.add("force-reset");
            }
            if (hasPermission(sender, "data64.cmd.transfer")) {
                list.add("transfer");
            }
            if (hasPermission(sender, "data64.cmd.claims")) {
                list.add("find-trusted");
                list.add("find-trusts");
                list.add("find-claims");
                list.add("monitor-add");
                list.add("monitor-remove");
                list.add("monitor-check");
                list.add("monitor-list");
                list.add("claim-tp");
                list.add("claim-id");
            }
        } else if (args.length == 2) {
            if (hasPermission(sender, "data64.cmd.reset") || hasPermission(sender, "data64.cmd.transfer") || hasPermission(sender, "data64.cmd.claims")) {
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