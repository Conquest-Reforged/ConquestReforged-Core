package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

@Render(RenderLayer.CUTOUT_MIPPED)
public class LeavesWillow extends LeavesBlock {

    public static final MapCodec<LeavesWillow> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(
            propertiesCodec()
    ).apply(i, LeavesWillow::new));
    public MapCodec<LeavesWillow> codec() {
        return CODEC;
    }


    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);
    private final ItemLike sapling;

    // Codec-driven constructor.
    public LeavesWillow(Properties properties) {
        super(0.01F, properties);
        this.sapling = null;
    }


    public LeavesWillow(Props props) {
        super(0.01f, props.toSettings());
        this.sapling = props.get("sapling", ItemLike.class);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        ColorParticleOption particle = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, level.getClientLeafTintColor(pos));
        ParticleUtils.spawnParticleBelow(level, pos, random, particle);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateList) {
        stateList.add(TOGGLE, DISTANCE, PERSISTENT, WATERLOGGED);
    }
}
