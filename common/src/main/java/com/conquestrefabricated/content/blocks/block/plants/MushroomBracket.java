package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.content.blocks.block.VerticalSlab;
import com.conquestrefabricated.content.blocks.block.directional.Half;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.block.trees.LogPillar;
import com.conquestrefabricated.content.blocks.block.trees.LogVerticalQuarter;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;
import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@ItemDescription(description = "toggle_3")
public class MushroomBracket extends HorizontalDirectional {

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 1, 16);

    public MushroomBracket(Props props) {
        super(props);
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, POSITION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace().getOpposite();
        BlockGetter blockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState state = blockreader.getBlockState(blockpos.relative(facing));
        Block block = state.getBlock();

        int pos = 1;

        if (block instanceof LogPillar) {
            switch (state.getValue(LogPillar.LAYERS)) {
                case 5: {
                    pos = 2;
                    break;
                }
                case 1: {
                    pos = 3;
                    break;
                }
                case 4: {
                    pos = 4;
                    break;
                }
                case 2: {
                    pos = 5;
                    break;
                }
                case 3: {
                    pos = 6;
                    break;
                }
            }

        } else if (block instanceof LogVerticalQuarter) {
            pos = state.getValue(LogVerticalQuarter.LAYERS) + 6;
        } else if (block instanceof VerticalSlab) {
            pos = state.getValue(VerticalSlab.LAYERS) + 10;
        } else if (BuiltInRegistries.BLOCK.getKey(block).toString().contains("branch_flat")) {
            if (state.hasProperty(TYPE_UPDOWN)) {
                if (state.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.TOP) {
                    pos = 16;
                } else {
                    pos = 15;
                }
            }
        }
        return super.getStateForPlacement(context).setValue(TOGGLE, 1).setValue(POSITION, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild) {
			if (player.isShiftKeyDown()) {
				level.setBlock(blockPos, state.cycle(POSITION), 3);
				return InteractionResult.SUCCESS;
			}
            level.setBlock(blockPos, state.cycle(TOGGLE), 3);
            return InteractionResult.SUCCESS;
        }

        if (player.getMainHandItem().is(CYCLING_TOOLS)) {
            if (player.isShiftKeyDown()) {
				level.setBlock(blockPos, state.cycle(POSITION), 3);
				return InteractionResult.SUCCESS;
			}
			level.setBlock(blockPos, state.cycle(TOGGLE), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
