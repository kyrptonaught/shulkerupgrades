package net.kyrptonaught.upgradedshulker.block.blockentity;

import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.stream.IntStream;

public class UpgradedShulkerBlockEntity extends ShulkerBoxBlockEntity {
    private int[] AVAILABLE_SLOTS;
    private ShulkerUpgrades.MATERIAL material;
    private NbtCompound upgrades;

    public UpgradedShulkerBlockEntity(DyeColor color, ShulkerUpgrades.MATERIAL matType, BlockPos pos, BlockState state) {
        super(color, pos, state);
        if (matType != null) setUpgradeType(matType);
        this.type = ShulkersRegistry.UPGRADEDSHULKERENTITYTYPE;
    }

    public void setUpgradeType(ShulkerUpgrades.MATERIAL type) {
        this.material = type;
        this.AVAILABLE_SLOTS = IntStream.range(0, type.size).toArray();
        this.setInvStackList(DefaultedList.ofSize(type.size, ItemStack.EMPTY));
    }

    public UpgradedShulkerBlockEntity(BlockPos pos, BlockState state) {
        this(null, null, pos, state);
    }

    public void appendUpgrades(NbtCompound tag) {
        this.upgrades = tag;

    }

    public boolean hasUpgrades() {
        return upgrades != null;
    }

    public NbtCompound getUpgrades() {
        return this.upgrades;
    }

    public ShulkerUpgrades.MATERIAL getMaterial() {
        return material;
    }

    public void readNbt(NbtCompound tag) {
        setUpgradeType(ShulkerUpgrades.MATERIAL.values()[tag.getInt("shulkertype")]);
        if (tag.contains(ShulkerUpgrades.KEY))
            upgrades = (NbtCompound) tag.get(ShulkerUpgrades.KEY);
        super.readNbt(tag);
    }

    public void writeNbt(NbtCompound tag) {
        tag.putInt("shulkertype", material.ordinal());
        if (hasUpgrades())
            tag.put(ShulkerUpgrades.KEY, upgrades);
        super.writeNbt(tag);
    }

    public int[] getAvailableSlots(Direction side) {
        return this.AVAILABLE_SLOTS;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.upgradedshulkers." + material.name + "shulker");
    }
}
