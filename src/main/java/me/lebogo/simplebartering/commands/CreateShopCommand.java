package me.lebogo.simplebartering.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.lebogo.simplebartering.SimpleBartering;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CreateShopCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;

        WanderingTrader trader = (WanderingTrader) player.getWorld().spawnEntity(player.getLocation(), EntityType.WANDERING_TRADER);
        Objects.requireNonNull(trader.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0);
        Objects.requireNonNull(trader.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1000);
        trader.setInvulnerable(true);
        trader.setSilent(true);
        trader.setCanPickupItems(false);
        trader.setCanDrinkPotion(false);
        trader.setCanDrinkMilk(false);

        PersistentDataContainer persistentDataContainer = trader.getPersistentDataContainer();
        persistentDataContainer.set(SimpleBartering.OWNER_KEY, PersistentDataType.STRING, player.getName());
        persistentDataContainer.set(SimpleBartering.SHOP_ID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());

        List<MerchantRecipe> recipes = new ArrayList<>();
        trader.setRecipes(recipes);
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }
}
