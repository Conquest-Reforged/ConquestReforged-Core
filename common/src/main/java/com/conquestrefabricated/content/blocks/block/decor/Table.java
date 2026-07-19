package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.block.WallOld;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Table extends CrossCollisionBlock {

    public static final MapCodec<WallOld> CODEC = simpleCodec(WallOld::new);

    public Table(Properties properties) {
        super(0.0F, 0.0F, 16.0F, 16.0F, 16.0F, properties);
    }

    @Override
    protected MapCodec<? extends CrossCollisionBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return directionToNeighbour.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), this.canTableConnectTo(level, currentPos, directionToNeighbour)) : super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    private boolean canTableConnectTo(BlockGetter reader, BlockPos pos, Direction facing) {
        BlockPos offset = pos.relative(facing);
        BlockState other = reader.getBlockState(offset);
        return canBeConnectedTo(other, reader, offset, facing.getOpposite()) || canBeConnectedTo(other, reader, pos, facing);
    }

    private boolean canBeConnectedTo(BlockState state, BlockGetter reader, BlockPos pos, Direction facing) {
        BlockState other = reader.getBlockState(pos.relative(facing));
        return this.attachesTo(other);
    }

    private boolean attachesTo(BlockState state) {
        Block block = state.getBlock();
        return block == this;
    }
}
