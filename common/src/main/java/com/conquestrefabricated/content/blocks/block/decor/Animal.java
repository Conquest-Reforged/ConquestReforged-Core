package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.tileentity.AnimalTileEntity;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class Animal extends BaseEntityBlock {

    public static final MapCodec<Animal> CODEC = simpleCodec(Animal::new);
    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);

    public Animal(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Animal> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Random rand = new Random();
        int toggleState = rand.nextInt(4) + 1;
        return this.defaultBlockState().setValue(TOGGLE, toggleState);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Environment(EnvType.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter blockReader, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new AnimalTileEntity(blockPos, state);
    }



    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState p_153213_, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return createTickerHelper(blockEntityType, TileEntityTypes.ANIMAL, AnimalTileEntity::particleTick);
        } else {
            return createTickerHelper(blockEntityType, TileEntityTypes.ANIMAL, AnimalTileEntity::particleTickServer);
        }
    }

    public static class Bird extends Animal {
        public static final BooleanProperty FLYING = BooleanProperty.create("flying");

        public Bird(Properties settings) {
            super(settings);
        }

        @Override
        public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
            return Interactions.onUseToggleItem(player, world, pos, state, FLYING);
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(TOGGLE, FLYING);
        }
    }
}
