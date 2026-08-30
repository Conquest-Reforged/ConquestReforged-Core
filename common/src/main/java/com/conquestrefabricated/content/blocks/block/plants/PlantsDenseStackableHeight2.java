package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.effects.Effects;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Random;

@Assets(
        state = @State(name = "%s", template = "parent_plant_dense_stackable"),
        item = @Model(name = "item/%s", parent = "block/%s_pane_ns", template = "item/parent_round_arch"),
        render = @Render(RenderLayer.CUTOUT),
        block = {
                @Model(name = "block/%s_plant_dense_model_1_a", template = "block/parent_plant_dense_model_1_a"),
                @Model(name = "block/%s_plant_dense_model_1_b", template = "block/parent_plant_dense_model_1_b"),
                @Model(name = "block/%s_plant_dense_model_1_c", template = "block/parent_plant_dense_model_1_c"),
                @Model(name = "block/%s_plant_dense_model_2_a", template = "block/parent_plant_dense_model_2_a"),
                @Model(name = "block/%s_plant_dense_model_2_b", template = "block/parent_plant_dense_model_2_b"),
                @Model(name = "block/%s_plant_dense_model_2_c", template = "block/parent_plant_dense_model_2_c"),
                @Model(name = "block/%s_plant_dense_model_3_a", template = "block/parent_plant_dense_model_3_a"),
                @Model(name = "block/%s_plant_dense_model_3_b", template = "block/parent_plant_dense_model_3_b"),
                @Model(name = "block/%s_plant_dense_model_3_c", template = "block/parent_plant_dense_model_3_c"),
                @Model(name = "block/%s_plant_dense_model_4_a", template = "block/parent_plant_dense_model_4_a"),
                @Model(name = "block/%s_plant_dense_model_4_b", template = "block/parent_plant_dense_model_4_b"),
                @Model(name = "block/%s_plant_dense_model_4_c", template = "block/parent_plant_dense_model_4_c"),
                @Model(name = "block/%s_plant_dense_model_5_a", template = "block/parent_plant_dense_model_5_a"),
                @Model(name = "block/%s_plant_dense_model_5_b", template = "block/parent_plant_dense_model_5_b"),
                @Model(name = "block/%s_plant_dense_model_5_c", template = "block/parent_plant_dense_model_5_c"),
                @Model(name = "block/%s_plant_dense_model_6_a", template = "block/parent_plant_dense_model_6_a"),
                @Model(name = "block/%s_plant_dense_model_6_b", template = "block/parent_plant_dense_model_6_b"),
                @Model(name = "block/%s_plant_dense_model_6_c", template = "block/parent_plant_dense_model_6_c"),
                @Model(name = "block/%s_plant_dense_model_7_a", template = "block/parent_plant_dense_model_7_a"),
                @Model(name = "block/%s_plant_dense_model_7_b", template = "block/parent_plant_dense_model_7_b"),
                @Model(name = "block/%s_plant_dense_model_7_c", template = "block/parent_plant_dense_model_7_c"),
                @Model(name = "block/%s_plant_dense_model_8_a", template = "block/parent_plant_dense_model_8_a"),
                @Model(name = "block/%s_plant_dense_model_8_b", template = "block/parent_plant_dense_model_8_b"),
                @Model(name = "block/%s_plant_dense_model_8_c", template = "block/parent_plant_dense_model_8_c"),
                @Model(name = "block/%s_pane_ns", template = "block/parent_flatpane_ns"),
        }
)
public class PlantsDenseStackableHeight2 extends Bush {

    private static final IntegerProperty RANDOM = IntegerProperty.create("random", 0, 3);
    private static final IntegerProperty HEIGHT = IntegerProperty.create("height", 1, 2);

    public PlantsDenseStackableHeight2(Props properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {

        if (entity instanceof LivingEntity livingEntity && ConquestConfig.INSTANCE.plantSlowness.get()) {
            Holder<MobEffect> slowness = level.registryAccess()
                    .lookupOrThrow(Registries.MOB_EFFECT)
                    .wrapAsHolder(Effects.CUSTOM_SLOWNESS);

            if (livingEntity instanceof Player) {
                if (state.getValue(HEIGHT) == 1 ) {
                    livingEntity.addEffect(new MobEffectInstance(slowness, 15, 9, false, false));
                } else if (state.getValue(HEIGHT) == 2) {
                    livingEntity.addEffect(new MobEffectInstance(slowness, 15, 12, false, false));
                }
            } else {
                if (state.getValue(HEIGHT) == 1 ) {
                    livingEntity.addEffect(new MobEffectInstance(slowness, 15, 4, false, false));
                } else if (state.getValue(HEIGHT) == 2) {
                    livingEntity.addEffect(new MobEffectInstance(slowness, 15, 6, false, false));
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos up = blockpos.above();
        BlockPos down = blockpos.below();
        BlockState blockStateUp = iblockreader.getBlockState(up);
        BlockState blockStateDown = iblockreader.getBlockState(down);
        Random rand = new Random();
        int randomBlockstate = rand.nextInt(3);
        int layerBlockState = 8;

        if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS) || blockStateDown.hasProperty(LAYERS)) {
            layerBlockState = blockStateDown.getValue(LAYERS);
        } else if (blockStateUp.hasProperty(Layer.LAYERS) || blockStateUp.hasProperty(Slab.LAYERS) || blockStateUp.hasProperty(LAYERS)) {
            layerBlockState = blockStateUp.getValue(LAYERS);
        }

        if (blockStateUp.getBlock() instanceof PlantsDenseStackableHeight2) {
            randomBlockstate = blockStateUp.getValue(RANDOM);
        } else if (blockStateDown.getBlock() instanceof PlantsDenseStackableHeight2) {
            randomBlockstate = blockStateDown.getValue(RANDOM);
        }

        return super.getStateForPlacement(context)
                .setValue(LAYERS, layerBlockState)
                .setValue(RANDOM, randomBlockstate);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RANDOM, HEIGHT, LAYERS, WATERLOGGED, OFFSET_TOGGLE);
    }
}
