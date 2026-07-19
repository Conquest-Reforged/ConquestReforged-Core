package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.content.effects.Effects;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.api.tags.ModTags.GARDENING_TOOLS;
import static com.conquestrefabricated.api.tags.ModTags.PLANT_SLOWNESS;
import static com.conquestrefabricated.content.blocks.block.plants.PlantsTall2.TWOTALL;

public class PlantsTall0to3 extends Bush {

    public static final VoxelShape THREETALL = Shapes.box(0, 0, 0, 16, 48, 16);
    private static final IntegerProperty HEIGHT = IntegerProperty.create("height", 0, 3);

    public PlantsTall0to3(Props properties) {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == Direction.DOWN) {
            // Check if the neighbor block is air (meaning it was broken)
            if (neighbourState.isAir()) {
                if (ConquestConfig.INSTANCE.plantBreaking.get()) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }

        return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        // If the context is a player and either they're in creative mode or holding an iron axe
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof Player player) {
                // Check if player is in creative mode or holding iron axe
                if ((player.getAbilities().instabuild || player.getMainHandItem().is(GARDENING_TOOLS) && player.getAbilities().mayBuild)) {
                    return super.getShape(state, worldIn, pos, context);
                } else {
                    return Shapes.empty();
                }
            }
            return super.getShape(state, worldIn, pos, context);
        } else {
            // Fallback for non-entity contexts that are holding an axe
            return super.getShape(state, worldIn, pos, context);
        }
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        if (state.getValue(HEIGHT) == 1) {
            return Shapes.block();
        } else if (state.getValue(HEIGHT) == 2) {
            return TWOTALL;
        } else if (state.getValue(HEIGHT) == 3) {
            return THREETALL;
        }
        return super.getInteractionShape(state, worldIn, pos);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {

        if (entity instanceof LivingEntity livingEntity && state.is(PLANT_SLOWNESS)) {
            Holder<MobEffect> slownessKey = level.registryAccess()
                    .lookupOrThrow(Registries.MOB_EFFECT)
                    .wrapAsHolder(Effects.CUSTOM_SLOWNESS);

            if (livingEntity instanceof Player) {
                if (state.getValue(HEIGHT) == 1 ) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, 9, false, false));
                } else if (state.getValue(HEIGHT) == 2) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, 12, false, false));
                } else if (state.getValue(HEIGHT) == 3) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, 12, false, false));
                }
            } else {
                if (state.getValue(HEIGHT) == 1 ) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, 4, false, false));
                } else if (state.getValue(HEIGHT) == 2) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, 9, false, false));
                } else if (state.getValue(HEIGHT) == 3) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, 9, false, false));
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos down = blockpos.below();
        BlockState blockStateDown = iblockreader.getBlockState(down);
        int layerBlockState = 8;

        if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS) || blockStateDown.hasProperty(LAYERS)) {
            layerBlockState = blockStateDown.getValue(LAYERS);
        }

        return super.getStateForPlacement(context).setValue(LAYERS, layerBlockState).setValue(HEIGHT, 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, HEIGHT);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HEIGHT, LAYERS, WATERLOGGED, OFFSET_TOGGLE);
    }
}
