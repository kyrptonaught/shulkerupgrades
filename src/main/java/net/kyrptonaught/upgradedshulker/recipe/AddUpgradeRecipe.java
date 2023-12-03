package net.kyrptonaught.upgradedshulker.recipe;

import com.google.gson.JsonObject;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class AddUpgradeRecipe extends SmithingTransformRecipe {
    public AddUpgradeRecipe(Identifier id, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, template, base, addition, result);
    }

    @Override
    public ItemStack craft(Inventory inv, DynamicRegistryManager registryManager) {
        ItemStack output = inv.getStack(1).copy();
        output.setCount(1);
        for (ShulkerUpgrades.UPGRADES upgrade : ShulkerUpgrades.UPGRADES.values())
            if (testTemplate(inv.getStack(0)) && upgrade.craftingItem.equals(inv.getStack(2).getItem())) {
                return upgrade.putOnStack(output);
            }
        return output;
    }

    public boolean matches(Inventory inv, World world) {
        if (ShulkerUpgrades.UPGRADES.hasUpgrades(inv.getStack(1)))
            return false;
        return super.matches(inv, world);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }

    public static class Serializer implements RecipeSerializer<AddUpgradeRecipe> {
        public AddUpgradeRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            Ingredient template = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "template"));
            ItemStack result = ShapedRecipe.getItem(JsonHelper.getObject(jsonObject, "result")).getDefaultStack();

            return new AddUpgradeRecipe(identifier, template, base, addition, result);
        }

        public AddUpgradeRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new AddUpgradeRecipe(identifier, ingredient, ingredient2, ingredient3, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, AddUpgradeRecipe smithingRecipe) {
            SmithingTransformRecipe.Serializer.SMITHING_TRANSFORM.write(packetByteBuf, smithingRecipe);
        }
    }
}