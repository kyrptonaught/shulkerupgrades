package net.kyrptonaught.upgradedshulker.block.blockentity;


import net.kyrptonaught.upgradedshulker.block.RiftEChest;
import net.kyrptonaught.upgradedshulker.util.RiftEChestInventory;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.UUID;

public class RiftChestBlockEntity extends OpenableBlockEntity {
    public RiftChestBlockEntity() {
        super(RiftEChest.blockEntity);
    }

    private UUID storedPlayer;
    private CompoundTag upgrades;

    public void appendUpgrades(CompoundTag tag) {
        this.upgrades = tag;
    }

    public boolean hasUpgrades() {
        return upgrades != null;
    }

    public CompoundTag getUpgrades() {
        return this.upgrades;
    }

    public void setStoredPlayer(PlayerEntity player) {
        this.storedPlayer = player.getUuid();
        sync();
    }

    public boolean hasStoredPlayer() {
        return storedPlayer != null;
    }

    public RiftEChestInventory getEChestInv(ServerWorld world) {
        if (!hasStoredPlayer()) return null;
        PlayerEntity player = world.getPlayerByUuid(storedPlayer);
        if (player == null) return null;
        return new RiftEChestInventory(player.getEnderChestInventory(), this);
    }

    public Text getPlayerName(ServerWorld world) {
        PlayerEntity player = world.getPlayerByUuid(storedPlayer);
        if (player == null) return null;
        return player.getName();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        if (tag.contains("storedplayer"))
            storedPlayer = tag.getUuid("storedplayer");
        if (tag.contains(ShulkerUpgrades.KEY))
            upgrades = (CompoundTag) tag.get(ShulkerUpgrades.KEY);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (storedPlayer != null)
            tag.putUuid("storedplayer", storedPlayer);
        if (hasUpgrades())
            tag.put(ShulkerUpgrades.KEY, upgrades);
        return super.toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        if (tag.contains("storedplayer"))
            storedPlayer = tag.getUuid("storedplayer");
        if (tag.contains(ShulkerUpgrades.KEY))
            upgrades = (CompoundTag) tag.get(ShulkerUpgrades.KEY);
        super.fromClientTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        if (storedPlayer != null)
            tag.putUuid("storedplayer", storedPlayer);
        if (hasUpgrades())
            tag.put(ShulkerUpgrades.KEY, upgrades);
        return super.toClientTag(tag);
    }
}
