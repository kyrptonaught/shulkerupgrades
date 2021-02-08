package net.kyrptonaught.upgradedshulker.client;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.RiftEChest;
import net.kyrptonaught.upgradedshulker.block.SpatialEChest;
import net.kyrptonaught.upgradedshulker.block.blockentity.RiftChestBlockEntity;
import net.kyrptonaught.upgradedshulker.block.blockentity.UpgradedShulkerBlockEntity;
import net.kyrptonaught.upgradedshulker.screen.UpgradedShulkerScreen;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class UpgradedShulkerClientMod implements ClientModInitializer {
    public static final HashMap<String, Identifier> SHULKER_TEXTURES = new HashMap<>();
    public static final Identifier SPATIAL_ECHEST_TEXTURE = new Identifier("us", "blocks/dimensional_chest_32");
    public static final Identifier RIFT_ECHEST_TEXTURE = new Identifier("us", "blocks/rift_chest_32");
    public static final Identifier RIFT_ECHEST_HOPPER_TEXTURE = new Identifier("us", "blocks/rift_chest_hopper_32");
    public static final DefaultParticleType BLUEPARTICLE = Registry.register(Registry.PARTICLE_TYPE, "us" + ":blueparticle", FabricParticleTypes.simple(true));
    public static final DefaultParticleType GREENPARTICLE = Registry.register(Registry.PARTICLE_TYPE, "us" + ":greenparticle", FabricParticleTypes.simple(true));

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(UpgradedShulkerMod.LINKED_SCREEN_HANDLER_TYPE, UpgradedShulkerScreen::new);
        for (ShulkerUpgrades.MATERIAL type : ShulkerUpgrades.MATERIAL.values()) {
            registerShulkerWith(null, type);
            BlockEntityRendererRegistry.INSTANCE.register(ShulkersRegistry.UPGRADEDSHULKERENTITYTYPE, UpgradedShulkerBoxRenderer::new);
            for (DyeColor color : DyeColor.values())
                registerShulkerWith(color, type);
        }

        BlockEntityRendererRegistry.INSTANCE.register(SpatialEChest.blockEntity, ChestBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(RiftEChest.blockEntity, ChestBlockEntityRenderer::new);
        ClientSpriteRegistryCallback.event(TexturedRenderLayers.CHEST_ATLAS_TEXTURE).register((atlasTexture, registry) -> registry.register(SPATIAL_ECHEST_TEXTURE));
        ClientSpriteRegistryCallback.event(TexturedRenderLayers.CHEST_ATLAS_TEXTURE).register((atlasTexture, registry) -> registry.register(RIFT_ECHEST_TEXTURE));
        ClientSpriteRegistryCallback.event(TexturedRenderLayers.CHEST_ATLAS_TEXTURE).register((atlasTexture, registry) -> registry.register(RIFT_ECHEST_HOPPER_TEXTURE));
        ParticleFactoryRegistry.getInstance().register(BLUEPARTICLE, ColoredPortalParticle.BlueFactory::new);
        ParticleFactoryRegistry.getInstance().register(GREENPARTICLE, ColoredPortalParticle.GreenFactory::new);
        BuiltinItemRendererRegistry.INSTANCE.register(UpgradedShulkerMod.spatialEChest, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(SpatialEChest.blockEntity.instantiate(), matrices, vertexConsumers, light, overlay);
        });
        BuiltinItemRendererRegistry.INSTANCE.register(UpgradedShulkerMod.riftEChest, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            RiftChestBlockEntity be = new RiftChestBlockEntity();
            CompoundTag tag = stack.getSubTag(ShulkerUpgrades.KEY);
            if (tag != null) be.appendUpgrades(tag);
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(be, matrices, vertexConsumers, light, overlay);
        });
    }

    private void registerShulkerWith(DyeColor color, ShulkerUpgrades.MATERIAL type) {
        registerTexture(color, type, "");
        for (ShulkerUpgrades.UPGRADES upgrade : type.getApplicableUpgrades())
            registerTexture(color, type, "_" + upgrade.name);
        BuiltinItemRendererRegistry.INSTANCE.register(ShulkersRegistry.getShulkerBlock(type, color), (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            UpgradedShulkerBlockEntity be = new UpgradedShulkerBlockEntity(color, type);
            CompoundTag tag = stack.getSubTag(ShulkerUpgrades.KEY);
            if (tag != null) be.appendUpgrades(tag);
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(be, matrices, vertexConsumers, light, overlay);
        });
    }

    private void registerTexture(DyeColor color, ShulkerUpgrades.MATERIAL type, String modifier) {
        String colorName = color != null ? color.getName() : "normal";
        Identifier id = new Identifier("us", "shulker/" + colorName + "/shulker_" + colorName + "_" + type.name + modifier);
        ClientSpriteRegistryCallback.event(TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE).register((atlasTexture, registry) -> registry.register(id));
        SHULKER_TEXTURES.put(colorName + type.name + modifier, id);
    }

    public static Identifier getTextureFor(DyeColor color, ShulkerUpgrades.MATERIAL type, String modifier) {
        return SHULKER_TEXTURES.get((color != null ? color.getName() : "normal") + type.name + modifier);
    }
}
