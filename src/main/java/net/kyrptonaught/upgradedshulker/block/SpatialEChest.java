package net.kyrptonaught.upgradedshulker.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.upgradedshulker.UpgradedShulkerMod;
import net.kyrptonaught.upgradedshulker.block.blockentity.SpatialEChestBlockEntity;
import net.kyrptonaught.upgradedshulker.client.UpgradedShulkerClientMod;
import net.kyrptonaught.upgradedshulker.inv.SpatialEChestInventory;
import net.kyrptonaught.upgradedshulker.util.ContainerNames;
import net.kyrptonaught.upgradedshulker.util.SpatialInvStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Random;

public class SpatialEChest extends EnderChestBlock {
    public static BlockEntityType<SpatialEChestBlockEntity> blockEntity;

    public SpatialEChest(Settings settings) {
        super(settings);
        Registry.register(Registry.BLOCK, new Identifier("us", "spatialchest"), this);
        blockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, UpgradedShulkerMod.MOD_ID + ":spatialchest", BlockEntityType.Builder.create(SpatialEChestBlockEntity::new, this).build(null));
        Item.Settings itemSettings = new Item.Settings().group(UpgradedShulkerMod.GROUP);
        Registry.register(Registry.ITEM, new Identifier("us", "spatialchest"), new BlockItem(this, itemSettings));
    }

    public BlockEntity createBlockEntity(BlockView world) {
        return new SpatialEChestBlockEntity();
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 3; ++i) {
            int j = random.nextInt(2) * 2 - 1;
            int k = random.nextInt(2) * 2 - 1;
            double d = (double) pos.getX() + 0.5D + 0.25D * (double) j;
            double e = (float) pos.getY() + random.nextFloat();
            double f = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
            double g = random.nextFloat() * (float) j;
            double h = ((double) random.nextFloat() - 0.5D) * 0.125D;
            double l = random.nextFloat() * (float) k;
            world.addParticle(UpgradedShulkerClientMod.GREENPARTICLE, d, e, f, g, h, l);
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (explosion.getDamageSource() instanceof EntityDamageSource && explosion.getDamageSource().getSource() instanceof CreeperEntity)
            if (((CreeperEntity) explosion.getDamageSource().getSource()).shouldRenderOverlay()) {
                Block.dropStack(world, pos, new ItemStack(UpgradedShulkerMod.riftEChest));
            }
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        if (explosion.getDamageSource() instanceof EntityDamageSource && explosion.getDamageSource().getSource() instanceof CreeperEntity)
            if (((CreeperEntity) explosion.getDamageSource().getSource()).shouldRenderOverlay())
                return false;
        return super.shouldDropItemsOnExplosion(explosion);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SpatialEChestBlockEntity) {
                SpatialEChestInventory inv = new SpatialEChestInventory(player, ((SpatialInvStorage) player).getSpatialInv());
                inv.setActiveBlockEntity((SpatialEChestBlockEntity) entity);
                player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
                    return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inv);
                }, ContainerNames.SPATIAL_CHEST));

                return ActionResult.CONSUME;
            }
        }
        return ActionResult.SUCCESS;
    }
}
