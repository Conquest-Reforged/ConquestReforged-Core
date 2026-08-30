package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.effects.Effects;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Render(RenderLayer.CUTOUT_MIPPED)
public class LeavesGround extends Block implements SimpleWaterloggedBlock {

    public static final IntegerProperty LAYER = BlockStateProperties.LAYERS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public LeavesGround(Props props) {
        super(props
                .customOffsetType(CustomOffsetType.LAYER_STATE_Y)
                .dynamicBounds(true)
                .toSettings()
        );
        this.registerDefaultState((this.stateDefinition.any()).setValue(LAYER, 8).setValue(WATERLOGGED, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos down = blockpos.below();
        BlockState blockStateDown = iblockreader.getBlockState(down);

        if (blockStateDown.hasProperty(Layer.LAYERS) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return super.getStateForPlacement(context).setValue(LAYER, blockStateDown.getValue(LAYER));
        } else {
            return super.getStateForPlacement(context).setValue(LAYER, 8);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LAYER);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity && ConquestConfig.INSTANCE.plantSlowness.get()) {
            Holder<MobEffect> slowness = level.registryAccess()
                    .lookupOrThrow(Registries.MOB_EFFECT)
                    .wrapAsHolder(Effects.CUSTOM_SLOWNESS);

            if (livingEntity instanceof Player) {
                livingEntity.addEffect(new MobEffectInstance(slowness, 15, 12, false, false));
            } else {
                livingEntity.addEffect(new MobEffectInstance(slowness, 15, 6, false, false));
            }
        }
    }


    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return true;
    }
}
