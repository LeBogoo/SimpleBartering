package me.lebogo.simplebartering;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Trade")
public final class Trade implements ConfigurationSerializable {
    private final ItemStack input1;
    private final ItemStack input2;
    private final ItemStack output;
    private final boolean valid;

    public Trade(ItemStack input1, ItemStack input2, ItemStack output,
                 boolean valid) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.valid = valid;
    }

    public static Trade deserialize(Map<String, Object> map) {
        ItemStack input1 = null;
        ItemStack input2 = null;
        ItemStack output = null;
        boolean valid = false;

        if (map.containsKey("input1")) input1 = (ItemStack) map.get("input1");
        if (map.containsKey("input2")) input2 = (ItemStack) map.get("input2");
        if (map.containsKey("output")) output = (ItemStack) map.get("output");
        if (map.containsKey("valid")) valid = (boolean) map.get("valid");


        return new Trade(input1, input2, output, valid);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("input1", input1);
        map.put("input2", input2);
        map.put("output", output);
        map.put("valid", valid);
        return map;
    }

    public ItemStack input1() {
        return input1;
    }

    public ItemStack input2() {
        return input2;
    }

    public ItemStack output() {
        return output;
    }

    public boolean valid() {
        return valid;
    }

    @Override
    public String toString() {
        return "Trade[" +
                "input1=" + input1 + ", " +
                "input2=" + input2 + ", " +
                "output=" + output + ", " +
                "valid=" + valid + ']';
    }

    public int getAmount(List<ItemStack> stock) {
        int stockAmount = 0;
        int outputAmount = output.getAmount();
        for (ItemStack itemStack : stock) {
            if (itemStack.isSimilar(output)) {
                stockAmount += itemStack.getAmount();
            }
        }

        return stockAmount / outputAmount;
    }


}
