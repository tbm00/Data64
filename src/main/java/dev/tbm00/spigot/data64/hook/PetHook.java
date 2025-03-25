package dev.tbm00.spigot.data64.hook;

import de.Keyle.MyPet.MyPetPlugin;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;

import de.Keyle.MyPet.MyPetApi;

public class PetHook extends MyPetApi {
	
	public PetHook(MyPetPlugin pl) {
		setPlugin(pl);
	}

	public static void storeCurrentPet(MyPetPlayer player) {
        if (player!=null && player.hasMyPet()) {
            MyPet myPet = player.getMyPet();
            String worldGroup = myPet.getWorldGroup();

            if (MyPetApi.getMyPetManager().deactivateMyPet(player, true)) {
                player.setMyPetForWorldGroup(worldGroup, null);
            }
        }
	}
}