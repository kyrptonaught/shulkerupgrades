package net.kyrptonaught.upgradedshulker.util;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.block.blockentity.UpgradedShulkerBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.Material;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShulkersRegistry {

    public static BlockEntityType<UpgradedShulkerBlockEntity> UPGRADEDSHULKERENTITYTYPE;
    public static final HashMap<ShulkerUpgrades.MATERIAL, HashMap<DyeColor, UpgradedShulkerBlock>> SHULKER_BLOCKS = new HashMap<>();
    public static HashMap<ShulkerUpgrades.MATERIAL, UpgradedShulkerBlock> upgradedShulkerBlocks = new HashMap<>();

    public static void init() {
        List<UpgradedShulkerBlock> boxes = new ArrayList<>();
        for (ShulkerUpgrades.MATERIAL type : ShulkerUpgrades.MATERIAL.values()) {
            UpgradedShulkerBlock baseShulker = createShulkerBoxBlock(type, null);
            upgradedShulkerBlocks.put(type, baseShulker);
            boxes.add(baseShulker);
            SHULKER_BLOCKS.put(type, new HashMap<>());
            for (DyeColor color : DyeColor.values()) {
                UpgradedShulkerBlock block = createShulkerBoxBlock(type, color);
                SHULKER_BLOCKS.get(type).put(color, block);
                boxes.add(block);
            }
        }
        UPGRADEDSHULKERENTITYTYPE = Registry.register(Registries.BLOCK_ENTITY_TYPE, UpgradedShulkerMod.MOD_ID + ":shulker", FabricBlockEntityTypeBuilder.create(UpgradedShulkerBlockEntity::new, boxes.toArray(new UpgradedShulkerBlock[0])).build(null));

        BlockPlacementDispenserBehavior dispenseBehavior = new BlockPlacementDispenserBehavior();
        for(UpgradedShulkerBlock box : boxes) {
            DispenserBlock.registerBehavior(box, dispenseBehavior);
        }
    }

    public static UpgradedShulkerBlock getShulkerBlock(ShulkerUpgrades.MATERIAL type, DyeColor color) {
        if (color == null) return upgradedShulkerBlocks.get(type);
        return SHULKER_BLOCKS.get(type).get(color);
    }

    private static UpgradedShulkerBlock createShulkerBoxBlock(ShulkerUpgrades.MATERIAL type, DyeColor color) {
        AbstractBlock.ContextPredicate contextPredicate = (blockState, blockView, blockPos) -> {
            BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
            if (!(blockEntity instanceof ShulkerBoxBlockEntity)) {
                return true;
            } else {
                ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity) blockEntity;
                return shulkerBoxBlockEntity.suffocates();
            }
        };
        AbstractBlock.Settings settings;
        if (color == null)
            settings = AbstractBlock.Settings.of(Material.SHULKER_BOX);
        else
            settings = AbstractBlock.Settings.of(Material.SHULKER_BOX, color);
        return new UpgradedShulkerBlock(type, color, settings.strength(2.0F, type == ShulkerUpgrades.MATERIAL.NETHERITE ? 1200 : 2).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));
    }
}
