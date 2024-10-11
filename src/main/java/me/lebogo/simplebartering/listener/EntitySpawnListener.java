package me.lebogo.simplebartering.listener;

import me.lebogo.simplebartering.Constants;
import me.lebogo.simplebartering.SimpleBartering;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        PersistentDataContainer itemDataContainer = event.getItem().getItemMeta().getPersistentDataContainer();
        Boolean isTraderSpawnEgg = itemDataContainer.get(SimpleBartering.TRADER_SPAWN_EGG_KEY, PersistentDataType.BOOLEAN);
        if (isTraderSpawnEgg == null || !isTraderSpawnEgg) return;

        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        playerDataContainer.set(SimpleBartering.TRADER_SPAWNED_KEY, PersistentDataType.BOOLEAN, true);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof WanderingTrader trader)) return;

        CreatureSpawnEvent.SpawnReason entitySpawnReason = entity.getEntitySpawnReason();

        if (!entitySpawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) return;

        Component component = entity.customName();
        if (!(component instanceof TextComponent textComponent)) return;

        boolean isTraderSpawnEgg = Constants.TRADER_SPAWN_EGG_NAME.content().equals(textComponent.content());
        if (!isTraderSpawnEgg) return;


        // get all players nearby
        List<Player> players = new ArrayList<>();
        for (Player player : trader.getWorld().getPlayers()) {
            if (player.getLocation().distance(trader.getLocation()) < 10) {
                players.add(player);
            }
        }

        // get the first player that got the PersistentDataContainer TRADER_SPAWNED_KEY set to true
        Player player = players.stream()
                .filter(p -> p.getPersistentDataContainer().get(SimpleBartering.TRADER_SPAWNED_KEY, PersistentDataType.BOOLEAN) != null)
                .findFirst()
                .orElse(null);

        if (player == null) return;

        player.getPersistentDataContainer().remove(SimpleBartering.TRADER_SPAWNED_KEY);

        Objects.requireNonNull(trader.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        Objects.requireNonNull(trader.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1000);
        trader.setInvulnerable(true);
        trader.setSilent(true);
        trader.setCanPickupItems(false);
        trader.setCanDrinkPotion(false);
        trader.setCanDrinkMilk(false);
        trader.setCollidable(false);

        String shopId = UUID.randomUUID().toString();

        PersistentDataContainer persistentDataContainer = trader.getPersistentDataContainer();
        persistentDataContainer.set(SimpleBartering.OWNER_KEY, PersistentDataType.STRING, player.getName());
        persistentDataContainer.set(SimpleBartering.SHOP_ID_KEY, PersistentDataType.STRING, shopId);

        SimpleBartering.TRADE_MANAGER.createShopConfig(shopId);

        List<MerchantRecipe> recipes = new ArrayList<>();
        trader.setRecipes(recipes);

        trader.customName(null);

        player.sendMessage(Component.text("Shop created!").style(Style.style(TextColor.color(0xFCAC04))));
    }
}
