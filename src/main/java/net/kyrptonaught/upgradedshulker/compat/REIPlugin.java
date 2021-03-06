package net.kyrptonaught.upgradedshulker.compat;

import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.crafting.DefaultShapedDisplay;
import me.shedaniel.rei.plugin.crafting.DefaultShapelessDisplay;
import me.shedaniel.rei.plugin.smithing.DefaultSmithingDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class REIPlugin implements REIPluginV0 {

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(UpgradedShulkerMod.MOD_ID, "upgradedshulkers");
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        for (ShulkerUpgrades.MATERIAL material : ShulkerUpgrades.MATERIAL.values()) {
            for (DyeColor dye : DyeColor.values()) {
                ItemStack output = new ItemStack(ShulkersRegistry.getShulkerBlock(material, dye));
                registerTierUpRecipe(recipeHelper, material, dye, output);
                registerColorRecipe(recipeHelper, material, dye, output);
                registerUpgradesRecipe(recipeHelper, output);
            }
        }
    }

    private void registerColorRecipe(RecipeHelper recipeHelper, ShulkerUpgrades.MATERIAL material, DyeColor color, ItemStack output) {
        Tag<Item> tag = ServerTagManagerHolder.getTagManager().getItems().getTag(new Identifier("us", material.name().toLowerCase() + "_shulkers"));
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(Ingredient.fromTag(tag));
        defaultedList.add(Ingredient.ofItems(DyeItem.byColor(color)));
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "dyeshulkers"), "dyeshulkers", output, defaultedList);
        recipeHelper.registerDisplay(new DefaultShapelessDisplay(shapelessRecipe));
    }

    private void registerUpgradesRecipe(RecipeHelper recipeHelper, ItemStack output) {
        for (ShulkerUpgrades.UPGRADES upgrade : ShulkerUpgrades.UPGRADES.values()) {
            SmithingRecipe recipe = new SmithingRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "upgradeshulkertier"), Ingredient.ofStacks(output), Ingredient.ofItems(upgrade.craftingItem), upgrade.putOnStack(output.copy()));
            recipeHelper.registerDisplay(new DefaultSmithingDisplay(recipe));
        }
    }

    private void registerTierUpRecipe(RecipeHelper recipeHelper, ShulkerUpgrades.MATERIAL material, DyeColor color, ItemStack output) {
        if (material == ShulkerUpgrades.MATERIAL.NETHERITE) {
            SmithingRecipe recipe = new SmithingRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "upgradeshulkertier"), Ingredient.ofItems(ShulkersRegistry.getShulkerBlock(ShulkerUpgrades.MATERIAL.DIAMOND, color)), Ingredient.ofItems(Items.NETHERITE_INGOT), output);
            recipeHelper.registerDisplay(new DefaultSmithingDisplay(recipe));
            return;
        }
        DefaultedList<Ingredient> defaultedList2 = DefaultedList.of();
        ItemConvertible upgradeMaterial;
        ItemConvertible oldShulker;
        switch (material) {
            case GOLD: {
                upgradeMaterial = Items.GOLD_INGOT;
                oldShulker = ShulkersRegistry.getShulkerBlock(ShulkerUpgrades.MATERIAL.IRON, color);
                break;
            }
            case DIAMOND: {
                upgradeMaterial = Items.DIAMOND;
                oldShulker = ShulkersRegistry.getShulkerBlock(ShulkerUpgrades.MATERIAL.GOLD, color);
                break;
            }
            default: {
                upgradeMaterial = Items.IRON_INGOT;
                oldShulker = ShulkerBoxBlock.get(color);
                break;
            }
        }
        defaultedList2.add(Ingredient.ofItems(upgradeMaterial));
        defaultedList2.add(Ingredient.ofItems(Items.SHULKER_SHELL));
        defaultedList2.add(Ingredient.ofItems(upgradeMaterial));

        defaultedList2.add(Ingredient.ofItems(upgradeMaterial));
        defaultedList2.add(Ingredient.ofItems(oldShulker));
        defaultedList2.add(Ingredient.ofItems(upgradeMaterial));

        defaultedList2.add(Ingredient.ofItems(upgradeMaterial));
        defaultedList2.add(Ingredient.ofItems(Items.SHULKER_SHELL));
        defaultedList2.add(Ingredient.ofItems(upgradeMaterial));

        ShapedRecipe recipe = new ShapedRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "upgradeshulkertier"), "upgradeshulkertier", 3, 3, defaultedList2, output);
        recipeHelper.registerDisplay(new DefaultShapedDisplay(recipe));
    }
}