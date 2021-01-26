package net.kyrptonaught.upgradedshulker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.upgradedshulker.util.UpgradedShulker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends Block implements UpgradedShulker {


    public ShulkerBoxBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onBreak", at = @At(value = "HEAD"), cancellable = true)
    public void UPGRADEDSHULKER$onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (isUpgradedShulker()) {
            super.onBreak(world, pos, state, player);
            ci.cancel();
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "appendTooltip", at = @At(value = "HEAD"), cancellable = true)
    public void UPGRADEDSHULKER$appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options, CallbackInfo ci) {
        if (isUpgradedShulker()) {
            super.appendTooltip(stack, world, tooltip, options);
            ci.cancel();
        }
    }

    @Override
    public boolean isUpgradedShulker() {
        return false;
    }
}
