package me.lebogo.simplebartering;

import me.lebogo.simplebartering.listener.EntityClickListener;
import me.lebogo.simplebartering.listener.EntitySpawnListener;
import me.lebogo.simplebartering.listener.InventoryListener;
import me.lebogo.simplebartering.listener.TradeListener;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleBartering extends JavaPlugin {


    public static TradeManager TRADE_MANAGER;
    public static NamespacedKey OWNER_KEY;
    public static NamespacedKey SHOP_ID_KEY;
    public static NamespacedKey CURRENT_SHOP_ID_KEY;
    public static NamespacedKey CURRENT_TRADER_ENTITY_KEY;
    public static NamespacedKey TRADER_SPAWN_EGG_KEY;
    public static NamespacedKey TRADER_SPAWNED_KEY;

    static {
        ConfigurationSerialization.registerClass(Trade.class, "Trade");

    }

    @Override
    public void onEnable() {
        saveConfig();


        TRADE_MANAGER = new TradeManager(getDataFolder());

        OWNER_KEY = new NamespacedKey(this, "owner");
        SHOP_ID_KEY = new NamespacedKey(this, "shop_id");
        CURRENT_SHOP_ID_KEY = new NamespacedKey(this, "current_shop_id");
        CURRENT_TRADER_ENTITY_KEY = new NamespacedKey(this, "current_trader_entity");
        TRADER_SPAWN_EGG_KEY = new NamespacedKey(this, "trader_spawn_egg");
        TRADER_SPAWNED_KEY = new NamespacedKey(this, "trader_spawned");

        getServer().addRecipe(
                new ShapelessRecipe(TRADER_SPAWN_EGG_KEY, Constants.TRADER_SPAWN_EGG_ITEM_STACK)
                        .addIngredient(Material.EGG)
                        .addIngredient(Material.DIAMOND_BLOCK)
                        .addIngredient(Material.EMERALD_BLOCK)
        );


        getServer().getPluginManager().registerEvents(new EntityClickListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new TradeListener(), this);
        getServer().getPluginManager().registerEvents(new EntitySpawnListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
