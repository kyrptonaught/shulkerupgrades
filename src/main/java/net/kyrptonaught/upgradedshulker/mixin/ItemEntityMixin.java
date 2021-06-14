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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    private int pickupDelay;

    @Shadow
    private UUID owner;

    @Shadow public abstract void setStack(ItemStack stack);

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onPlayerCollision", at = @At(value = "HEAD"), cancellable = true)
    public void attemptHopperPickup(PlayerEntity player, CallbackInfo ci) {
        if (!this.world.isClient) {
            ItemStack itemStack = this.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid()))) {
                itemStack = putIntoHopperShulker(player, itemStack, player.getInventory());
                this.setStack(itemStack);
                if (itemStack.getCount() != i) {
                    player.sendPickup(this, i);
                    if (itemStack.isEmpty()) {
                        this.discard();
                        itemStack.setCount(i);
                        player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                        player.triggerItemPickedUpByEntityCriteria((ItemEntity) (Object) this);
                        ci.cancel();
                    } else {
                        player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                        player.triggerItemPickedUpByEntityCriteria((ItemEntity) (Object) this);
                    }
                }
            }
        }
    }

    @Unique
    private ItemStack putIntoHopperShulker(PlayerEntity player, ItemStack pickupStack, PlayerInventory playerInventory) {
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
