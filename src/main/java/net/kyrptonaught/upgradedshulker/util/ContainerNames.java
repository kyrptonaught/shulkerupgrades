package net.kyrptonaught.upgradedshulker.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ContainerNames {
    public static Text SPATIAL_CHEST = new TranslatableText("upgradedshulkers.containername.spatialchest");

    public static Text getRiftChestName(Text playerName) {
        return new TranslatableText("upgradedshulkers.containername.riftchest").append(playerName);
    }
}
