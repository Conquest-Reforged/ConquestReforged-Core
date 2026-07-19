package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class CauldronBehavior {
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);

    private final List<VoxelShape> hitBox;

    public CauldronBehavior(List<VoxelShape> hitBox) {
        this.hitBox = hitBox;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        }
        return Shapes.block();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return hitBox.get(0);
    }

    public boolean calculateSlabOffset(Direction facing, Block block, BlockState state, BlockPlaceContext context) {
        if (facing == Direction.DOWN) {
            return block instanceof Slab ||
                    block instanceof SlabBlock ||
                    block instanceof Layer ||
                    block instanceof SnowLayerBlock ||
                    block instanceof SlabLessLayers ||
                    block instanceof BoardsHorizontal;
        } else if (facing != Direction.UP) {
            return block instanceof VerticalSlab && state.getValue(VerticalSlab.DIRECTION) == context.getClickedFace();
        }
        return false;
    }

    public void onEntityCollision(BlockState state, Level world, BlockPos pos, Entity entity) {
        int level = state.getValue(LEVEL);
        float waterLevel = (float) pos.getY() + (6.0F + (float) (3 * level)) / 16.0F;

        if (!world.isClientSide() && entity.isOnFire() && level > 0 && entity.getY() <= (double) waterLevel) {
            entity.clearFire();
            world.setBlockAndUpdate(pos, state.setValue(LEVEL, level - 1));
        }
    }

    public void precipitationTick(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        if (world.getRandom().nextInt(20) == 1) {
            float temperature = world.getBiome(pos).value().getBaseTemperature();
            if (temperature >= 0.15F) {
                BlockState currentState = world.getBlockState(pos);
                if (currentState.getValue(LEVEL) < 3) {
                    world.setBlock(pos, currentState.cycle(LEVEL), 2);
                }
            }
        }
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, Level world, BlockPos pos) {
        return state.getValue(LEVEL);
    }
}