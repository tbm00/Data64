package dev.tbm00.spigot.data64.process;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;

import xzot1k.plugins.ds.DisplayShopsAPI;
import xzot1k.plugins.ds.api.objects.Shop;

import dev.tbm00.spigot.data64.Data64;

public class DSProcess {
    private Data64 javaPlugin;
    private DisplayShopsAPI dsHook;

    public DSProcess(Data64 javaPlugin) {
        this.javaPlugin = javaPlugin;
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
                buyPrice = buyPrice/8;
                shop.setBuyPrice((double) Math.round(buyPrice));
            }
            
            double sellPrice = shop.getSellPrice(false);
            if (sellPrice > 0) {
                sellPrice = sellPrice/8;
                shop.setSellPrice((double) Math.round(sellPrice));
            }
            i++;
        }
        javaPlugin.log(ChatColor.GOLD, "Reset "+i+" DisplayShop balances & prices! ");
    }
}