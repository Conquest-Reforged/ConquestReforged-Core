package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.content.blocks.util.PlantSupport;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;
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

    /**
     * Flat pick box shared by all bushes. When the plant is lowered onto a layer/slab its model is
     * drawn shifted down by the offset, so a full-height box would float a block above the layer.
     */
    private static final VoxelShape LOWERED_SHAPE = com.conquestrefabricated.content.blocks.util.PlantHitboxes.FLAT_SHAPE;

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof Player player) {
                // Any held item shows the hitbox; only players who can't build (adventure) pass through
                if ((player.getAbilities().instabuild || player.getAbilities().mayBuild)) {
                    return plantShape(state, player, worldIn, pos, context);
                } else {
                    return Shapes.empty();
                }
            }
            return plantShape(state, null, worldIn, pos, context);
        } else {
            // Fallback for non-entity contexts that are holding an axe
            return plantShape(state, null, worldIn, pos, context);
        }
    }

    private VoxelShape plantShape(BlockState state, Player player, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        // Plants with a height toggle need to be clickable along the whole stalk for whoever can cycle them
        boolean cycler = player != null && (player.getAbilities().instabuild || player.getMainHandItem().is(CYCLING_TOOLS));
        if (cycler && hasHeightProperty(state)) {
            Vec3 o = state.getOffset(pos);
            return TOGGLE_SHAPE.move(o.x, o.y, o.z);
        }
        // Ray hits are tested against the whole segment, so a shape sitting below this cell
        // (plant lowered onto a layer) is still picked up whenever the ray passes through the cell.
        Vec3 offset = state.getOffset(pos);
        return LOWERED_SHAPE.move(offset.x, offset.y, offset.z);
    }

    private static final VoxelShape TOGGLE_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    private static boolean hasHeightProperty(BlockState state) {
        for (var property : state.getProperties()) {
            if (property.getName().equals("height")) {
                return true;
            }
        }
        return false;
    }

    /** Destroy progress per tick when breaking a plant without a gardening tool (about 3 seconds). */
    private static final float HAND_BREAK_PROGRESS = 1.0F / 60.0F;

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        // Any item that isn't a gardening tool breaks as slowly as an empty hand
        if (player.getMainHandItem().is(GARDENING_TOOLS)) {
            return 1.0F;
        }
        return HAND_BREAK_PROGRESS;
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

        if ((PlantSupport.isSpecial(blockStateDown) || blockStateDown.hasProperty(Layer.LAYERS)) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return super.getStateForPlacement(context).setValue(LAYERS, PlantSupport.layers(blockStateDown));
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

        if ((PlantSupport.isSpecial(blockStateDown) || blockStateDown.hasProperty(Layer.LAYERS)) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return result.setValue(LAYERS, PlantSupport.layers(blockStateDown));
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
