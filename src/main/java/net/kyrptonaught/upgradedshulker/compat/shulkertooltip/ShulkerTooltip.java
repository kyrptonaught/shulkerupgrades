package net.kyrptonaught.upgradedshulker.compat.shulkertooltip;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.util.ShulkerUpgrades;
import net.kyrptonaught.upgradedshulker.util.ShulkersRegistry;
import net.kyrptonaught.upgradedshulker.util.UpgradedShulker;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShulkerTooltip implements ShulkerBoxTooltipApi {



    public static final HashMap<ShulkerUpgrades.MATERIAL, Item[]> UPGRADED_SHULKER_BOX_ITEMS_MAP;
    static {
        UPGRADED_SHULKER_BOX_ITEMS_MAP = new HashMap<>();
        for (ShulkerUpgrades.MATERIAL material : ShulkerUpgrades.MATERIAL.values()) {
            List<Item> itemsList = new ArrayList<>();
            itemsList.add(ShulkersRegistry.upgradedShulkerBlocks.get(material).asItem());
            ShulkersRegistry.SHULKER_BLOCKS.get(material).values().forEach(b -> itemsList.add(b.asItem()));
            
            UPGRADED_SHULKER_BOX_ITEMS_MAP.put(material, itemsList.toArray(new Item[0]));
        }
    }

    @Override
    public void registerProviders(PreviewProviderRegistry registry) {
        for (ShulkerUpgrades.MATERIAL material : ShulkerUpgrades.MATERIAL.values()) {
            Item[] items = UPGRADED_SHULKER_BOX_ITEMS_MAP.get(material);
            registry.register(new Identifier(UpgradedShulkerMod.MOD_ID, material.name + "_upgraded_shulker_box"), new UpgradedShulkersPreviewProvider(material), items);
        }
    }
}
