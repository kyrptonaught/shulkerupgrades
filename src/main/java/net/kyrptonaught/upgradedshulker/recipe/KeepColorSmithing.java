package net.kyrptonaught.upgradedshulker.recipe;

import com.google.gson.JsonObject;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class KeepColorSmithing extends SmithingTransformRecipe {
    public KeepColorSmithing(Identifier id, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, template, base, addition, result);
    }

    @Override
    public ItemStack craft(Inventory inv, DynamicRegistryManager registryManager) {
        ItemStack output = this.getOutput(registryManager).copy();
        ItemStack shulker = inv.getStack(1);
        ShulkerUpgrades.MATERIAL type = ((UpgradedShulkerBlock) Block.getBlockFromItem(output.getItem())).material;
        DyeColor color = ((UpgradedShulkerBlock) Block.getBlockFromItem(shulker.getItem())).getColor();
        output = ShulkersRegistry.getShulkerBlock(type, color).asItem().getDefaultStack();
        if (shulker.hasNbt())
            output.setNbt(shulker.getNbt());
        return output;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }

    public static class Serializer implements RecipeSerializer<KeepColorSmithing> {
        public KeepColorSmithing read(Identifier identifier, JsonObject jsonObject) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient template = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "template"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack result = ShapedRecipe.getItem(JsonHelper.getObject(jsonObject, "result")).getDefaultStack();
            return new KeepColorSmithing(identifier, template, base, addition, result);
        }

        public KeepColorSmithing read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new KeepColorSmithing(identifier, ingredient, ingredient2, ingredient3, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, KeepColorSmithing smithingRecipe) {
            SmithingTransformRecipe.Serializer.SMITHING_TRANSFORM.write(packetByteBuf, smithingRecipe);
        }
    }
}
