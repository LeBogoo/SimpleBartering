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

    public YamlConfiguration createShopConfig(String shopId) {
        File shopFile = new File(tradesFolder, shopId + ".yml");
        if (!shopFile.exists()) {
            try {
                shopFile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(shopFile);
        setTrades(shopId, new ArrayList<>());
        setStock(shopId, new ArrayList<>());

        return yamlConfiguration;
    }

    public void deleteShopConfig(String shopId) {
        File shopFile = new File(tradesFolder, shopId + ".yml");
        if (!shopFile.exists()) {
            return;
        }
        shopFile.delete();
    }

    private YamlConfiguration getShopConfig(String shopId) {
        File shopFile = new File(tradesFolder, shopId + ".yml");
        return YamlConfiguration.loadConfiguration(shopFile);
    }

    public void setTrades(String shopId, List<Trade> trades) {
        YamlConfiguration shopConfig = getShopConfig(shopId);
        shopConfig.set("trades", trades);
        try {
            shopConfig.save(new File(tradesFolder, shopId + ".yml"));
        } catch (IOException ignored) {
        }
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

    public boolean isItemInStock(String shopId, ItemStack itemStack) {
        List<ItemStack> stock = getStock(shopId);
        for (ItemStack stockItem : stock) {
            if (stockItem.isSimilar(itemStack) && stockItem.getAmount() >= itemStack.getAmount()) {
                return true;
            }
        }
        return false;
    }

    public List<MerchantRecipe> getMerchantRecipes(String shopId) {
        List<Trade> tradeList = getTrades(shopId);
        List<MerchantRecipe> trades = new ArrayList<>();

        for (Trade trade : tradeList) {
            if (!trade.valid()) continue;
            MerchantRecipe merchantTrade = new MerchantRecipe(trade.output(), 0, trade.getAmount(getStock(shopId)), false);
            if (trade.input1() != null) merchantTrade.addIngredient(trade.input1());
            if (trade.input2() != null) merchantTrade.addIngredient(trade.input2());
            trades.add(merchantTrade);
        }

        return trades;
    }
}
