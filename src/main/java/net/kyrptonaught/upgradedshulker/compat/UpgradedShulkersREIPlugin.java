package net.kyrptonaught.upgradedshulker.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultShapedDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultShapelessDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class UpgradedShulkersREIPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (ShulkerUpgrades.MATERIAL material : ShulkerUpgrades.MATERIAL.values()) {
            for (DyeColor dye : DyeColor.values()) {
                ItemStack output = new ItemStack(ShulkersRegistry.getShulkerBlock(material, dye));
                registerTierUpRecipe(registry, material, dye, output);
                registerColorRecipe(registry, material, dye, output);
                registerUpgradesRecipe(registry, output);
            }
        }
    }

    private void registerColorRecipe(DisplayRegistry recipeHelper, ShulkerUpgrades.MATERIAL material, DyeColor color, ItemStack output) {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(Ingredient.fromTag(TagKey.of(Registries.ITEM.getKey(), new Identifier("us", material.name().toLowerCase() + "_shulkers"))));
        defaultedList.add(Ingredient.ofItems(DyeItem.byColor(color)));
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "dyeshulkers"), "dyeshulkers", CraftingRecipeCategory.EQUIPMENT, output, defaultedList);
        recipeHelper.add(new DefaultShapelessDisplay(shapelessRecipe));
    }

    private void registerUpgradesRecipe(DisplayRegistry recipeHelper, ItemStack output) {
        for (ShulkerUpgrades.UPGRADES upgrade : ShulkerUpgrades.UPGRADES.values()) {
            SmithingRecipe recipe = new SmithingRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "upgradeshulkertier"), Ingredient.ofStacks(output), Ingredient.ofItems(upgrade.craftingItem), upgrade.putOnStack(output.copy()));
            recipeHelper.add(new DefaultSmithingDisplay(recipe));
        }
    }

    private void registerTierUpRecipe(DisplayRegistry recipeHelper, ShulkerUpgrades.MATERIAL material, DyeColor color, ItemStack output) {
        if (material == ShulkerUpgrades.MATERIAL.NETHERITE) {
            SmithingRecipe recipe = new SmithingRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "upgradeshulkertier"), Ingredient.ofItems(ShulkersRegistry.getShulkerBlock(ShulkerUpgrades.MATERIAL.DIAMOND, color)), Ingredient.ofItems(Items.NETHERITE_INGOT), output);
            recipeHelper.add(new DefaultSmithingDisplay(recipe));
            return;
        }
        DefaultedList<Ingredient> defaultedList2 = DefaultedList.of();
        ItemConvertible upgradeMaterial;
        ItemConvertible oldShulker;
        switch (material) {
            case GOLD -> {
                upgradeMaterial = Items.GOLD_INGOT;
                oldShulker = ShulkersRegistry.getShulkerBlock(ShulkerUpgrades.MATERIAL.IRON, color);
            }
            case DIAMOND -> {
                upgradeMaterial = Items.DIAMOND;
                oldShulker = ShulkersRegistry.getShulkerBlock(ShulkerUpgrades.MATERIAL.GOLD, color);
            }
            default -> {
                upgradeMaterial = Items.IRON_INGOT;
                oldShulker = ShulkerBoxBlock.get(color);
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

        ShapedRecipe recipe = new ShapedRecipe(new Identifier(UpgradedShulkerMod.MOD_ID, "upgradeshulkertier"), "upgradeshulkertier", CraftingRecipeCategory.EQUIPMENT, 3, 3, defaultedList2, output);
        recipeHelper.add(new DefaultShapedDisplay(recipe));
    }
}