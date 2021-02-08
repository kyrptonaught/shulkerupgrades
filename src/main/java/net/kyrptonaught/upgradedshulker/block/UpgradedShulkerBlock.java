package net.kyrptonaught.upgradedshulker.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.shulkerutils.UpgradableShulker;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.blockentity.UpgradedShulkerBlockEntity;
import net.kyrptonaught.upgradedshulker.screen.UpgradedShulkerScreenHandler;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.UpgradedShulker;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
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

    public BlockEntity createBlockEntity(BlockView world) {
        return new UpgradedShulkerBlockEntity(this.getColor(), material);
    }

    @Override
    public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
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
        if (itemStack.hasTag()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof UpgradedShulkerBlockEntity) {
                ((UpgradedShulkerBlockEntity) blockEntity).appendUpgrades(itemStack.getSubTag(ShulkerUpgrades.KEY));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof UpgradedShulkerBlockEntity) {
            if (((UpgradedShulkerBlockEntity) blockEntity).hasUpgrades())
                itemStack.putSubTag(ShulkerUpgrades.KEY, ((UpgradedShulkerBlockEntity) blockEntity).getUpgrades());
        }
        return itemStack;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof UpgradedShulkerBlockEntity) {
                UpgradedShulkerBlockEntity shulkerBoxBlockEntity = (UpgradedShulkerBlockEntity) blockEntity;
                player.openHandledScreen(UpgradedShulkerScreenHandler.createScreenHandlerFactory(shulkerBoxBlockEntity, shulkerBoxBlockEntity.getDisplayName()));
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof UpgradedShulkerBlockEntity) {
            UpgradedShulkerBlockEntity shulkerBoxBlockEntity = (UpgradedShulkerBlockEntity) blockEntity;
            if (!world.isClient && player.isCreative() && !shulkerBoxBlockEntity.isEmpty()) {
                ItemStack itemStack = new ItemStack(this);
                if (!shulkerBoxBlockEntity.isEmpty()) {
                    CompoundTag compoundTag = shulkerBoxBlockEntity.serializeInventory(new CompoundTag());
                    if (!compoundTag.isEmpty()) {
                        itemStack.putSubTag("BlockEntityTag", compoundTag);
                    }
                }
                if (shulkerBoxBlockEntity.hasCustomName()) {
                    itemStack.setCustomName(shulkerBoxBlockEntity.getCustomName());
                }
                if (shulkerBoxBlockEntity.hasUpgrades())
                    itemStack.putSubTag(ShulkerUpgrades.KEY, shulkerBoxBlockEntity.getUpgrades());
                ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                shulkerBoxBlockEntity.checkLootInteraction(player);
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        CompoundTag upgradeTag = stack.getSubTag(ShulkerUpgrades.KEY);
        if (upgradeTag != null) {
            tooltip.add(new TranslatableText("upgradedshulkers.hasupgrades"));
            for (String str : upgradeTag.getKeys())
                tooltip.add(new TranslatableText("upgradedshulkers.upgrade." + str));
        }
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag != null) {
            if (compoundTag.contains("LootTable", 8)) {
                tooltip.add(new LiteralText("???????"));
            }

            if (compoundTag.contains("Items", 9)) {
                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(material.size, ItemStack.EMPTY);
                Inventories.fromTag(compoundTag, defaultedList);
                int i = 0;
                int j = 0;
                Iterator var9 = defaultedList.iterator();

                while (var9.hasNext()) {
                    ItemStack itemStack = (ItemStack) var9.next();
                    if (!itemStack.isEmpty()) {
                        ++j;
                        if (i <= 4) {
                            ++i;
                            MutableText mutableText = itemStack.getName().shallowCopy();
                            mutableText.append(" x").append(String.valueOf(itemStack.getCount()));
                            tooltip.add(mutableText);
                        }
                    }
                }

                if (j - i > 0) {
                    tooltip.add((new TranslatableText("container.shulkerBox.more", j - i)).formatted(Formatting.ITALIC));
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
