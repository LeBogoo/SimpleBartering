package me.lebogo.simplebartering;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lebogo.simplebartering.commands.CreateShopCommand;
import me.lebogo.simplebartering.listener.EntityClickListener;
import me.lebogo.simplebartering.listener.InventoryListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleBartering extends JavaPlugin {
    public static final Component PREFIX_COMPONENT = Component.text("[Bartering]");
    public static ItemStack EDIT_ITEM_STACK = new ItemStack(Material.WRITABLE_BOOK);
    public static ItemStack SHOP_INVENTORY_ITEM_STACK = new ItemStack(Material.CHEST);
    public static ItemStack DESTROY_ITEM_STACK = new ItemStack(Material.BARRIER);
    public static ItemStack FILLER_ITEM_STACK = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);


    public static TradeManager TRADE_MANAGER;
    public static NamespacedKey OWNER_KEY;
    public static NamespacedKey SHOP_ID_KEY;

    static {
        Style style = Style.style().decoration(TextDecoration.ITALIC, false).build();

        ItemMeta editItemMeta = EDIT_ITEM_STACK.getItemMeta();
        // remove all default styles
        editItemMeta.displayName(Component.text("Edit Offers").style(style));
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
    }

    @Override
    public void onEnable() {
        saveConfig();

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("createshop", new CreateShopCommand());
        });


        TRADE_MANAGER = new TradeManager(getDataFolder());

        OWNER_KEY = new NamespacedKey(this, "owner");
        SHOP_ID_KEY = new NamespacedKey(this, "shop_id");

        getServer().getPluginManager().registerEvents(new EntityClickListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
