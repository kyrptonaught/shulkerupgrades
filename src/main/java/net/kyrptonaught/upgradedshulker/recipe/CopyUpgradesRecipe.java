package net.kyrptonaught.upgradedshulker.recipe;

import com.google.gson.JsonObject;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class CopyUpgradesRecipe extends ShapedRecipe {

    public CopyUpgradesRecipe(ShapedRecipe shapedRecipe) {
        super(shapedRecipe.getId(), UpgradedShulkerMod.MOD_ID, shapedRecipe.getWidth(), shapedRecipe.getHeight(), shapedRecipe.getPreviewInputs(), shapedRecipe.getOutput());
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack output = this.getOutput().copy();
        ItemStack shulker = craftingInventory.getStack(4);
        ShulkerUpgrades.MATERIAL type = ((UpgradedShulkerBlock) Block.getBlockFromItem(output.getItem())).material;
        DyeColor color = ((ShulkerBoxBlock) Block.getBlockFromItem(shulker.getItem())).getColor();
        output = ShulkersRegistry.getShulkerBlock(type, color).asItem().getDefaultStack();
        if (shulker.hasTag())
            output.setTag(shulker.getTag());
        return output;
    }

    public RecipeSerializer<?> getSerializer() {
        return UpgradedShulkerMod.copyDyeRecipe;
    }

    public static class Serializer implements RecipeSerializer<CopyUpgradesRecipe> {

        @Override
        public CopyUpgradesRecipe read(Identifier id, JsonObject json) {
            return new CopyUpgradesRecipe(ShapedRecipe.Serializer.SHAPED.read(id, json));
        }

        @Override
        public CopyUpgradesRecipe read(Identifier id, PacketByteBuf buf) {
            return new CopyUpgradesRecipe(ShapedRecipe.Serializer.SHAPED.read(id, buf));
        }

        @Override
        public void write(PacketByteBuf buf, CopyUpgradesRecipe recipe) {
            ShapedRecipe.Serializer.SHAPED.write(buf, recipe);
        }
    }
}