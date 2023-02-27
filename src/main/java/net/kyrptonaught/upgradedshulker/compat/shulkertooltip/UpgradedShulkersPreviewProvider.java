package net.kyrptonaught.upgradedshulker.compat.shulkertooltip;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import net.kyrptonaught.upgradedshulker.block.UpgradedShulkerBlock;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

public class UpgradedShulkersPreviewProvider extends BlockEntityPreviewProvider {
    private final static float[] DEFAULT_COLOR = new float[]{0.592f, 0.403f, 0.592f};
    protected final int maxRowSize;

    public UpgradedShulkersPreviewProvider(final ShulkerUpgrades.MATERIAL material) {
        super(material.size, true);
        this.maxRowSize = material.size <= 81 ? 9 : material.size / 9;
    }

    @Override
    public int getMaxRowSize(final PreviewContext context) {
        return this.maxRowSize;
    }

    @Override
    public ColorKey getWindowColorKey(PreviewContext context) {
        final DyeColor dye = ((UpgradedShulkerBlock) Block.getBlockFromItem(context.stack().getItem())).getColor();
        float[] result = DEFAULT_COLOR;
        if (dye != null) {
            final float[] components = dye.getColorComponents();
            result = new float[]{Math.max(0.15f, components[0]), Math.max(0.15f, components[1]),
                    Math.max(0.15f, components[2])};
        }
        return ColorKey.ofRgb(result);
    }
}
