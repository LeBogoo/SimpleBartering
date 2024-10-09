package me.lebogo.simplebartering;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleBartering extends JavaPlugin {
    public static TradeManager TRADE_MANAGER;
    @Override
    public void onEnable() {
        saveConfig();

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
        TRADE_MANAGER = new TradeManager(getDataFolder());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
