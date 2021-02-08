package net.kyrptonaught.upgradedshulker.recipe;

import com.google.gson.JsonObject;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class KeepColorSmithing extends SmithingRecipe {
    public KeepColorSmithing(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, base, addition, result);
    }

    public ItemStack craft(Inventory inv) {
        ItemStack output = this.getOutput().copy();
        ItemStack shulker = inv.getStack(0);
        ShulkerUpgrades.MATERIAL type = ((UpgradedShulkerBlock) Block.getBlockFromItem(output.getItem())).material;
        DyeColor color = ((UpgradedShulkerBlock) Block.getBlockFromItem(shulker.getItem())).getColor();
        output = ShulkersRegistry.getShulkerBlock(type, color).asItem().getDefaultStack();
        if (shulker.hasTag())
            output.setTag(shulker.getTag());
        return output;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING;
    }

    public static class Serializer implements RecipeSerializer<KeepColorSmithing> {
        public KeepColorSmithing read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
            return new KeepColorSmithing(identifier, ingredient, ingredient2, itemStack);
        }

        public KeepColorSmithing read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new KeepColorSmithing(identifier, ingredient, ingredient2, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, KeepColorSmithing smithingRecipe) {
            SmithingRecipe.Serializer.SMITHING.write(packetByteBuf, smithingRecipe);
        }
    }
}
