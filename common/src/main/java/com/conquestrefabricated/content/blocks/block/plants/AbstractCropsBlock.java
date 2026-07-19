package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.content.effects.Effects;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import static com.conquestrefabricated.api.tags.ModTags.PLANT_SLOWNESS;

public abstract class AbstractCropsBlock extends CropBlock {

    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    protected final int slowness;

    protected AbstractCropsBlock(Props props) {
        super(props.toSettings());
        this.slowness = props.getOrDefault("slowness", Integer.class, 0);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 8).setValue(AGE, 0));
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity && state.is(PLANT_SLOWNESS) && ConquestConfig.INSTANCE.plantSlowness.get()) {
            int age = state.getValue(AGE);
            if (slowness > 0 && age > 1) {
                Holder<MobEffect> slownessKey = level.registryAccess()
                        .lookupOrThrow(Registries.MOB_EFFECT)
                        .wrapAsHolder(Effects.CUSTOM_SLOWNESS);

                if (livingEntity instanceof Player) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, this.slowness + Math.round(age/3), false, false));
                } else if (slowness > 1) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, Math.round(this.slowness + Math.round(age/3) / 2), false, false));
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        if (PlacementHelper.isDuringWorldGen(reader)) {
            return super.canSurvive(state, reader, pos);
        }
        return true;
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter reader, BlockPos pos) {
        if (PlacementHelper.isDuringWorldGen(reader)) {
            return super.mayPlaceOn(state, reader, pos);
        }
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos down = blockpos.below();
        BlockState blockStateDown = iblockreader.getBlockState(down);

        if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS)) {
            return super.getStateForPlacement(context).setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return super.getStateForPlacement(context).setValue(LAYERS, 8);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockPos down = currentPos.below();
        BlockState blockStateDown = level.getBlockState(down);

        if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS)) {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random).setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random).setValue(LAYERS, 8);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, LAYERS);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = getAvailableMoisture1(this, world, pos);
                if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                    world.setBlock(pos, state.setValue(AGE, i + 1), 2);
                }
            }
        }

    }

    //Override to prevent use of #withAge (uses #getDefaultState, resetting Layers state)
    @Override
    public void growCrops(Level worldIn, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        worldIn.setBlock(pos, state.setValue(AGE, i), 2);
    }

    //Copy so that we can make it work for our farmlands
    protected static float getAvailableMoisture1(Block block, BlockGetter world, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockPos = pos.below();

        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float g = 0.0F;
                BlockState blockState = world.getBlockState(blockPos.offset(i, 0, j));
                if (
                        blockState.is(Blocks.FARMLAND) ||
                                blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "directional_farmland")).get()) ||
                                blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "directional_farmland_layer")).get()) ||
                                blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "diagonally_plowed_farmland")).get()) ||
                                blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "diagonally_plowed_farmland_layer")).get())
                ) { //Line Difference btwn Mod and  Vanilla is with above condition
                    g = 1.0F;
                    if (blockState.is(Blocks.FARMLAND) && blockState.getValue(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    g /= 4.0F;
                }

                f += g;
            }
        }

        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = world.getBlockState(blockPos4).is(block) || world.getBlockState(blockPos5).is(block);
        boolean bl2 = world.getBlockState(blockPos2).is(block) || world.getBlockState(blockPos3).is(block);
        if (bl && bl2) {
            f /= 2.0F;
        } else {
            boolean bl3 = world.getBlockState(blockPos4.north()).is(block) || world.getBlockState(blockPos5.north()).is(block) || world.getBlockState(blockPos5.south()).is(block) || world.getBlockState(blockPos4.south()).is(block);
            if (bl3) {
                f /= 2.0F;
            }
        }

        return f;
    }
}
