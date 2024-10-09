package me.lebogo.simplebartering;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lebogo.simplebartering.commands.CreateShopCommand;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleBartering extends JavaPlugin {
    public static TradeManager TRADE_MANAGER;
    public static NamespacedKey OWNER_KEY;
    public static NamespacedKey SHOP_ID_KEY;
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
