package net.kyrptonaught.upgradedshulker.mixin;

import net.kyrptonaught.upgradedshulker.block.SpatialEChest;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EntityExplosionBehavior.class)
public class EntityExplosionBehaviorMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "getBlastResistance", at = @At("HEAD"), cancellable = true)
    public void chargedCreeperRiftRecipe(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, CallbackInfoReturnable<Optional<Float>> cir) {
        if (entity instanceof CreeperEntity && ((CreeperEntity) entity).shouldRenderOverlay()) {//apparantly shouldRenderOverlay means charged creeper for some reason
            if (blockState.getBlock() instanceof SpatialEChest)
                cir.setReturnValue(Optional.of(0f));
        }
    }
}
