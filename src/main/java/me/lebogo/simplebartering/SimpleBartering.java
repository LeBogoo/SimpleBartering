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
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleBartering extends JavaPlugin {


    public static TradeManager TRADE_MANAGER;
    public static NamespacedKey OWNER_KEY;
    public static NamespacedKey SHOP_ID_KEY;
    public static NamespacedKey CURRENT_SHOP_ID_KEY;
    public static NamespacedKey CURRENT_TRADER_ENTITY_KEY;

    static {
        ConfigurationSerialization.registerClass(Trade.class, "Trade");

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
        CURRENT_SHOP_ID_KEY = new NamespacedKey(this, "current_shop_id");
        CURRENT_TRADER_ENTITY_KEY = new NamespacedKey(this, "current_trader_entity");

        getServer().getPluginManager().registerEvents(new EntityClickListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
