package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.tileentity.loom.LoomBlockEntity;
import com.conquestrefabricated.content.loom.LoomWeaves;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;
import static com.conquestrefabricated.content.blocks.util.PlacementHelper.isFacingSlab;

/**
 * A loom, which is both a piece of furniture and a crafting station.
 *
 * <p>Right-clicking it opens the weaving picker; the weave it draws follows what is in its slots.
 * The block properties keep their own gestures: a mallet cycles the size, a sneaking empty hand
 * cycles the position, and a rug or bolt of canvas loads straight into the input slot rather than
 * making the player open the screen for it.</p>
 *
 * @see LoomBlockEntity
 */
@ItemDescription(description = "loom_toggle_4")
public class Loom extends HorizontalDirectional implements EntityBlock {

    public static final IntegerProperty SIZE = IntegerProperty.create("size", 1, 3);
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 1, 4);
    public static final BooleanProperty HAS_THREAD = BooleanProperty.create("has_thread");
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    public Loom(Props props) {
        super(props
                .customOffsetType(CustomOffsetType.LAYER_XYZ)
                .offset(BlockBehaviour.OffsetType.NONE)
                .dynamicBounds(true)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_THREAD, false).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        } else {
            return super.getCollisionShape(state, worldIn, pos, context);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIZE, POSITION, HAS_THREAD, OFFSET_TOGGLE);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = isFacingSlab(context);
        return super.getStateForPlacement(context).setValue(SIZE, 1).setValue(POSITION, 1).setValue(HAS_THREAD, false).setValue(OFFSET_TOGGLE, isSlab);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        //This here is to ensure that the NBT that gets placed is rendered when the block is placed, rather than
        //using the default "test" string for the LoomBlockEntity. If we don't do this, the loom texture is initially
        //rendered as the default (white)
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LoomBlockEntity loom) {
            TypedEntityData<BlockEntityType<?>> beData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (beData != null) {
                beData.loadInto(loom, world.registryAccess());
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }

        if (stack.getItem().equals(Items.DEBUG_STICK)) return InteractionResult.TRY_WITH_EMPTY_HAND;

        if (stack.is(CYCLING_TOOLS)) {
            level.setBlock(blockPos, state.cycle(SIZE), 3);
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(blockPos) instanceof LoomBlockEntity blockEntity)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        // Anything with a weave of its own - a rug, a bolt of canvas - goes straight onto the loom,
        // which is the gesture looms have always had. Everything else falls through, so the loom
        // does not swallow a right-click meant to place a block against it.
        if (LoomWeaves.isKnown(LoomWeaves.productOf(stack))) {
            return loadWeave(level, blockPos, player, hand, blockEntity, stack);
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            level.setBlock(blockPos, state.cycle(POSITION), 3);
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(blockPos) instanceof LoomBlockEntity blockEntity)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide() && blockEntity instanceof MenuProvider provider) {
            player.openMenu(provider);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LoomBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == TileEntityTypes.LOOM ?
                (world1, pos, state1, blockEntity) -> ((LoomBlockEntity) blockEntity).tick(world1, pos) : null;
    }

    /**
     * Loads a cloth into the loom's input slot, which is what makes its weave show.
     *
     * <p>Where this used to set the weave directly and eat the item, it now hands the item to the
     * loom, so the same click both dresses the loom and leaves the cloth somewhere the player can
     * take it back from - or cut into layers with the picker's family toggle.</p>
     *
     * @return {@code SUCCESS} once the cloth is on the loom, or {@code TRY_WITH_EMPTY_HAND} if the
     *         input slot is already busy with something else
     */
    private static InteractionResult loadWeave(Level level, BlockPos blockPos, Player player, InteractionHand hand,
                                               LoomBlockEntity blockEntity, ItemStack stack) {
        ItemStack input = blockEntity.getItem(LoomBlockEntity.INPUT_SLOT);
        boolean stackable = ItemStack.isSameItemSameComponents(input, stack)
                && input.getCount() < input.getMaxStackSize();
        if (!input.isEmpty() && !stackable) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (input.isEmpty()) {
            blockEntity.setItem(LoomBlockEntity.INPUT_SLOT, stack.copyWithCount(1));
        } else {
            input.grow(1);
            blockEntity.setChanged();
        }

        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }

        level.playSound(null, blockPos, SoundEvents.UI_LOOM_SELECT_PATTERN, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }
}
