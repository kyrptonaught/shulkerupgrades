package net.kyrptonaught.upgradedshulker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.upgradedshulker.block.blockentity.UpgradedShulkerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class UpgradedShulkerBoxRenderer<T extends UpgradedShulkerBlockEntity> extends BlockEntityRenderer<T> {
    private final ShulkerEntityModel<?> model = new ShulkerEntityModel<>();

    public UpgradedShulkerBoxRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);

    }

    public void render(T shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = Direction.UP;
        if (shulkerBoxBlockEntity.hasWorld()) {
            BlockState blockState = shulkerBoxBlockEntity.getWorld().getBlockState(shulkerBoxBlockEntity.getPos());
            if (blockState.getBlock() instanceof ShulkerBoxBlock) {
                direction = blockState.get(ShulkerBoxBlock.FACING);
            }
        }
        String modifier = "";
        if (shulkerBoxBlockEntity.hasUpgrades()) {
            shulkerBoxBlockEntity.getUpgrades().getKeys();
            for (String upgrade : shulkerBoxBlockEntity.getUpgrades().getKeys())
                modifier += "_" + upgrade;
        }
        SpriteIdentifier spriteIdentifier;
        Identifier id = UpgradedShulkerClientMod.getTextureFor(shulkerBoxBlockEntity.getColor(), shulkerBoxBlockEntity.getMaterial(), modifier);

        spriteIdentifier = new SpriteIdentifier(TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE, id);

        matrixStack.push();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
        matrixStack.multiply(direction.getRotationQuaternion());
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        matrixStack.translate(0.0D, -1.0D, 0.0D);

        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.model.getBottomShell().render(matrixStack, vertexConsumer, i, j);
        matrixStack.translate(0.0D, -shulkerBoxBlockEntity.getAnimationProgress(f) * 0.5F, 0.0D);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F * shulkerBoxBlockEntity.getAnimationProgress(f)));
        this.model.getTopShell().render(matrixStack, vertexConsumer, i, j);
        matrixStack.pop();
    }
}
