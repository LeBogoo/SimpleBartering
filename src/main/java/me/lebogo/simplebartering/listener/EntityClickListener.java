package me.lebogo.simplebartering.listener;

import me.lebogo.simplebartering.SimpleBartering;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class EntityClickListener implements Listener {

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        // get name of entity
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        player.sendMessage(Component.text(entity.getName()));

        if (!(entity instanceof WanderingTrader)) return;
        WanderingTrader trader = (WanderingTrader) entity;
        PersistentDataContainer persistentDataContainer = trader.getPersistentDataContainer();
        String shopId = persistentDataContainer.get(SimpleBartering.SHOP_ID_KEY, PersistentDataType.STRING);
        if (shopId == null) return;

        String ownerName = persistentDataContainer.get(SimpleBartering.OWNER_KEY, PersistentDataType.STRING);
        assert ownerName != null;

        player.sendMessage(Component.text(player.isSneaking()));
        player.sendMessage(Component.text(shopId));
        player.sendMessage(Component.text(ownerName));

        boolean isOwner = player.getName().equals(ownerName);
        if (player.isSneaking()) {
            event.setCancelled(true);

            if (isOwner) openShopConfig(player, trader);
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

        List<MerchantRecipe> recipes = new ArrayList<>();
        // TODO - repalce with actual recipes loaded from store config
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(Material.IRON_AXE), 0, 1000, false);
        merchantRecipe.addIngredient(new ItemStack(Material.IRON_INGOT, 3));
        merchantRecipe.addIngredient(new ItemStack(Material.DIAMOND, 1));
        recipes.add(merchantRecipe);

        trader.setRecipes(recipes);
    }


    private void openShopConfig(Player player, WanderingTrader trader) {
        Inventory inventory = player.getServer().createInventory(player, InventoryType.HOPPER, Component.textOfChildren(SimpleBartering.PREFIX_COMPONENT, Component.space(), Component.text("Shop Menu")));

        inventory.setItem(0, SimpleBartering.EDIT_ITEM_STACK);
        inventory.setItem(1, SimpleBartering.FILLER_ITEM_STACK);
        inventory.setItem(2, SimpleBartering.SHOP_INVENTORY_ITEM_STACK);
        inventory.setItem(3, SimpleBartering.FILLER_ITEM_STACK);
        inventory.setItem(4, SimpleBartering.DESTROY_ITEM_STACK);

        player.openInventory(inventory);
    }
}
