package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Render(RenderLayer.CUTOUT_MIPPED)
public class LeavesLightToggle extends LeavesBlock {

    public static final MapCodec<LeavesWillow> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(
            propertiesCodec()
    ).apply(i, LeavesWillow::new));
    public MapCodec<LeavesWillow> codec() {
        return CODEC;
    }


    public static final BooleanProperty LIGHT_PASS_THRU = BooleanProperty.create("light_pass_thru");
    private final ItemLike sapling;

    // Codec-driven constructor.
    public LeavesLightToggle(Properties properties) {
        super(0.01F, properties);
        this.sapling = null;
    }

    public LeavesLightToggle(Props props) {
        super(0.01f, props.toSettings());
        this.sapling = props.get("sapling", ItemLike.class);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_PASS_THRU, false).setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(WATERLOGGED, false));
    }

    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(LIGHT_PASS_THRU)) {
            return BlockVoxelShapes.cubePartialShape.get(0);
        } else {
            return Shapes.block();
        }
    }

    @Override
    protected int getLightDampening(BlockState state) {
        if (state.getValue(LIGHT_PASS_THRU)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        ColorParticleOption particle = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, level.getClientLeafTintColor(pos));
        ParticleUtils.spawnParticleBelow(level, pos, random, particle);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateList) {
        stateList.add(LIGHT_PASS_THRU, DISTANCE, PERSISTENT, WATERLOGGED);
    }

}
