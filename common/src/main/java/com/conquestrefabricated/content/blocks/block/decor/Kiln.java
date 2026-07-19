package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.tileentity.KilnTileEntity;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@Render(RenderLayer.CUTOUT)
public class Kiln extends HorizontalDirectional.OffsetXYZ.Toggle2 implements EntityBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public Kiln(Props properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getAbilities().mayBuild) {
           return InteractionResult.FAIL;
        } else {
            if (stack.getItem() == Items.FLINT_AND_STEEL) {
                world.setBlock(pos, state.setValue(LIT, true), 3);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (blockState.getValue(LIT)) {
            if (randomSource.nextInt(10) == 0) {
                level.playLocalSound((double)blockPos.getX() + (double)0.5F, (double)blockPos.getY() + (double)0.5F, (double)blockPos.getZ() + (double)0.5F, net.minecraft.sounds.SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + randomSource.nextFloat(), randomSource.nextFloat() * 0.7F + 0.6F, false);
            }

            if (randomSource.nextInt(5) == 0) {
                for(int i = 0; i < randomSource.nextInt(1) + 1; ++i) {
                    level.addParticle(ParticleTypes.LAVA, (double)blockPos.getX() + (double)0.5F, (double)blockPos.getY() + (double)0.5F, (double)blockPos.getZ() + (double)0.5F, (double)(randomSource.nextFloat() / 2.0F), 5.0E-5, (double)(randomSource.nextFloat() / 2.0F));
                }
            }

        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        KilnTileEntity entity = new KilnTileEntity(blockPos, blockState);
        return entity;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return state.getValue(LIT) ? checkType(blockEntityType, TileEntityTypes.KILN, KilnTileEntity::particleTick) : null;
        } else {
            return null;
        }
    }

    @Nullable
    protected static <E extends BlockEntity, T extends BlockEntity> BlockEntityTicker<T> checkType(BlockEntityType<T> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<T>) ticker : null;
    }


    public static void spawnSmokeParticle(Level level, BlockPos blockPos) {
        RandomSource randomSource = level.getRandom();
        SimpleParticleType defaultParticleType = ParticleTypes.CAMPFIRE_COSY_SMOKE;
        level.addAlwaysVisibleParticle(defaultParticleType, true, (double)blockPos.getX() + (double)0.5F + randomSource.nextDouble() / (double)3.0F * (double)(randomSource.nextBoolean() ? 1 : -1), (double)blockPos.getY() + 0.75 + randomSource.nextDouble() + randomSource.nextDouble(), (double)blockPos.getZ() + (double)0.5F + randomSource.nextDouble() / (double)3.0F * (double)(randomSource.nextBoolean() ? 1 : -1), (double)0.0F, 0.07, (double)0.0F);
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        super.addProperties(builder);
        builder.add(LIT);
    }
}
