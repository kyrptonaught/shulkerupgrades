package net.kyrptonaught.upgradedshulker.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.shulkerutils.UpgradableShulker;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.blockentity.UpgradedShulkerBlockEntity;
import net.kyrptonaught.upgradedshulker.screen.UpgradedShulkerScreenHandler;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.kyrptonaught.upgradedshulker.util.UpgradedShulker;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradedShulkerBlock extends ShulkerBoxBlock implements UpgradedShulker, UpgradableShulker {
    public ShulkerUpgrades.MATERIAL material;

    public UpgradedShulkerBlock(ShulkerUpgrades.MATERIAL upgradedshulkertype, DyeColor color, Settings blockSettings) {
        super(color, blockSettings);
        this.material = upgradedshulkertype;
        String colorName = color != null ? color.getName() : "normal";
        Registry.register(Registry.BLOCK, new Identifier("us", colorName + upgradedshulkertype.name + "shulker"), this);

        Item.Settings itemSettings = new Item.Settings().maxCount(1).group(UpgradedShulkerMod.GROUP);
        if (material == ShulkerUpgrades.MATERIAL.NETHERITE) itemSettings = itemSettings.fireproof();
        Registry.register(Registry.ITEM, new Identifier("us", colorName + upgradedshulkertype.name + "shulker"), new BlockItem(this, itemSettings));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UpgradedShulkerBlockEntity(this.getColor(), material, pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient & type == ShulkersRegistry.UPGRADEDSHULKERENTITYTYPE ? (world1, pos, state1, blockEntity) -> ShulkerBoxBlockEntity.tick(world, pos, state, (ShulkerBoxBlockEntity) blockEntity) : null;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        ItemStack shulkerStack = new ItemStack(this);
        list.add(shulkerStack.copy());
        for (ShulkerUpgrades.UPGRADES upgrade : material.getApplicableUpgrades()) {
            ItemStack upgradedStack = shulkerStack.copy();
            upgrade.putOnStack(upgradedStack);
            list.add(upgradedStack);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (itemStack.hasNbt()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof UpgradedShulkerBlockEntity) {
                ((UpgradedShulkerBlockEntity) blockEntity).appendUpgrades(itemStack.getSubNbt(ShulkerUpgrades.KEY));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof UpgradedShulkerBlockEntity) {
            if (((UpgradedShulkerBlockEntity) blockEntity).hasUpgrades())
                itemStack.setSubNbt(ShulkerUpgrades.KEY, ((UpgradedShulkerBlockEntity) blockEntity).getUpgrades());
        }
        return itemStack;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return ActionResult.CONSUME;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof UpgradedShulkerBlockEntity shulkerBoxBlockEntity) {
                boolean bl2;
                if (shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                    Box box = ShulkerEntity.calculateBoundingBox(state.get(FACING), 0.0F, 0.5F).offset(pos).contract(1.0E-6D);
                    bl2 = world.isSpaceEmpty(box);
                } else {
                    bl2 = true;
                }

                if (bl2) {
                    player.openHandledScreen(UpgradedShulkerScreenHandler.createScreenHandlerFactory(shulkerBoxBlockEntity, shulkerBoxBlockEntity.getDisplayName()));
                    player.incrementStat(Stats.OPEN_SHULKER_BOX);
                    PiglinBrain.onGuardedBlockInteracted(player, true);
                }

                return ActionResult.CONSUME;
            } else {
                return ActionResult.PASS;
            }
        }
    }


    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof UpgradedShulkerBlockEntity shulkerBoxBlockEntity) {
            if (!world.isClient && player.isCreative() && !shulkerBoxBlockEntity.isEmpty()) {
                ItemStack itemStack = new ItemStack(this);
                if (!shulkerBoxBlockEntity.isEmpty()) {
                    blockEntity.setStackNbt(itemStack);
                }
                if (shulkerBoxBlockEntity.hasCustomName()) {
                    itemStack.setCustomName(shulkerBoxBlockEntity.getCustomName());
                }
                if (shulkerBoxBlockEntity.hasUpgrades())
                    itemStack.setSubNbt(ShulkerUpgrades.KEY, shulkerBoxBlockEntity.getUpgrades());
                ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                shulkerBoxBlockEntity.checkLootInteraction(player);
            }
        }

        super.onBreak(world, pos, state, player);
    }


    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof UpgradedShulkerBlockEntity shulkerBoxBlockEntity) {
            return UpgradedShulkerScreenHandler.createScreenHandlerFactory(shulkerBoxBlockEntity, shulkerBoxBlockEntity.getDisplayName());
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        NbtCompound upgradeTag = stack.getSubNbt(ShulkerUpgrades.KEY);
        if (upgradeTag != null) {
            tooltip.add(Text.translatable("upgradedshulkers.hasupgrades"));
            for (String str : upgradeTag.getKeys())
                tooltip.add(Text.translatable("upgradedshulkers.upgrade." + str));
        }
        NbtCompound compoundTag = stack.getSubNbt("BlockEntityTag");
        if (compoundTag != null) {
            if (compoundTag.contains("LootTable", 8)) {
                tooltip.add(Text.translatable("upgradedshulkers.tooltip.?"));
            }

            if (compoundTag.contains("Items", 9)) {
                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(material.size, ItemStack.EMPTY);
                Inventories.readNbt(compoundTag, defaultedList);
                int i = 0;
                int j = 0;

                for (ItemStack itemStack : defaultedList) {
                    if (!itemStack.isEmpty()) {
                        ++j;
                        if (i <= 4) {
                            ++i;
                            MutableText mutableText = itemStack.getName().copy();
                            mutableText.append(" x").append(String.valueOf(itemStack.getCount()));
                            tooltip.add(mutableText);
                        }
                    }
                }

                if (j - i > 0) {
                    tooltip.add((Text.translatable("container.shulkerBox.more", j - i)).formatted(Formatting.ITALIC));
                }
            }
        }

    }

    @Override
    public boolean isUpgradedShulker() {
        return true;
    }

    @Override
    public int getInventorySize() {
        return material.size;
    }
}
