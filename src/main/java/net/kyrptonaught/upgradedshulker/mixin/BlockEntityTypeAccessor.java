package net.kyrptonaught.upgradedshulker.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntity.class)
public interface BlockEntityTypeAccessor {

    @Accessor("type")
    void settype(BlockEntityType<?> type);
}
