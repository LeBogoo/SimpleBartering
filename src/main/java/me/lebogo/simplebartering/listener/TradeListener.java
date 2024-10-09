package me.lebogo.simplebartering.listener;

import io.papermc.paper.event.player.PlayerTradeEvent;
import me.lebogo.simplebartering.SimpleBartering;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class TradeListener implements Listener {

    @EventHandler
    public void onTrade(PlayerTradeEvent event) {
        MerchantRecipe trade = event.getTrade();
        PersistentDataContainer persistentDataContainer = event.getVillager().getPersistentDataContainer();
        String shopId = persistentDataContainer.get(SimpleBartering.SHOP_ID_KEY, PersistentDataType.STRING);
        if (shopId == null) {
            return;
        }

        List<ItemStack> stock = SimpleBartering.TRADE_MANAGER.getStock(shopId);
        ItemStack result = trade.getResult();
        // remove result from stock. Stock is a list of ItemStacks. Keep in mind that each itemstack has a different amount.
        for (ItemStack itemStack : stock) {
            if (itemStack.isSimilar(result)) {
                itemStack.setAmount(itemStack.getAmount() - result.getAmount());
                break;
            }
        }

        List<ItemStack> ingredients = trade.getIngredients();
        // add ingredients to stock
        for (ItemStack ingredient : ingredients) {
            boolean found = false;
            for (ItemStack stockItemStack : stock) {
                if (stockItemStack.isSimilar(ingredient)) {
                    stockItemStack.setAmount(stockItemStack.getAmount() + ingredient.getAmount());
                    found = true;
                    break;
                }
            }

            if (!found) {
                stock.add(ingredient);
            }
        }

        SimpleBartering.TRADE_MANAGER.setStock(shopId, stock);


        // TODO - Disable all trades that don't have enough stock left for another trade
        // List<MerchantRecipe> merchantRecipes = SimpleBartering.TRADE_MANAGER.getMerchantRecipes(shopId);
        //        event.getVillager().setRecipes(merchantRecipes);

    }
}
