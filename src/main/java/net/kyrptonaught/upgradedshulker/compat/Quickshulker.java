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
            new QuickOpenableRegistry.Builder()
                    .setItem(UpgradedShulkerBlock.class)
                    .supportsBundleing(true)
                    .setOpenAction((player, itemStack) -> {
                        Text text = Text.translatable("block.upgradedshulkers." + ((UpgradedShulkerBlock) ((BlockItem) itemStack.getItem()).getBlock()).material.name + "shulker");
                        player.openHandledScreen(UpgradedShulkerScreenHandler.createScreenHandlerFactory(ShulkerUtils.getInventoryFromShulker(itemStack), text));
                    }).register();
    }
}
