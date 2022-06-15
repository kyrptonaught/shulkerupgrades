package net.kyrptonaught.upgradedshulker.compat;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.screen.UpgradedShulkerScreenHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.text.Text;

public class Quickshulker implements RegisterQuickShulker {
    @Override
    public void registerProviders() {
        if (QuickShulkerMod.getConfig().quickShulkerBox)
            QuickOpenableRegistry.register(UpgradedShulkerBlock.class, true, (playerEntity, stack) -> {
                Text text = Text.translatable("block.upgradedshulkers." + ((UpgradedShulkerBlock) ((BlockItem) stack.getItem()).getBlock()).material.name + "shulker");
                playerEntity.openHandledScreen(UpgradedShulkerScreenHandler.createScreenHandlerFactory(ShulkerUtils.getInventoryFromShulker(stack), text));
            });
    }
}
