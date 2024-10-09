package me.lebogo.simplebartering;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Constants {
    // Components
    public static final Component PREFIX_COMPONENT = Component.text("[Bartering]");
    public static final Component SHOP_MENU_TITLE = Component.text("Shop Menu");
    public static final Component SHOP_INVENTORY_TITLE = Component.text("Shop Inventory");
    public static final Component SHOP_EDIT_TRADES_TITLE = Component.text("Edit Trades");
    public static final Component DESTROY_SHOP_MENU_TITLE = Component.text("Destroy Shop?");

    // ItemStacks
    public static ItemStack EDIT_ITEM_STACK = new ItemStack(Material.WRITABLE_BOOK);
    public static ItemStack SHOP_INVENTORY_ITEM_STACK = new ItemStack(Material.CHEST);
    public static ItemStack DESTROY_ITEM_STACK = new ItemStack(Material.BARRIER);
    public static ItemStack FILLER_ITEM_STACK = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    public static ItemStack INPUT_1_ITEM_STACK = new ItemStack(Material.EMERALD_BLOCK);
    public static ItemStack INPUT_2_ITEM_STACK = new ItemStack(Material.EMERALD_BLOCK);
    public static ItemStack OUTPUT_ITEM_STACK = new ItemStack(Material.DIAMOND_BLOCK);


    // Custom ItemStacks
    static {
        Style style = Style.style().decoration(TextDecoration.ITALIC, false).build();

        ItemMeta editItemMeta = EDIT_ITEM_STACK.getItemMeta();
        // remove all default styles
        editItemMeta.displayName(Component.text("Edit Trades").style(style));
        EDIT_ITEM_STACK.setItemMeta(editItemMeta);

        ItemMeta shopInventoryItemMeta = SHOP_INVENTORY_ITEM_STACK.getItemMeta();
        shopInventoryItemMeta.displayName(Component.text("Open Inventory").style(style));
        SHOP_INVENTORY_ITEM_STACK.setItemMeta(shopInventoryItemMeta);

        ItemMeta destroyItemMeta = DESTROY_ITEM_STACK.getItemMeta();
        destroyItemMeta.displayName(Component.text("Destroy Shop").style(style.color(TextColor.color(0xFB5454))));
        DESTROY_ITEM_STACK.setItemMeta(destroyItemMeta);

        ItemMeta fillerItemMeta = FILLER_ITEM_STACK.getItemMeta();
        fillerItemMeta.displayName(Component.empty());
        FILLER_ITEM_STACK.setItemMeta(fillerItemMeta);

        ItemMeta input1ItemMeta = INPUT_1_ITEM_STACK.getItemMeta();
        input1ItemMeta.displayName(Component.text("Input 1").style(style.color(TextColor.color(0x4FFB54))));
        input1ItemMeta.lore(List.of(Component.text("Put your first input item in this row.")));
        INPUT_1_ITEM_STACK.setItemMeta(input1ItemMeta);

        ItemMeta input2ItemMeta = INPUT_2_ITEM_STACK.getItemMeta();
        input2ItemMeta.displayName(Component.text("Input 2").style(style.color(TextColor.color(0x4FFB54))));
        input2ItemMeta.lore(List.of(Component.text("Put your second input item in this row.")));
        INPUT_2_ITEM_STACK.setItemMeta(input2ItemMeta);

        ItemMeta outputItemMeta = OUTPUT_ITEM_STACK.getItemMeta();
        outputItemMeta.displayName(Component.text("Output").style(style.color(TextColor.color(0xFB5454))));
        outputItemMeta.lore(List.of(Component.text("Put your output item in this row.")));
        OUTPUT_ITEM_STACK.setItemMeta(outputItemMeta);

    }

}
