package net.kyrptonaught.upgradedshulker.recipe;


import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DyeShulkerRecipe extends SpecialCraftingRecipe {
    public DyeShulkerRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id,category);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int shulkers = 0, dye = 0;
        for (int i = 0; i < inv.size(); i++)
            if (Block.getBlockFromItem(inv.getStack(i).getItem()) instanceof UpgradedShulkerBlock)
                shulkers++;
            else if (inv.getStack(i).getItem() instanceof DyeItem)
                dye++;
        return shulkers == dye && shulkers == 1;
    }

    @Override
    public ItemStack craft(CraftingInventory inv, DynamicRegistryManager registryManager) {
        ItemStack shulkerStack = ItemStack.EMPTY;
        ItemStack dyeStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); i++)
            if (Block.getBlockFromItem(inv.getStack(i).getItem()) instanceof UpgradedShulkerBlock)
                shulkerStack = inv.getStack(i);
            else if (inv.getStack(i).getItem() instanceof DyeItem)
                dyeStack = inv.getStack(i);
        UpgradedShulkerBlock shulkerBlock = (UpgradedShulkerBlock) Block.getBlockFromItem(shulkerStack.getItem());

        ItemStack coloredStack = ShulkersRegistry.getShulkerBlock(shulkerBlock.material, ((DyeItem) dyeStack.getItem()).getColor()).asItem().getDefaultStack();
        if (shulkerStack.hasNbt())
            coloredStack.setNbt(shulkerStack.getNbt());
        return coloredStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return UpgradedShulkerMod.dyeShulkerRecipe;
    }
}
