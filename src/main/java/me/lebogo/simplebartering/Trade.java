package me.lebogo.simplebartering;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("Trade")
public record Trade(ItemStack input1, ItemStack input2, ItemStack output) implements ConfigurationSerializable {

    public static Trade deserialize(Map<String, Object> map) {
        ItemStack input1 = (ItemStack) map.get("input1");
        ItemStack input2 = (ItemStack) map.get("input2");
        ItemStack output = (ItemStack) map.get("output");
        return new Trade(input1, input2, output);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("input1", input1);
        map.put("input2", input2);
        map.put("output", output);
        return map;
    }


}
