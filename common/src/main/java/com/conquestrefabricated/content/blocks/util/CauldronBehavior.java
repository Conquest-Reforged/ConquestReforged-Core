package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class CauldronBehavior {
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", MIN_LEVEL, MAX_LEVEL);

    private final List<VoxelShape> hitBox;

    public CauldronBehavior(List<VoxelShape> hitBox) {
        this.hitBox = hitBox;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        }
        return Shapes.block();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return hitBox.get(0);
    }

    public boolean calculateSlabOffset(Direction facing, Block block, BlockState state, BlockPlaceContext context) {
        if (facing == Direction.DOWN) {
            return block instanceof Slab ||
                    block instanceof SlabBlock ||
                    block instanceof Layer ||
                    block instanceof SnowLayerBlock ||
                    block instanceof SlabLessLayers ||
                    block instanceof BoardsHorizontal;
        } else if (facing != Direction.UP) {
            return block instanceof VerticalSlab && state.getValue(VerticalSlab.DIRECTION) == context.getClickedFace();
        }
        return false;
    }

    /**
     * Item interactions that add or remove water.
     * <p>
     * Unlike the vanilla {@code CauldronInteractions} dispatchers this never swaps the block for
     * {@code minecraft:water_cauldron} / {@code lava_cauldron} / {@code powder_snow_cauldron};
     * it only moves {@link #LEVEL} on the modded block. Lava and powder snow are deliberately
     * not supported, and anything else falls through to the empty-hand interaction.
     */
    public InteractionResult useItemOn(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        int level = state.getValue(LEVEL);
        Item item = stack.getItem();

        if (item == Items.WATER_BUCKET) {
            // Same as vanilla: a water bucket always fills the cauldron to the brim.
            return exchange(state.setValue(LEVEL, MAX_LEVEL), world, pos, player, hand, stack,
                    new ItemStack(Items.BUCKET), Stats.FILL_CAULDRON, SoundEvents.BUCKET_EMPTY, GameEvent.FLUID_PLACE);
        }
        if (item == Items.BUCKET) {
            if (level < MAX_LEVEL) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            return exchange(state.setValue(LEVEL, MIN_LEVEL), world, pos, player, hand, stack,
                    new ItemStack(Items.WATER_BUCKET), Stats.USE_CAULDRON, SoundEvents.BUCKET_FILL, GameEvent.FLUID_PICKUP);
        }
        if (item == Items.GLASS_BOTTLE) {
            if (level <= MIN_LEVEL) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            return exchange(state.setValue(LEVEL, level - 1), world, pos, player, hand, stack,
                    PotionContents.createItemStack(Items.POTION, Potions.WATER), Stats.USE_CAULDRON, SoundEvents.BOTTLE_FILL, GameEvent.FLUID_PICKUP);
        }
        if (item == Items.POTION) {
            if (level >= MAX_LEVEL || !isWaterBottle(stack)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            return exchange(state.setValue(LEVEL, level + 1), world, pos, player, hand, stack,
                    new ItemStack(Items.GLASS_BOTTLE), Stats.USE_CAULDRON, SoundEvents.BOTTLE_EMPTY, GameEvent.FLUID_PLACE);
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static boolean isWaterBottle(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(Potions.WATER);
    }

    /**
     * Server-side half of an interaction: swaps the held item for {@code result}, awards the
     * cauldron stats, writes {@code newState} and plays the matching sound / game event.
     * Mirrors what vanilla's package-private {@code CauldronInteractions.fillBucket} /
     * {@code emptyBucket} do, minus the block replacement.
     */
    private static InteractionResult exchange(BlockState newState, Level world, BlockPos pos, Player player, InteractionHand hand,
                                              ItemStack used, ItemStack result, Identifier stat, SoundEvent sound, Holder<GameEvent> event) {
        if (!world.isClientSide()) {
            Item item = used.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(used, player, result));
            player.awardStat(stat);
            player.awardStat(Stats.ITEM_USED.get(item));
            world.setBlockAndUpdate(pos, newState);
            world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, event, pos);
        }
        return InteractionResult.SUCCESS;
    }

    public void onEntityCollision(BlockState state, Level world, BlockPos pos, Entity entity) {
        int level = state.getValue(LEVEL);
        float waterLevel = (float) pos.getY() + (6.0F + (float) (3 * level)) / 16.0F;

        if (!world.isClientSide() && entity.isOnFire() && level > 0 && entity.getY() <= (double) waterLevel) {
            entity.clearFire();
            world.setBlockAndUpdate(pos, state.setValue(LEVEL, level - 1));
        }
    }

    public void precipitationTick(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        if (world.getRandom().nextInt(20) == 1) {
            float temperature = world.getBiome(pos).value().getBaseTemperature();
            if (temperature >= 0.15F) {
                BlockState currentState = world.getBlockState(pos);
                if (currentState.getValue(LEVEL) < MAX_LEVEL) {
                    world.setBlock(pos, currentState.cycle(LEVEL), 2);
                }
            }
        }
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, Level world, BlockPos pos) {
        return state.getValue(LEVEL);
    }
}