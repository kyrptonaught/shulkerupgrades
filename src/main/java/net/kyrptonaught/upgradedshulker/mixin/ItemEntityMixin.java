package net.kyrptonaught.upgradedshulker.mixin;

import net.kyrptonaught.shulkerutils.ItemStackInventory;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    public abstract void setStack(ItemStack stack);

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // Workaround for ModifyArg not being able to capture arguments of the calling method.
    @Unique private PlayerEntity addingToPlayer;

    @Inject(
            method = "onPlayerCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z")
    )
    public void UPGRADEDSHULKER$onPlayerCollision$modifyArgIsWeird(PlayerEntity player, CallbackInfo ci) {
        addingToPlayer = player;
    }

    @ModifyArg(
            method = "onPlayerCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z")
    )
    public ItemStack UPGRADEDSHULKER$onPlayerCollision$modifyInsertStack(ItemStack oldStack) {
        PlayerEntity player = addingToPlayer;
        addingToPlayer = null;
        int oldCount = oldStack.getCount();

        ItemStack newStack = putIntoShulker(player, oldStack, player.getInventory());

        // Manually play the pickup animation before calling setStack - else you might get a pickup animation for an empty stack, which just
        // makes it look like the item vanishes. This looks bad.
        if(oldCount > 0 && newStack.isEmpty())
            player.sendPickup((ItemEntity) (Object) this, oldCount);

        // It's possible for putIntoShulker to return a new instance of ItemStack (e.g. ItemStack.EMPTY), instead of mutating oldStack.
        // If this happens, we should update the ItemEntity to reflect the new stack instance.
        if(oldStack != newStack)
            setStack(newStack);

        return newStack;
    }

    @Unique
    private ItemStack putIntoShulker(PlayerEntity player, ItemStack pickupStack, PlayerInventory playerInventory) {
        if (pickupStack.getCount() > 0) {
            for (int i = 0; i < playerInventory.size(); i++) {
                ItemStack shulker = playerInventory.getStack(i);
                if (shulker.getItem() instanceof BlockItem && ((BlockItem) shulker.getItem()).getBlock() instanceof ShulkerBoxBlock) {
                    if (ShulkerUpgrades.UPGRADES.HOPPER.isOnStack(shulker)) {
                        ItemStackInventory shulkerInv = ShulkerUtils.getInventoryFromShulker(shulker);
                        if (ShulkerUtils.shulkerContainsAny(shulkerInv, pickupStack)) {
                            pickupStack = ShulkerUtils.insertIntoShulker(shulkerInv, pickupStack, player);
                            if (pickupStack.getCount() <= 0)
                                break;
                        }
                    }
                    if (ShulkerUpgrades.UPGRADES.VOID.isOnStack(shulker)) {
                        ItemStackInventory shulkerInv = ShulkerUtils.getInventoryFromShulker(shulker);
                        if (ShulkerUtils.shulkerContainsAny(shulkerInv, pickupStack)) {
                            pickupStack.setCount(0);
                            break;
                        }
                    }
                }
            }
        }
        return pickupStack;
    }
}
