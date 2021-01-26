package net.kyrptonaught.upgradedshulker.client;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.blockentity.UpgradedShulkerBlockEntity;
import net.kyrptonaught.upgradedshulker.screen.UpgradedShulkerScreen;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class UpgradedShulkerClientMod implements ClientModInitializer {
    public static final HashMap<String, Identifier> SHULKER_TEXTURES = new HashMap<>();
    //public static final Identifier SHULKER_BOXES_ATLAS_TEXTURE = new Identifier(UpgradedShulkerMod.MOD_ID,"textures/atlas/shulker_boxes.png");

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(UpgradedShulkerMod.LINKED_SCREEN_HANDLER_TYPE, UpgradedShulkerScreen::new);
        for (ShulkerUpgrades.MATERIAL type : ShulkerUpgrades.MATERIAL.values()) {
            registerShulkerWith(null, type);
            BlockEntityRendererRegistry.INSTANCE.register(ShulkersRegistry.UPGRADEDSHULKERENTITYTYPE, UpgradedShulkerBoxRenderer::new);
            for (DyeColor color : DyeColor.values())
                registerShulkerWith(color, type);
        }
        /*
        LANGUAGES.asMap().forEach((s, c) -> {
            JLang lang = JLang.lang();
            for (Consumer<JLang> consumer : c) {
                consumer.accept(lang);
            }
            UpgradedShulkerMod.RESOURCE_PACK.addLang(new Identifier(UpgradedShulkerMod.MOD_ID, s), lang);
        });
        UpgradedShulkerMod.RESOURCE_PACK.dump();

         */
    }

    private void registerShulkerWith(DyeColor color, ShulkerUpgrades.MATERIAL type) {
        String colorName = color != null ? color.getName() : "normal";
        registerTexture(color, type, "");
        for (ShulkerUpgrades.UPGRADES upgrade : type.getApplicableUpgrades())
            registerTexture(color, type, "_" + upgrade.name);
        BuiltinItemRendererRegistry.INSTANCE.register(ShulkersRegistry.getShulkerBlock(type, color), (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            UpgradedShulkerBlockEntity be = new UpgradedShulkerBlockEntity(color, type);
            CompoundTag tag = stack.getSubTag(ShulkerUpgrades.KEY);
            if (tag != null) be.appendUpgrades(tag);
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(be, matrices, vertexConsumers, light, overlay);
        });
        //UpgradedShulkerMod.RESOURCE_PACK.addBlockState(JState.state(JState.variant(JState.model("minecraft:block/" + (colorName.equals("normal") ? "" : colorName.toLowerCase() + "_") + "shulker_box"))), new Identifier("us", colorName + type.name + "shulker"));
        // UpgradedShulkerMod.RESOURCE_PACK.addModel(JModel.model("minecraft:item/template_shulker_box"), new Identifier("us", "item/" + colorName + type.name + "shulker"));
        // LANGUAGES.put("en_us", jLang -> jLang.block(ShulkersRegistry.getShulkerBlock(type, color), WordUtils.capitalize(type.name + (colorName.equals("normal") ? "" : " " + colorName.toLowerCase()) + " Shulker Box")));

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
