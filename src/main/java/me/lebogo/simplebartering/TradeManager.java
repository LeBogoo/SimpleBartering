package me.lebogo.simplebartering;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TradeManager {

    private final File tradesFolder;

    public TradeManager(File tradesFolder) {
        this.tradesFolder = tradesFolder;
    }

    private YamlConfiguration getShopConfig(String shopId) {
        File shopFile = new File(tradesFolder, shopId + ".yml");
        if (!shopFile.exists()) {
            try {
                shopFile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        return YamlConfiguration.loadConfiguration(shopFile);
    }

    public List<Trade> getTrades(String shopId) {
        YamlConfiguration shopConfig = getShopConfig(shopId);

        List<Trade> trades = new ArrayList<>();

        if (!shopConfig.contains("trades")) {
            return trades;
        }

        return (List<Trade>) shopConfig.getList("trades");
    }

    public void setStock(String shopId, List<ItemStack> stock) {
        YamlConfiguration shopConfig = getShopConfig(shopId);
        shopConfig.set("stock", stock);
        try {
            shopConfig.save(new File(tradesFolder, shopId + ".yml"));
        } catch (IOException ignored) {
        }
    }

    public List<ItemStack> getStock(String shopId) {
        YamlConfiguration shopConfig = getShopConfig(shopId);

        List<ItemStack> stock = new ArrayList<>();

        if (!shopConfig.contains("stock")) {
            return stock;
        }

        return (List<ItemStack>) shopConfig.getList("stock");
    }

    public List<MerchantRecipe> getMerchantRecipes(String shopId) {
        List<Trade> tradeList = getTrades(shopId);
        List<MerchantRecipe> trades = new ArrayList<>();

        // TODO - get stock from config

        for (Trade trade : tradeList) {
            MerchantRecipe merchantTrade = new MerchantRecipe(trade.output(), 0, 0, false);
            merchantTrade.addIngredient(trade.input1());
            merchantTrade.addIngredient(trade.input2());
            trades.add(merchantTrade);
        }

        return trades;
    }
}
