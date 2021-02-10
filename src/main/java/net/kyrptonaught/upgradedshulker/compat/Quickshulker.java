package net.kyrptonaught.upgradedshulker.compat;

import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.kyrptonaught.upgradedshulker.block.RiftEChest;
import net.kyrptonaught.upgradedshulker.block.SpatialEChest;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.screen.UpgradedShulkerScreenHandler;
import net.kyrptonaught.upgradedshulker.inv.SpatialEChestInventory;
import net.kyrptonaught.upgradedshulker.util.ContainerNames;
import net.kyrptonaught.upgradedshulker.util.SpatialInvStorage;
import net.minecraft.item.BlockItem;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class Quickshulker implements RegisterQuickShulker {
    @Override
    public void registerProviders() {
        QuickOpenableRegistry.register(UpgradedShulkerBlock.class, (playerEntity, stack) -> {
            Text text = new TranslatableText("block.upgradedshulkers." + ((UpgradedShulkerBlock) ((BlockItem) stack.getItem()).getBlock()).material.name + "shulker");
            playerEntity.openHandledScreen(UpgradedShulkerScreenHandler.createScreenHandlerFactory(ShulkerUtils.getInventoryFromShulker(stack), text));
        });
        QuickOpenableRegistry.register(SpatialEChest.class, (player, stack) -> {
            SpatialEChestInventory inv = new SpatialEChestInventory(player, ((SpatialInvStorage) player).getSpatialInv());
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
                return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inv);
            }, ContainerNames.SPATIAL_CHEST));
        });
        QuickOpenableRegistry.register(RiftEChest.class, (player, stack) -> {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
                return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, player.getEnderChestInventory());
            }, ContainerNames.getRiftChestName(player.getName())));
        });
    }
}
