package net.kyrptonaught.upgradedshulker.mixin;

import net.kyrptonaught.shulkerutils.ItemStackInventory;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.RiftEChest;
import net.kyrptonaught.upgradedshulker.block.SpatialEChest;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean attemptHopperPickup(PlayerInventory playerInventory, ItemStack pickupStack, PlayerEntity player) {
        if (pickupStack.isEmpty())
            return false;

        for (int i = 0; i < playerInventory.size(); i++) {
            ItemStack shulker = playerInventory.getStack(i);
            if (shulker.getItem() instanceof BlockItem && ((BlockItem) shulker.getItem()).getBlock() instanceof ShulkerBoxBlock) {
                if (ShulkerUpgrades.UPGRADES.HOPPER.isOnStack(shulker)) {
                    ItemStackInventory shulkerInv = ShulkerUtils.getInventoryFromShulker(shulker);
                    if (ShulkerUtils.shulkerContainsAny(shulkerInv, pickupStack)) {
                        pickupStack = ShulkerUtils.insertIntoShulker(shulkerInv, pickupStack, player);
                        if (pickupStack.isEmpty()) {
                            this.remove();
                            return true;
                        }
                    }
                }
                if (ShulkerUpgrades.UPGRADES.VOID.isOnStack(shulker)) {
                    ItemStackInventory shulkerInv = ShulkerUtils.getInventoryFromShulker(shulker);
                    if (ShulkerUtils.shulkerContainsAny(shulkerInv, pickupStack)) {
                        pickupStack.setCount(0);
                        this.remove();
                        return true;
                    }
                }
            } else if (shulker.getItem() instanceof BlockItem && ((BlockItem) shulker.getItem()).getBlock() instanceof RiftEChest) {
                if (ShulkerUpgrades.UPGRADES.HOPPER.isOnStack(shulker)) {
                    EnderChestInventory enderChestInv = player.getEnderChestInventory();
                    if (ShulkerUtils.shulkerContainsAny(enderChestInv, pickupStack)) {
                        pickupStack = ShulkerUtils.insertIntoShulker(enderChestInv, pickupStack, player);
                        if (pickupStack.isEmpty()) {
                            this.remove();
                            return true;
                        }
                    }
                }
            }
        }
        return playerInventory.insertStack(pickupStack);
    }
}
