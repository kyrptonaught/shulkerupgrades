package net.kyrptonaught.upgradedshulker.util;

import net.kyrptonaught.upgradedshulker.block.blockentity.OpenableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class RiftEChestInventory extends SimpleInventory implements SidedInventory {
    EnderChestInventory enderChestInventory;
    private OpenableBlockEntity activeBlockEntity;

    public RiftEChestInventory(EnderChestInventory enderChestInventory, OpenableBlockEntity activeBlockEntity) {
        super(0);
        this.enderChestInventory = enderChestInventory;
        this.activeBlockEntity = activeBlockEntity;
    }

    public void onOpen(PlayerEntity player) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onOpen();
        }

        super.onOpen(player);
    }

    public void onClose(PlayerEntity player) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onClose();
        }

        super.onClose(player);
        this.activeBlockEntity = null;
    }


    @Override
    public int[] getAvailableSlots(Direction side) {
        return IntStream.range(0, size()).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        return enderChestInventory.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return enderChestInventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return enderChestInventory.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return enderChestInventory.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        enderChestInventory.setStack(slot, stack);
    }
}
