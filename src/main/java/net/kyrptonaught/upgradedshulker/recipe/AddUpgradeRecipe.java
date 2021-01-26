package net.kyrptonaught.upgradedshulker.recipe;

import com.google.gson.JsonObject;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class AddUpgradeRecipe extends SmithingRecipe {
    public AddUpgradeRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, base, addition, result);
    }

    public ItemStack craft(Inventory inv) {
        ItemStack output = inv.getStack(0).copy();
        for (ShulkerUpgrades.UPGRADES upgrade : ShulkerUpgrades.UPGRADES.values())
            if (upgrade.craftingItem.equals(inv.getStack(1).getItem()))
                return upgrade.putOnStack(output);
        return output;
    }

    public boolean matches(Inventory inv, World world) {
        if (ShulkerUpgrades.UPGRADES.hasUpgrades(inv.getStack(0)))
            return false;
        return super.matches(inv, world);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING;
    }

    public static class Serializer implements RecipeSerializer<AddUpgradeRecipe> {
        public AddUpgradeRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
            return new AddUpgradeRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public AddUpgradeRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new AddUpgradeRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, AddUpgradeRecipe smithingRecipe) {
            SmithingRecipe.Serializer.SMITHING.write(packetByteBuf, smithingRecipe);
        }
    }
}