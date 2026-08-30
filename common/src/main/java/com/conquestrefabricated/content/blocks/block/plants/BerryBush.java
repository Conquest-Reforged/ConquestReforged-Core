package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@Assets(
        state = @State(name = "%s", template = "parent_berry_bush"),
        item = @Model(name = "item/%s", parent = "block/%s_pane_ns", template = "item/parent_round_arch"),
        render = @Render(RenderLayer.CUTOUT),
        block = {
                @Model(name = "block/%s_shrub_model_1_a", template = "block/parent_shrub_model_1_a"),
                @Model(name = "block/%s_shrub_model_1_b", template = "block/parent_shrub_model_1_b"),
                @Model(name = "block/%s_shrub_model_1_c", template = "block/parent_shrub_model_1_c"),
                @Model(name = "block/%s_berry_bush_model_1_a", template = "block/parent_berry_bush_model_1_a"),
                @Model(name = "block/%s_berry_bush_model_1_b", template = "block/parent_berry_bush_model_1_b"),
                @Model(name = "block/%s_berry_bush_model_1_c", template = "block/parent_berry_bush_model_1_c"),
                @Model(name = "block/%s_shrub_model_2_a", template = "block/parent_shrub_model_2_a"),
                @Model(name = "block/%s_shrub_model_2_b", template = "block/parent_shrub_model_2_b"),
                @Model(name = "block/%s_shrub_model_2_c", template = "block/parent_shrub_model_2_c"),
                @Model(name = "block/%s_berry_bush_model_2_a", template = "block/parent_berry_bush_model_2_a"),
                @Model(name = "block/%s_berry_bush_model_2_b", template = "block/parent_berry_bush_model_2_b"),
                @Model(name = "block/%s_berry_bush_model_2_c", template = "block/parent_berry_bush_model_2_c"),
                @Model(name = "block/%s_shrub_model_3_a", template = "block/parent_shrub_model_3_a"),
                @Model(name = "block/%s_shrub_model_3_b", template = "block/parent_shrub_model_3_b"),
                @Model(name = "block/%s_shrub_model_3_c", template = "block/parent_shrub_model_3_c"),
                @Model(name = "block/%s_berry_bush_model_3_a", template = "block/parent_berry_bush_model_3_a"),
                @Model(name = "block/%s_berry_bush_model_3_b", template = "block/parent_berry_bush_model_3_b"),
                @Model(name = "block/%s_berry_bush_model_3_c", template = "block/parent_berry_bush_model_3_c"),
                @Model(name = "block/%s_shrub_model_4_a", template = "block/parent_shrub_model_4_a"),
                @Model(name = "block/%s_shrub_model_4_b", template = "block/parent_shrub_model_4_b"),
                @Model(name = "block/%s_shrub_model_4_c", template = "block/parent_shrub_model_4_c"),
                @Model(name = "block/%s_berry_bush_model_4_a", template = "block/parent_berry_bush_model_4_a"),
                @Model(name = "block/%s_berry_bush_model_4_b", template = "block/parent_berry_bush_model_4_b"),
                @Model(name = "block/%s_berry_bush_model_4_c", template = "block/parent_berry_bush_model_4_c"),
                @Model(name = "block/%s_shrub_model_5_a", template = "block/parent_shrub_model_5_a"),
                @Model(name = "block/%s_shrub_model_5_b", template = "block/parent_shrub_model_5_b"),
                @Model(name = "block/%s_shrub_model_5_c", template = "block/parent_shrub_model_5_c"),
                @Model(name = "block/%s_berry_bush_model_5_a", template = "block/parent_berry_bush_model_5_a"),
                @Model(name = "block/%s_berry_bush_model_5_b", template = "block/parent_berry_bush_model_5_b"),
                @Model(name = "block/%s_berry_bush_model_5_c", template = "block/parent_berry_bush_model_5_c"),
                @Model(name = "block/%s_shrub_model_6_a", template = "block/parent_shrub_model_6_a"),
                @Model(name = "block/%s_shrub_model_6_b", template = "block/parent_shrub_model_6_b"),
                @Model(name = "block/%s_shrub_model_6_c", template = "block/parent_shrub_model_6_c"),
                @Model(name = "block/%s_berry_bush_model_6_a", template = "block/parent_berry_bush_model_6_a"),
                @Model(name = "block/%s_berry_bush_model_6_b", template = "block/parent_berry_bush_model_6_b"),
                @Model(name = "block/%s_berry_bush_model_6_c", template = "block/parent_berry_bush_model_6_c"),
                @Model(name = "block/%s_shrub_model_7_a", template = "block/parent_shrub_model_7_a"),
                @Model(name = "block/%s_shrub_model_7_b", template = "block/parent_shrub_model_7_b"),
                @Model(name = "block/%s_shrub_model_7_c", template = "block/parent_shrub_model_7_c"),
                @Model(name = "block/%s_berry_bush_model_7_a", template = "block/parent_berry_bush_model_7_a"),
                @Model(name = "block/%s_berry_bush_model_7_b", template = "block/parent_berry_bush_model_7_b"),
                @Model(name = "block/%s_berry_bush_model_7_c", template = "block/parent_berry_bush_model_7_c"),
                @Model(name = "block/%s_shrub_model_8_a", template = "block/parent_shrub_model_8_a"),
                @Model(name = "block/%s_shrub_model_8_b", template = "block/parent_shrub_model_8_b"),
                @Model(name = "block/%s_shrub_model_8_c", template = "block/parent_shrub_model_8_c"),
                @Model(name = "block/%s_berry_bush_model_8_a", template = "block/parent_berry_bush_model_8_a"),
                @Model(name = "block/%s_berry_bush_model_8_b", template = "block/parent_berry_bush_model_8_b"),
                @Model(name = "block/%s_berry_bush_model_8_c", template = "block/parent_berry_bush_model_8_c"),
                @Model(name = "block/%s_pane_ns", template = "block/parent_flatpane_ns"),
        }
)

public class BerryBush extends AbstractCropsBlock {

    private final ItemLike fruit;

    public BerryBush(Props props) {
        super(props);
        this.fruit = props.get("fruit", ItemLike.class);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity && ConquestConfig.INSTANCE.plantSlowness.get()) {
            int age = state.getValue(AGE);
            if (slowness > 0) {
                Holder<MobEffect> slownessKey = level.registryAccess()
                        .lookupOrThrow(Registries.MOB_EFFECT)
                        .wrapAsHolder(Effects.CUSTOM_SLOWNESS);


                if (livingEntity instanceof Player) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, this.slowness, false, false));
                } else if (slowness > 1) {
                    livingEntity.addEffect(new MobEffectInstance(slownessKey, 15, this.slowness - 1, false, false));
                }
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BlockVoxelShapes.cubeMediumLargePartialShape.get(0);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return BlockVoxelShapes.cubeMediumLargePartialShape.get(0);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(this, 1);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (this.isMaxAge(state)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            } else {
                state = state.setValue(AGE, 0);
                level.setBlock(blockPos, state, 3);
                this.dropFruit(level, blockPos, state);
                return InteractionResult.SUCCESS;

            }
        }
        return InteractionResult.FAIL;
    }

    private void dropFruit(Level world, BlockPos pos, BlockState state) {
        if (!world.isClientSide()) {
            float f = 0.7F;
            double d0 = (double) (world.getRandom().nextFloat() * 0.7F) + 0.15000000596046448D;
            double d1 = (double) (world.getRandom().nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double d2 = (double) (world.getRandom().nextFloat() * 0.7F) + 0.15000000596046448D;
            ItemStack itemstack1 = new ItemStack(fruit, 1);
            ItemEntity entityitem = new ItemEntity(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
            entityitem.setDefaultPickUpDelay();

            // addEntity == spawnEntitiy
            world.addFreshEntity(entityitem);
        }
    }

    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
}
