package net.kyrptonaught.upgradedshulker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.blockentity.UpgradedShulkerBlockEntity;
import net.kyrptonaught.upgradedshulker.screen.UpgradedShulkerScreen;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class UpgradedShulkerClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(UpgradedShulkerMod.US_SCREEN_HANDLER_TYPE, UpgradedShulkerScreen::new);
        for (ShulkerUpgrades.MATERIAL type : ShulkerUpgrades.MATERIAL.values()) {
            registerShulkerWith(null, type);
            BlockEntityRendererFactories.register(ShulkersRegistry.UPGRADEDSHULKERENTITYTYPE, UpgradedShulkerBoxRenderer::new);
            for (DyeColor color : DyeColor.values())
                registerShulkerWith(color, type);
        }
    }

    private void registerShulkerWith(DyeColor color, ShulkerUpgrades.MATERIAL type) {
        BuiltinItemRendererRegistry.INSTANCE.register(ShulkersRegistry.getShulkerBlock(type, color), (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            UpgradedShulkerBlockEntity be = new UpgradedShulkerBlockEntity(color, type, BlockPos.ORIGIN, ShulkersRegistry.getShulkerBlock(type, color).getDefaultState());
            NbtCompound tag = stack.getSubNbt(ShulkerUpgrades.KEY);
            if (tag != null) be.appendUpgrades(tag);
            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(be, matrices, vertexConsumers, light, overlay);
        });
    }

    public static Identifier getTextureFor(DyeColor color, ShulkerUpgrades.MATERIAL type, String modifier) {
        String colorName = color != null ? color.getName() : "normal";
        return new Identifier("us", "shulker/" + colorName + "/shulker_" + colorName + "_" + type.name + modifier);
    }
}
