package net.kyrptonaught.upgradedshulker.mixin;

import net.kyrptonaught.upgradedshulker.util.SpatialInvStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements SpatialInvStorage {

    @Unique
    SimpleInventory spatialInv = new SimpleInventory(27);

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    public void writeSpatialEChest(CompoundTag tag, CallbackInfo ci) {
        tag.put("spatialItems", spatialInv.getTags());
    }

    @Inject(method = "readCustomDataFromTag", at = @At("TAIL"))
    public void readSpatialEChest(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("spatialItems", 9)) {
            this.spatialInv.readTags(tag.getList("spatialItems", 10));
        }
    }

    @Override
    public SimpleInventory getSpatialInv() {
        return spatialInv;
    }
}
