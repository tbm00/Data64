package dev.tbm00.spigot.data64.hook;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

import dev.tbm00.spigot.data64.Data64;

public class GDHook {

	public GDHook(Data64 data64) {}

	public static int getClaimAmount(OfflinePlayer player) {
		final Core gd = GriefDefender.getCore();
        return gd.getUser(player.getUniqueId()).getPlayerData().getClaims().size();
	}

	public static int getAccruedBlocks(OfflinePlayer player) {
		final Core gd = GriefDefender.getCore();
        return gd.getUser(player.getUniqueId()).getPlayerData().getAccruedClaimBlocks();
	}

	public static void transferClaims(OfflinePlayer playerA, OfflinePlayer playerB) {
		final Core gd = GriefDefender.getCore();
        UUID uuidB = playerB.getUniqueId();

        Claim[] claimsA = gd.getUser(playerA.getUniqueId()).getPlayerData().getClaims().toArray(new Claim[0]);
		for (Claim claim : claimsA) {
			claim.transferOwner(uuidB);
		}
	}
}