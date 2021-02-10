package net.kyrptonaught.upgradedshulker.inv;

import net.kyrptonaught.upgradedshulker.block.blockentity.SpatialEChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class SpatialEChestInventory implements Inventory {
    private SpatialEChestBlockEntity activeBlockEntity;
    private final PlayerEntity playerEntity;
    private final SimpleInventory extraInventory;


    public SpatialEChestInventory(PlayerEntity playerEntity, SimpleInventory spatialInv) {
        super();
        extraInventory = spatialInv;
        this.playerEntity = playerEntity;
    }

    public void setActiveBlockEntity(SpatialEChestBlockEntity blockEntity) {
        this.activeBlockEntity = blockEntity;
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 27)
            return playerEntity.getEnderChestInventory().getStack(slot);
        return extraInventory.getStack(slot - 27);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 27)
            return playerEntity.getEnderChestInventory().removeStack(slot, amount);
        return extraInventory.removeStack(slot - 27, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 27)
            return playerEntity.getEnderChestInventory().removeStack(slot);
        return extraInventory.removeStack(slot - 27);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 27)
            playerEntity.getEnderChestInventory().setStack(slot, stack);
        else
            extraInventory.setStack(slot - 27, stack);
    }

    @Override
    public void markDirty() {
        playerEntity.getEnderChestInventory().markDirty();
        extraInventory.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return playerEntity.getEnderChestInventory().isEmpty() || extraInventory.isEmpty();
    }

    @Override
    public void clear() {
        extraInventory.clear();
        this.markDirty();
    }

    public void onOpen(PlayerEntity player) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onOpen();
        }

    }

    @Override
    public void onClose(PlayerEntity player) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onClose();
        }
        this.activeBlockEntity = null;
    }
}
