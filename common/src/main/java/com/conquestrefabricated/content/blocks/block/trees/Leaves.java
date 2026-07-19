package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MangroveLeavesBlock;

@Render(RenderLayer.CUTOUT_MIPPED)
public class Leaves extends LeavesBlock {

    public static final MapCodec<Leaves> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(
            propertiesCodec()
    ).apply(i, Leaves::new));
    public MapCodec<Leaves> codec() {
        return CODEC;
    }


    private final ItemLike sapling;

    // Codec-driven constructor.
    public Leaves(Properties properties) {
        super(0.01F, properties);
        this.sapling = null;
    }

    public Leaves(Props props) {
        super(0.01F, props.toSettings());
        this.sapling = props.get("sapling", ItemLike.class);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        ColorParticleOption particle = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, level.getClientLeafTintColor(pos));
        ParticleUtils.spawnParticleBelow(level, pos, random, particle);
    }
}
