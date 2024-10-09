package me.lebogo.simplebartering.listener;

import me.lebogo.simplebartering.SimpleBartering;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;


public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // get name of inventory
        Component title = event.getView().title();
        List<Component> children = title.children();
        if (SimpleBartering.PREFIX_COMPONENT.equals(children.getFirst())) {
            event.setCancelled(true);
        }
    }
}
