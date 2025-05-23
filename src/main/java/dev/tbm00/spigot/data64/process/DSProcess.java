package dev.tbm00.spigot.data64.process;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;

import xzot1k.plugins.ds.DisplayShopsAPI;
import xzot1k.plugins.ds.api.objects.Shop;

import dev.tbm00.spigot.data64.Data64;

public class DSProcess {
    private Data64 javaPlugin;
    private DisplayShopsAPI dsHook;

    public DSProcess(Data64 javaPlugin, DisplayShopsAPI dsHook) {
        this.javaPlugin = javaPlugin;
        this.dsHook = dsHook;
        alterShops();
    }

    /**
     * Divides all display shop balances by 10.
     */
    private void alterShops() {
        ConcurrentHashMap<String, Shop> dsMap = dsHook.getManager().getShopMap();
        int i = 0;
        for (Shop shop : dsMap.values()) {
            double bal = shop.getStoredBalance();
            if (bal > 0) {
                bal = bal/10;
                shop.setStoredBalance((double) Math.round(bal));
            }

            double buyPrice = shop.getBuyPrice(false);
            if (buyPrice > 0) {
                buyPrice = (double) Math.round(buyPrice/8);
                if (buyPrice<1) buyPrice = 1;
                shop.setBuyPrice(buyPrice);
            }
            
            double sellPrice = shop.getSellPrice(false);
            if (sellPrice > 0) {
                sellPrice = (double) Math.round(sellPrice/8);
                if (sellPrice<1) sellPrice = 1;
                shop.setSellPrice(sellPrice);
            }
            i++;
        }
        javaPlugin.log(ChatColor.GOLD, "Reset "+i+" DisplayShop balances & prices! ");
    }
}