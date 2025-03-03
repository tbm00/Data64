package dev.tbm00.spigot.reset64.process;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;

import xzot1k.plugins.ds.DisplayShopsAPI;
import xzot1k.plugins.ds.api.objects.Shop;

import dev.tbm00.spigot.reset64.Reset64;

public class DSProcess {
    private Reset64 javaPlugin;
    private DisplayShopsAPI dsHook;

    public DSProcess(Reset64 javaPlugin) {
        this.javaPlugin = javaPlugin;
        divideShopBalances();
    }

    /**
     * Divides all display shop balances by 10.
     */
    private void divideShopBalances() {
        ConcurrentHashMap<String, Shop> dsMap = dsHook.getManager().getShopMap();
        int i = 0;
        for (Shop shop : dsMap.values()) {
            // Process each shop object
            double bal = shop.getStoredBalance();
            if (bal > 0) {
                bal = bal/10;
                shop.setStoredBalance((double) Math.round(bal));
            }
            i++;
        }
        javaPlugin.log(ChatColor.GOLD, "Reset "+i+" DisplayShop balances by factor of 10! ");
    }
}