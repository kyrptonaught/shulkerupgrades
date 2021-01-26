package net.kyrptonaught.upgradedshulker.mixin;

import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserMixin {

    @Inject(method = "getBehaviorForItem", at = @At(value = "HEAD"), cancellable = true)
    public void upgradedShulkerBehavior(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cir) {
        if (Block.getBlockFromItem(stack.getItem())instanceof UpgradedShulkerBlock)
            cir.setReturnValue(new BlockPlacementDispenserBehavior());
    }
}
