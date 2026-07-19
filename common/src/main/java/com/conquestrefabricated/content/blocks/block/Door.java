package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@Render(RenderLayer.CUTOUT)
public class Door extends DoorBlock {

    private BlockSetType blockSetType;
    public Door(Props properties, BlockSetType type) {
        super(type, properties.tag(BlockTags.DOORS).toSettings());
        this.blockSetType = type;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        state = state.cycle(OPEN);
        level.setBlock(blockPos, state, 10);
        this.playSound(player, level, blockPos, (Boolean)state.getValue(OPEN));
        return InteractionResult.SUCCESS;
    }

    private void playSound(@Nullable Entity entity, Level world, BlockPos blockPos, boolean bl) {
        world.playSound(entity, blockPos, bl ? this.blockSetType.doorOpen() : this.blockSetType.doorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        BlockState stateDown = reader.getBlockState(pos.below());
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return true;
        } else {
            return stateDown.getBlock() == this;
        }
    }
}
