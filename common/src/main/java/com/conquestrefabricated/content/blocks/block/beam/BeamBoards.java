package com.conquestrefabricated.content.blocks.block.beam;

import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.DirectionalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Assets(
        state = @State(name = "%s_beam_board", template = "parent_beam_board"),
        item = @Model(name = "item/%s_corner_slab", parent = "block/%s_slab_corner", template = "item/parent_slab_corner"),
        block = {
                @Model(name = "block/%s_slab_corner", template = "block/parent_slab_corner_beam"),
                @Model(name = "block/%s_slab_corner_top", template = "block/parent_slab_corner_top_beam"),
        }
)
public class BeamBoards extends DirectionalShape {

    public static final VoxelShape FLAT_N = Block.box(0, 0, 10, 16, 2.5, 16);
    public static final VoxelShape FLAT_E = Block.box(0, 0, 0, 6, 2.5, 16);
    public static final VoxelShape FLAT_S = Block.box(0, 0, 0, 16, 2.5, 6);
    public static final VoxelShape FLAT_W = Block.box(10, 0, 0, 16, 2.5, 16);
    public static final VoxelShape FLAT_N_2 = Block.box(0, 0, 8, 16, 2.5, 16);
    public static final VoxelShape FLAT_E_2 = Block.box(0, 0, 0, 8, 2.5, 16);
    public static final VoxelShape FLAT_S_2 = Block.box(0, 0, 0, 16, 2.5, 8);
    public static final VoxelShape FLAT_W_2 = Block.box(8, 0, 0, 16, 2.5, 16);
    public static final VoxelShape FLAT_N_3 = Block.box(0, 0, 4, 16, 2.5, 16);
    public static final VoxelShape FLAT_E_3 = Block.box(0, 0, 0, 12, 2.5, 16);
    public static final VoxelShape FLAT_S_3 = Block.box(0, 0, 0, 16, 2.5, 12);
    public static final VoxelShape FLAT_W_3 = Block.box(4, 0, 0, 16, 2.5, 16);


    public static final VoxelShape FLAT_N_TOP = Block.box(0, 13.5, 10, 16, 16, 16);
    public static final VoxelShape FLAT_E_TOP = Block.box(0, 13.5, 0, 6, 16, 16);
    public static final VoxelShape FLAT_S_TOP = Block.box(0, 13.5, 0, 16, 16, 6);
    public static final VoxelShape FLAT_W_TOP = Block.box(10, 13.5, 0, 16, 16, 16);
    public static final VoxelShape FLAT_N_TOP_2 = Block.box(0, 13.5, 8, 16, 16, 16);
    public static final VoxelShape FLAT_E_TOP_2 = Block.box(0, 13.5, 0, 8, 16, 16);
    public static final VoxelShape FLAT_S_TOP_2 = Block.box(0, 13.5, 0, 16, 16, 8);
    public static final VoxelShape FLAT_W_TOP_2 = Block.box(8, 13.5, 0, 16, 16, 16);
    public static final VoxelShape FLAT_N_TOP_3 = Block.box(0, 13.5, 4, 16, 16, 16);
    public static final VoxelShape FLAT_E_TOP_3 = Block.box(0, 13.5, 0, 12, 16, 16);
    public static final VoxelShape FLAT_S_TOP_3 = Block.box(0, 13.5, 0, 16, 16, 12);
    public static final VoxelShape FLAT_W_TOP_3 = Block.box(4, 13.5, 0, 16, 16, 16);


    public static final VoxelShape SIDE_TOGGLE_1_N = Block.box(0, 0, 13.5, 16, 6, 16);
    public static final VoxelShape SIDE_TOGGLE_1_E = Block.box(0, 0, 0, 2.5, 6, 16);
    public static final VoxelShape SIDE_TOGGLE_1_S = Block.box(0, 0, 0, 16, 6, 2.5);
    public static final VoxelShape SIDE_TOGGLE_1_W = Block.box(13.5, 0, 0, 16, 6, 16);
    public static final VoxelShape SIDE_TOGGLE_1_N_2 = Block.box(0, 0, 13.5, 16, 8, 16);
    public static final VoxelShape SIDE_TOGGLE_1_E_2 = Block.box(0, 0, 0, 2.5, 8, 16);
    public static final VoxelShape SIDE_TOGGLE_1_S_2 = Block.box(0, 0, 0, 16, 8, 2.5);
    public static final VoxelShape SIDE_TOGGLE_1_W_2 = Block.box(13.5, 0, 0, 16, 8, 16);
    public static final VoxelShape SIDE_TOGGLE_1_N_3 = Block.box(0, 0, 13.5, 16, 12, 16);
    public static final VoxelShape SIDE_TOGGLE_1_E_3 = Block.box(0, 0, 0, 2.5, 12, 16);
    public static final VoxelShape SIDE_TOGGLE_1_S_3 = Block.box(0, 0, 0, 16, 12, 2.5);
    public static final VoxelShape SIDE_TOGGLE_1_W_3 = Block.box(13.5, 0, 0, 16, 12, 16);

    public static final VoxelShape SIDE_TOGGLE_2_N = Block.box(10, 0, 13.5, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_2_E = Block.box(0, 0, 10, 2.5, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_2_S = Block.box(0, 0, 0, 6, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_2_W = Block.box(13.5, 0, 0, 16, 16, 6);
    public static final VoxelShape SIDE_TOGGLE_2_N_2 = Block.box(8, 0, 13.5, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_2_E_2 = Block.box(0, 0, 8, 2.5, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_2_S_2 = Block.box(0, 0, 0, 8, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_2_W_2 = Block.box(13.5, 0, 0, 16, 16, 8);
    public static final VoxelShape SIDE_TOGGLE_2_N_3 = Block.box(4, 0, 13.5, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_2_E_3 = Block.box(0, 0, 4, 2.5, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_2_S_3 = Block.box(0, 0, 0, 12, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_2_W_3 = Block.box(13.5, 0, 0, 16, 16, 12);


    public static final VoxelShape SIDE_TOGGLE_3_N = Block.box(0, 10, 13.5, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_E = Block.box(0, 10, 0, 2.5, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_S = Block.box(0, 10, 0, 16, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_3_W = Block.box(13.5, 10, 0, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_N_2 = Block.box(0, 8, 13.5, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_E_2 = Block.box(0, 8, 0, 2.5, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_S_2 = Block.box(0, 8, 0, 16, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_3_W_2 = Block.box(13.5, 8, 0, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_N_3 = Block.box(0, 4, 13.5, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_E_3 = Block.box(0, 4, 0, 2.5, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_3_S_3 = Block.box(0, 4, 0, 16, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_3_W_3 = Block.box(13.5, 4, 0, 16, 16, 16);


    public static final VoxelShape SIDE_TOGGLE_4_N = Block.box(0, 0, 13.5, 6, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_4_E = Block.box(0, 0, 0, 2.5, 16, 6);
    public static final VoxelShape SIDE_TOGGLE_4_S = Block.box(10, 0, 0, 16, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_4_W = Block.box(13.5, 0, 10, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_4_N_2 = Block.box(0, 0, 13.5, 8, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_4_E_2 = Block.box(0, 0, 0, 2.5, 16, 8);
    public static final VoxelShape SIDE_TOGGLE_4_S_2 = Block.box(8, 0, 0, 16, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_4_W_2 = Block.box(13.5, 0, 8, 16, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_4_N_3 = Block.box(0, 0, 13.5, 12, 16, 16);
    public static final VoxelShape SIDE_TOGGLE_4_E_3 = Block.box(0, 0, 0, 2.5, 16, 12);
    public static final VoxelShape SIDE_TOGGLE_4_S_3 = Block.box(4, 0, 0, 16, 16, 2.5);
    public static final VoxelShape SIDE_TOGGLE_4_W_3 = Block.box(13.5, 0, 4, 16, 16, 16);


    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);
   public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 3);

   public BeamBoards(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        } else if (player.isHolding(this.asItem())) {
            level.setBlock(blockPos, state.cycle(LAYERS), 3);
            return InteractionResult.SUCCESS;
        } else {
            return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, LAYERS);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
       switch (state.getValue(DIRECTION))  {
           case UP:
               switch (state.getValue(TOGGLE)) {
                   case 1:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_N;
                           case 2:
                               return FLAT_N_2;
                           case 3:
                               return FLAT_N_3;
                       }
                   case 2:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_E;
                           case 2:
                               return FLAT_E_2;
                           case 3:
                               return FLAT_E_3;
                       }
                   case 3:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_S;
                           case 2:
                               return FLAT_S_2;
                           case 3:
                               return FLAT_S_3;
                       }
                   case 4:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_W;
                           case 2:
                               return FLAT_W_2;
                           case 3:
                               return FLAT_W_3;
                       }
               }
           case DOWN:
               switch (state.getValue(TOGGLE)) {
                   case 1:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_N_TOP;
                           case 2:
                               return FLAT_N_TOP_2;
                           case 3:
                               return FLAT_N_TOP_3;
                       }
                   case 2:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_E_TOP;
                           case 2:
                               return FLAT_E_TOP_2;
                           case 3:
                               return FLAT_E_TOP_3;
                       }
                   case 3:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_S_TOP;
                           case 2:
                               return FLAT_S_TOP_2;
                           case 3:
                               return FLAT_S_TOP_3;
                       }
                   case 4:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return FLAT_W_TOP;
                           case 2:
                               return FLAT_W_TOP_2;
                           case 3:
                               return FLAT_W_TOP_3;
                       }
               }
           case NORTH:
               switch (state.getValue(TOGGLE)) {
                   case 1:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_1_N;
                           case 2:
                               return SIDE_TOGGLE_1_N_2;
                           case 3:
                               return SIDE_TOGGLE_1_N_3;
                       }
                   case 2:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_2_N;
                           case 2:
                               return SIDE_TOGGLE_2_N_2;
                           case 3:
                               return SIDE_TOGGLE_2_N_3;
                       }
                   case 3:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_3_N;
                           case 2:
                               return SIDE_TOGGLE_3_N_2;
                           case 3:
                               return SIDE_TOGGLE_3_N_3;
                       }
                   case 4:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_4_N;
                           case 2:
                               return SIDE_TOGGLE_4_N_2;
                           case 3:
                               return SIDE_TOGGLE_4_N_3;
                       }
               }
           case EAST:
               switch (state.getValue(TOGGLE)) {
                   case 1:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_1_E;
                           case 2:
                               return SIDE_TOGGLE_1_E_2;
                           case 3:
                               return SIDE_TOGGLE_1_E_3;
                       }
                   case 2:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_2_E;
                           case 2:
                               return SIDE_TOGGLE_2_E_2;
                           case 3:
                               return SIDE_TOGGLE_2_E_3;
                       }
                   case 3:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_3_E;
                           case 2:
                               return SIDE_TOGGLE_3_E_2;
                           case 3:
                               return SIDE_TOGGLE_3_E_3;
                       }
                   case 4:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_4_E;
                           case 2:
                               return SIDE_TOGGLE_4_E_2;
                           case 3:
                               return SIDE_TOGGLE_4_E_3;
                       }
               }
           case SOUTH:
               switch (state.getValue(TOGGLE)) {
                   case 1:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_1_S;
                           case 2:
                               return SIDE_TOGGLE_1_S_2;
                           case 3:
                               return SIDE_TOGGLE_1_S_3;
                       }
                   case 2:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_2_S;
                           case 2:
                               return SIDE_TOGGLE_2_S_2;
                           case 3:
                               return SIDE_TOGGLE_2_S_3;
                       }
                   case 3:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_3_S;
                           case 2:
                               return SIDE_TOGGLE_3_S_2;
                           case 3:
                               return SIDE_TOGGLE_3_S_3;
                       }
                   case 4:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_4_S;
                           case 2:
                               return SIDE_TOGGLE_4_S_2;
                           case 3:
                               return SIDE_TOGGLE_4_S_3;
                       }
               }
           case WEST:
               switch (state.getValue(TOGGLE)) {
                   case 1:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_1_W;
                           case 2:
                               return SIDE_TOGGLE_1_W_2;
                           case 3:
                               return SIDE_TOGGLE_1_W_3;
                       }
                   case 2:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_2_W;
                           case 2:
                               return SIDE_TOGGLE_2_W_2;
                           case 3:
                               return SIDE_TOGGLE_2_W_3;
                       }
                   case 3:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_3_W;
                           case 2:
                               return SIDE_TOGGLE_3_W_2;
                           case 3:
                               return SIDE_TOGGLE_3_W_3;
                       }
                   case 4:
                       switch (state.getValue(LAYERS)) {
                           case 1:
                               return SIDE_TOGGLE_4_W;
                           case 2:
                               return SIDE_TOGGLE_4_W_2;
                           case 3:
                               return SIDE_TOGGLE_4_W_3;
                       }
               }
       }
       return Shapes.block();
    }
}