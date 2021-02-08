package net.kyrptonaught.upgradedshulker.mixin;

import net.kyrptonaught.upgradedshulker.block.blockentity.RiftChestBlockEntity;
import net.kyrptonaught.upgradedshulker.block.blockentity.SpatialEChestBlockEntity;
import net.kyrptonaught.upgradedshulker.client.UpgradedShulkerClientMod;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenerLayerMixin {

    @Inject(method = "Lnet/minecraft/client/render/TexturedRenderLayers;getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;", at = @At("HEAD"), cancellable = true)
    private static void getUpgradedTexture(BlockEntity blockEntity, ChestType type, boolean christmas, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if (blockEntity instanceof SpatialEChestBlockEntity)
            cir.setReturnValue(new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, UpgradedShulkerClientMod.SPATIAL_ECHEST_TEXTURE));
        else if (blockEntity instanceof RiftChestBlockEntity) {
            if (((RiftChestBlockEntity) blockEntity).hasUpgrades())
                cir.setReturnValue(new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, UpgradedShulkerClientMod.RIFT_ECHEST_HOPPER_TEXTURE));
            else
                cir.setReturnValue(new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, UpgradedShulkerClientMod.RIFT_ECHEST_TEXTURE));
        }
    }
}
