package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.effects.Effects;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.block.properties.Waterloggable;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.api.tags.ModTags.GARDENING_TOOLS;
import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Render(RenderLayer.CUTOUT)
public class Bush extends AbstractBush implements Waterloggable {

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    private final int slowness;

    public Bush(Props properties) {
        super(properties
                .customOffsetType(CustomOffsetType.PLANT_XYZ)
                .dynamicBounds(true)
                .toSettings()
        );
        this.slowness = properties.getOrDefault("slowness", Integer.class, 0);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LAYERS, 8)
                .setValue(WATERLOGGED, false)
                .setValue(OFFSET_TOGGLE, false));
    }

    // Secondary constructor for codec reconstruction
    public Bush(BlockBehaviour.Properties settings, int slowness) {
        super(settings.dynamicShape());
        this.slowness = slowness;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LAYERS, 8)
                .setValue(WATERLOGGED, false)
                .setValue(OFFSET_TOGGLE, false));
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
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity && ConquestConfig.INSTANCE.plantSlowness.get()) {
            if (slowness > 0) {
                Holder<MobEffect> slownessKey = level.registryAccess()
                        .lookupOrThrow(Registries.MOB_EFFECT)
                        .wrapAsHolder(Effects.CUSTOM_SLOWNESS);



                if (livingEntity instanceof Player) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, this.slowness, false, false));
                } else if (slowness > 1) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, this.slowness / 2, false, false));
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

        if (blockStateDown.hasProperty(Layer.LAYERS) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return super.getStateForPlacement(context).setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return super.getStateForPlacement(context).setValue(LAYERS, 8);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == Direction.DOWN) {
            if (neighbourState.isAir()) {
                if (ConquestConfig.INSTANCE.plantBreaking.get()) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }

        BlockPos down = currentPos.below();
        BlockState blockStateDown = level.getBlockState(down);

        BlockState result = super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);

        // Guard: if the parent already turned this into a different block (e.g. AIR because
        // support was lost), don't try to set LAYERS on it — it won't have that property.
        if (!result.hasProperty(LAYERS)) {
            return result;
        }

        if (blockStateDown.hasProperty(Layer.LAYERS) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return result.setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return result.setValue(LAYERS, 8);
        }
    }


    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Waterloggable.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LAYERS, OFFSET_TOGGLE);
    }
}
