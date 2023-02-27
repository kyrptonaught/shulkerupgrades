package net.kyrptonaught.upgradedshulker.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class UpgradedShulkerScreen extends HandledScreen<UpgradedShulkerScreenHandler> {
    private static final Identifier GOLD_TEXTURE = new Identifier("us", "textures/container/gold_container.png");
    private static final Identifier IRON_TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private static final Identifier DIAMOND_TEXTURE = new Identifier("us", "textures/container/diamond_container.png");
    private final Identifier selectedTexture;

    public UpgradedShulkerScreen(UpgradedShulkerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;

        int rows = handler.getRows();
        int collums = handler.getColumns();
        //this.backgroundHeight = 276;
        this.backgroundWidth = 16 + collums * 18;
        this.backgroundHeight = 114 + rows * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;

        if (handler.getInventory().size() == ShulkerUpgrades.MATERIAL.GOLD.size)
            selectedTexture = GOLD_TEXTURE;
        else if (handler.getInventory().size() == ShulkerUpgrades.MATERIAL.DIAMOND.size)
            selectedTexture = DIAMOND_TEXTURE;
        else
            selectedTexture = IRON_TEXTURE;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.selectedTexture);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, Math.max(256, backgroundHeight));
    }
}
