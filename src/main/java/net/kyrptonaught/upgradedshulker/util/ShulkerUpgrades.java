package net.kyrptonaught.upgradedshulker.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

public class ShulkerUpgrades {
    public static String KEY = "upgradedshulker";

    public enum UPGRADES {
        HOPPER("auto", Items.HOPPER), VOID("void", Items.LAVA_BUCKET);

        public String name;
        public Item craftingItem;

        UPGRADES(String name, Item craftingItem) {
            this.name = name;
            this.craftingItem = craftingItem;
        }

        public ItemStack putOnStack(ItemStack stack) {
            stack.getOrCreateSubTag(KEY).putBoolean(name, true);
            return stack;
        }

        public boolean isOnStack(ItemStack stack) {
            CompoundTag upgradeTag = stack.getSubTag(KEY);
            if (upgradeTag != null) {
                return upgradeTag.getBoolean(name);
            }
            return false;
        }

        public static boolean hasUpgrades(ItemStack stack) {
            return stack.getSubTag(KEY) != null;
        }
    }

    public enum MATERIAL {
        IRON("iron", 54), GOLD("gold", 81), DIAMOND("diamond", 108), NETHERITE("netherite", 108);
        public String name;
        public int size;

        MATERIAL(String name, int size) {
            this.name = name;
            this.size = size;
        }

        public UPGRADES[] getApplicableUpgrades() {
            /*
            switch (this) {
                case IRON:
                case GOLD:
                 return new UPGRADES[]{UPGRADES.HOPPER};
                case DIAMOND:
                case NETHERITE:
                    return new UPGRADES[]{UPGRADES.HOPPER, UPGRADES.VOID};
            }
            return new UPGRADES[0];
         */
            return new UPGRADES[]{UPGRADES.HOPPER, UPGRADES.VOID};

        }
    }
}
