package me.lebogo.simplebartering.listener;

import me.lebogo.simplebartering.Constants;
import me.lebogo.simplebartering.SimpleBartering;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class EntityClickListener implements Listener {

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        // get name of entity
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (!(entity instanceof WanderingTrader trader)) return;
        PersistentDataContainer traderDataContainer = trader.getPersistentDataContainer();
        String shopId = traderDataContainer.get(SimpleBartering.SHOP_ID_KEY, PersistentDataType.STRING);
        if (shopId == null) return;

        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        playerDataContainer.set(SimpleBartering.CURRENT_SHOP_ID_KEY, PersistentDataType.STRING, shopId);
        playerDataContainer.set(SimpleBartering.CURRENT_TRADER_ENTITY_KEY, PersistentDataType.STRING, trader.getUniqueId().toString());


        String ownerName = traderDataContainer.get(SimpleBartering.OWNER_KEY, PersistentDataType.STRING);
        assert ownerName != null;

        boolean isOwner = player.getName().equals(ownerName);
        if (player.isSneaking()) {
            event.setCancelled(true);
            if (isOwner) openShopConfig(player, trader, shopId);
            else
                player.sendMessage(
                        Component.textOfChildren(
                                Component.text("This shop belongs to "),
                                Component.text(ownerName).style(Style.style(TextColor.color(0xFCAC04))),
                                Component.text("."),
                                Component.newline(),
                                Component.text("You can only edit your own shops."),
                                Component.newline(),
                                Component.text("Try interacting with the shop without sneaking.")
                        ).style(Style.style(TextColor.color(0xFB5454)))
                );
            return;
        }

        List<MerchantRecipe> merchantRecipes = SimpleBartering.TRADE_MANAGER.getMerchantRecipes(shopId);
        trader.setRecipes(merchantRecipes);

        if (merchantRecipes.isEmpty()) {
            player.sendMessage(Component.text("This shop doesn't have any trades set up yet.").style(Style.style(TextColor.color(0xFCAC04))));
            if (isOwner) {
                player.sendMessage(Component.text("You can interact with it while sneaking to set up trades and restock the shop.").style(Style.style(TextColor.color(0xFCAC04))));
            }
        }

    }


    private void openShopConfig(Player player, WanderingTrader trader, String shopId) {
        Inventory inventory = player.getServer().createInventory(player, InventoryType.HOPPER, Component.textOfChildren(Constants.PREFIX_COMPONENT, Component.space(), Constants.SHOP_MENU_TITLE));

        inventory.setItem(0, Constants.EDIT_ITEM_STACK);
        inventory.setItem(1, Constants.FILLER_ITEM_STACK);
        inventory.setItem(2, Constants.SHOP_INVENTORY_ITEM_STACK);
        inventory.setItem(3, Constants.FILLER_ITEM_STACK);
        inventory.setItem(4, Constants.DESTROY_ITEM_STACK);

        player.openInventory(inventory);
    }
}
