package me.lebogo.simplebartering.listener;

import io.papermc.paper.event.player.PlayerTradeEvent;
import me.lebogo.simplebartering.SimpleBartering;
import me.lebogo.simplebartering.Trade;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
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

        // TODO - The GUI doesn't update when the recipes are updated. This is a bug or a limitation of the Spigot/Paper API.
        //  I don't know if there is a solution for this. May look into this again in the future.
        List<MerchantRecipe> recipes = SimpleBartering.TRADE_MANAGER.getMerchantRecipes(shopId);
        event.getVillager().setRecipes(recipes);
    }
}
