package me.lebogo.simplebartering.listener;

import me.lebogo.simplebartering.Constants;
import me.lebogo.simplebartering.SimpleBartering;
import me.lebogo.simplebartering.Trade;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class InventoryListener implements Listener {

    public static ItemStack YES_ITEM_STACK = new ItemStack(Material.EMERALD);
    public static ItemStack NO_ITEM_STACK = new ItemStack(Material.BARRIER);


    static {
        Style style = Style.style().decoration(TextDecoration.ITALIC, false).build();

        ItemMeta yesItemMeta = YES_ITEM_STACK.getItemMeta();
        yesItemMeta.displayName(Component.text("Yes").style(style.color(TextColor.color(0x4FFB54))));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("This will destroy this shop forever.").style(style.color(TextColor.color(0xFB5454))));
        lore.add(Component.text("All stored items will be dropped to").style(style.color(TextColor.color(0xFB5454))));
        lore.add(Component.text("the ground, so make sure to pick them up quickly!").style(style.color(TextColor.color(0xFB5454))));


        yesItemMeta.lore(lore);
        YES_ITEM_STACK.setItemMeta(yesItemMeta);

        ItemMeta noItemMeta = NO_ITEM_STACK.getItemMeta();
        noItemMeta.displayName(Component.text("No").style(style.color(TextColor.color(0xFB5454))));
        NO_ITEM_STACK.setItemMeta(noItemMeta);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        // get name of inventory
        List<Component> children = view.title().children();
        if (children.isEmpty()) return;

        Component first = children.getFirst();
        if (!first.equals(Constants.PREFIX_COMPONENT)) return;

        // We now know that the inventory is a bartering inventory. We don't know which one yet.

        Player player = (Player) event.getWhoClicked();
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        String shopId = persistentDataContainer.get(SimpleBartering.CURRENT_SHOP_ID_KEY, PersistentDataType.STRING);
        String traderEntityId = persistentDataContainer.get(SimpleBartering.CURRENT_TRADER_ENTITY_KEY, PersistentDataType.STRING);

        Component last = children.getLast();

        if (last.equals(Constants.SHOP_MENU_TITLE)) {
            handleMainMenuClick(event, player, shopId);
        } else if (last.equals(Constants.SHOP_EDIT_TRADES_TITLE)) {
            handleEditTradesClick(event);
        } else if (last.equals(Constants.DESTROY_SHOP_MENU_TITLE)) {
            handleDestroyMenuClick(event, player, shopId, traderEntityId);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        // get name of inventory
        List<Component> children = view.title().children();
        if (children.isEmpty()) return;

        Component first = children.getFirst();
        if (!first.equals(Constants.PREFIX_COMPONENT)) return;

        // We now know that the inventory is a bartering inventory. We don't know which one yet.

        Player player = (Player) event.getPlayer();
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        String shopId = persistentDataContainer.get(SimpleBartering.CURRENT_SHOP_ID_KEY, PersistentDataType.STRING);

        Component last = children.getLast();

        if (last.equals(Constants.SHOP_INVENTORY_TITLE)) {
            handleShopInventoryClose(event, shopId);
        } else if (last.equals(Constants.SHOP_EDIT_TRADES_TITLE)) {
            handleEditTradesClose(event, shopId);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, Player player, String shopId) {
        event.setCancelled(true);

        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null) return;

        if (currentItem.equals(Constants.EDIT_ITEM_STACK)) {
            openEditInventory(player, shopId);
        } else if (currentItem.equals(Constants.SHOP_INVENTORY_ITEM_STACK)) {
            openShopInventory(player);
        } else if (currentItem.equals(Constants.DESTROY_ITEM_STACK)) {
            openDestroyInventory((Player) event.getWhoClicked());
        }
    }

    private void handleShopInventoryClose(InventoryCloseEvent event, String shopId) {
        Inventory topInventory = event.getView().getTopInventory();

        List<ItemStack> stock = new ArrayList<>();
        for (ItemStack inventoryItemStack : topInventory.getContents()) {
            // sum up all similar items and add to stock
            if (inventoryItemStack == null) continue;
            boolean found = false;
            for (ItemStack stockItemStack : stock) {
                if (stockItemStack.isSimilar(inventoryItemStack)) {
                    stockItemStack.setAmount(stockItemStack.getAmount() + inventoryItemStack.getAmount());
                    found = true;
                    break;
                }
            }

            if (!found) {
                stock.add(inventoryItemStack);
            }
        }

        SimpleBartering.TRADE_MANAGER.setStock(shopId, stock);
        System.out.println("Stock updated");
    }

    private void handleEditTradesClose(InventoryCloseEvent event, String shopId) {
        List<Trade> trades = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ItemStack firstInput = event.getView().getTopInventory().getItem(i);
            ItemStack secondInput = event.getView().getTopInventory().getItem(i + 9);
            ItemStack output = event.getView().getTopInventory().getItem(i + 18);
            boolean valid = true;
            if (firstInput == null && secondInput == null && output == null) continue;
            if (firstInput == null && secondInput == null || output == null) {
                event.getPlayer().sendMessage(
                        Component.text("Invalid trade at position " + (i + 1) + ". Please fill in at least one input slot and the output slot.")
                                .style(Style.style(TextColor.color(0xFB5454)))
                );
                valid = false;
            }

            Trade trade = new Trade(firstInput, secondInput, output, valid);
            trades.add(trade);
        }

        SimpleBartering.TRADE_MANAGER.setTrades(shopId, trades);
    }

    private void handleEditTradesClick(InventoryClickEvent event) {
        int column = event.getSlot() % 9;
        if (column == 0) {
            event.setCancelled(true);
        }
    }

    private void handleDestroyMenuClick(InventoryClickEvent event, Player player, String shopId, String traderEntityId) {
        event.setCancelled(true);

        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null) return;

        if (currentItem.equals(YES_ITEM_STACK)) {
            if (traderEntityId == null) {
                player.sendMessage(Component.text("Could not find trader entity."));
                return;
            }

            // Get trader entity
            Entity entity = player.getServer().getEntity(UUID.fromString(traderEntityId));
            if (entity == null) {
                player.sendMessage(Component.text("Could not find trader entity."));
                return;
            }

            for (ItemStack itemStack : SimpleBartering.TRADE_MANAGER.getStock(shopId)) {
                entity.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }

            for (Trade trade : SimpleBartering.TRADE_MANAGER.getTrades(shopId)) {
                if (trade.input1() != null) entity.getWorld().dropItemNaturally(player.getLocation(), trade.input1());
                if (trade.input2() != null) entity.getWorld().dropItemNaturally(player.getLocation(), trade.input2());
                if (trade.output() != null) entity.getWorld().dropItemNaturally(player.getLocation(), trade.output());
            }

            if (entity instanceof Damageable) {
                ((Damageable) entity).setHealth(0);
            }

            SimpleBartering.TRADE_MANAGER.deleteShopConfig(shopId);

            player.closeInventory();
            player.sendMessage(Component.text("Shop destroyed successfully.").style(Style.style(TextColor.color(0x4FFB54))));

        } else if (currentItem.equals(NO_ITEM_STACK)) {
            // Close inventory
            player.closeInventory();
        }
    }

    private void openEditInventory(Player player, String shopId) {
        Inventory inventory = player.getServer().createInventory(player, InventoryType.CHEST, Component.textOfChildren(
                Constants.PREFIX_COMPONENT,
                Component.space(),
                Constants.SHOP_EDIT_TRADES_TITLE
        ));


        inventory.setItem(0, Constants.INPUT_1_ITEM_STACK);
        inventory.setItem(9, Constants.INPUT_2_ITEM_STACK);
        inventory.setItem(18, Constants.OUTPUT_ITEM_STACK);

        List<Trade> trades = SimpleBartering.TRADE_MANAGER.getTrades(shopId);
        for (int i = 0; i < trades.size(); i++) {
            Trade trade = trades.get(i);
            inventory.setItem(i + 1, trade.input1());
            inventory.setItem(i + 1 + 9, trade.input2());
            inventory.setItem(i + 1 + 18, trade.output());
        }


        player.openInventory(inventory);

    }

    private void openShopInventory(Player player) {
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        String shopId = persistentDataContainer.get(SimpleBartering.CURRENT_SHOP_ID_KEY, PersistentDataType.STRING);

        List<ItemStack> stock = SimpleBartering.TRADE_MANAGER.getStock(shopId);

        Inventory inventory = player.getServer().createInventory(player, 9 * 6, Component.textOfChildren(
                Constants.PREFIX_COMPONENT,
                Component.space(),
                Constants.SHOP_INVENTORY_TITLE
        ));

        for (ItemStack itemStack : stock) {
            // TODO - Items are added to the shop inventory by trading.
            //  What if more items are coming in than go out? This would result in more stack inside the inventory than the double chest can hold.
            //  Maybe add some sort of pagination to the inventory?

            // get individual item stacks from stacked itemstack
            while (itemStack.getAmount() > itemStack.getMaxStackSize()) {
                ItemStack clone = itemStack.clone();
                clone.setAmount(itemStack.getMaxStackSize());
                inventory.addItem(clone);
                itemStack.setAmount(itemStack.getAmount() - itemStack.getMaxStackSize());
            }
            inventory.addItem(itemStack);
        }


        player.openInventory(inventory);
    }

    private void openDestroyInventory(Player player) {
        // open destroy inventory
        Inventory inventory = player.getServer().createInventory(player, InventoryType.HOPPER, Component.textOfChildren(
                Constants.PREFIX_COMPONENT,
                Component.space(),
                Constants.DESTROY_SHOP_MENU_TITLE
        ));

        inventory.setItem(0, Constants.FILLER_ITEM_STACK);
        inventory.setItem(1, YES_ITEM_STACK);
        inventory.setItem(2, Constants.FILLER_ITEM_STACK);
        inventory.setItem(3, NO_ITEM_STACK);
        inventory.setItem(4, Constants.FILLER_ITEM_STACK);

        player.openInventory(inventory);
    }
}
