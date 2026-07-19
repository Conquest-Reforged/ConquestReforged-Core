package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.tileentity.loom.LoomBlockEntity;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;
import static com.conquestrefabricated.content.blocks.util.PlacementHelper.isFacingSlab;

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

        LoomBlockEntity blockEntity = (LoomBlockEntity) level.getBlockEntity(blockPos);
        if (blockEntity == null) return InteractionResult.FAIL;

        String itemId = stack.getItem().toString();

        // Canvas blocks
        if (itemId.contains("red_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:red_canvas");
        else if (itemId.contains("black_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:black_canvas");
        else if (itemId.contains("light_gray_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:light_gray_canvas");
        else if (itemId.contains("gray_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:gray_canvas");
        else if (itemId.contains("white_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:white_canvas");
        else if (itemId.contains("brown_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:brown_canvas");
        else if (itemId.contains("yellow_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:yellow_canvas");
        else if (itemId.contains("orange_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:orange_canvas");
        else if (itemId.contains("pink_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:pink_canvas");
        else if (itemId.contains("magenta_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:magenta_canvas");
        else if (itemId.contains("purple_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:purple_canvas");
        else if (itemId.contains("light_blue_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:light_blue_canvas");
        else if (itemId.contains("blue_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:blue_canvas");
        else if (itemId.contains("cyan_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:cyan_canvas");
        else if (itemId.contains("green_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:green_canvas");
        else if (itemId.contains("lime_canvas")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:lime_canvas");

            // Rug blocks
        else if (itemId.contains("baotuo_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:baotuo_rug");
        else if (itemId.contains("berber_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:berber_rug");
        else if (itemId.contains("black_persian_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:black_persian_rug");
        else if (itemId.contains("blue_nain_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:blue_nain_rug");
        else if (itemId.contains("brown_oriental_carpet")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:brown_oriental_carpet");
        else if (itemId.contains("celtic_knot_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:celtic_knot_rug");
        else if (itemId.contains("kashmiri_carpet")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:kashmiri_carpet");
        else if (itemId.contains("kazakh_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:kazakh_rug");
        else if (itemId.contains("kilim_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:kilim_rug");
        else if (itemId.contains("nahavand_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:nahavand_rug");
        else if (itemId.contains("red_and_blue_sarouk_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:red_and_blue_sarouk_rug");
        else if (itemId.contains("red_oriental_carpet")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:red_oriental_carpet");
        else if (itemId.contains("red_pazyryk_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:red_pazyryk_rug");
        else if (itemId.contains("shirishabad_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:shirishabad_rug");
        else if (itemId.contains("william_morris_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:william_morris_rug");
        else if (itemId.contains("yellow_red_persian_rug")) return setProduct(level, blockPos, player, state, hand, blockEntity, "conquest:yellow_red_persian_rug");

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }

        LoomBlockEntity blockEntity = (LoomBlockEntity) level.getBlockEntity(blockPos);
        if (blockEntity == null) return InteractionResult.FAIL;

        if (player.isShiftKeyDown()) {
            if (!player.getAbilities().instabuild) {
                return dropProduct(level, blockPos, state, blockEntity);
            } else {
                level.setBlock(blockPos, state.setValue(HAS_THREAD, false), 3);
                blockEntity.setProduct("");
                blockEntity.setChanged();
                level.sendBlockUpdated(blockPos, state, state, 3);
                return InteractionResult.SUCCESS;
            }
        } else {
            level.setBlock(blockPos, state.cycle(POSITION), 3);
            return InteractionResult.SUCCESS;
        }
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
     * Sets the product for the loom block entity and updates the block state.
     * 
     * @param level The world
     * @param blockPos The position of the block
     * @param state The current block state
     * @param blockEntity The loom block entity
     * @param product The product to set
     * @return ActionResult.SUCCESS
     */
    private InteractionResult setProduct(Level level, BlockPos blockPos, Player player, BlockState state, InteractionHand hand, LoomBlockEntity blockEntity, String product) {
        if (!player.getAbilities().instabuild) {
            dropProduct(level, blockPos, state, blockEntity);
            player.getItemInHand(hand).shrink(1);
        }
        level.setBlock(blockPos, state.setValue(HAS_THREAD, true), 3);
        blockEntity.setProduct(product);
        blockEntity.setChanged();
        level.sendBlockUpdated(blockPos, state, state, 3);
        return InteractionResult.SUCCESS;
    }

    /**
     * Drops the current product from the loom block entity and updates the block state.
     * 
     * @param level The world
     * @param blockPos The position of the block
     * @param state The current block state
     * @param blockEntity The loom block entity
     * @return ActionResult.SUCCESS
     */
    private InteractionResult dropProduct(Level level, BlockPos blockPos, BlockState state, LoomBlockEntity blockEntity) {
        String product = blockEntity.getProduct();
        if (product != null && !product.isEmpty()) {
            // Create the item entity
            ItemStack itemToSpawn = new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse(product)).get(), 1);
            Vec3 spawnPos = Vec3.atCenterOf(blockPos).add(0, 1.3, 0);
            ItemEntity itemEntity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, itemToSpawn);
            itemEntity.setPickUpDelay(10);
            level.addFreshEntity(itemEntity);
            level.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);

            level.setBlock(blockPos, state.setValue(HAS_THREAD, false), 3);
            blockEntity.setProduct("");
            blockEntity.setChanged();
            level.sendBlockUpdated(blockPos, state, state, 3);
        }
        return InteractionResult.SUCCESS;
    }
}
