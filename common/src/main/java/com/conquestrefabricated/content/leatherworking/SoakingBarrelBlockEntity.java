package com.conquestrefabricated.content.leatherworking;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.util.CauldronBehavior;
import com.conquestrefabricated.content.station.StationRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * What a barrel is doing: one batch of one thing, soaking in the water.
 *
 * <p>The barrel takes one kind of thing at a time - up to {@link #MAX_BATCH} of it - and works the
 * whole batch as one. Whatever recipe matches decides how long it takes and what it becomes. A recipe
 * that names an additive wants that dissolved in the water first; the additive is remembered here
 * until something to soak in it turns up, and washed away if the barrel is drained.</p>
 *
 * <p>There is deliberately no menu. The player is told how it is going on the action bar, by
 * bubbles rising from the water, by a comparator, and by the colour of the water itself - see
 * {@link #waterColor()}. That colour is the only thing sent to the client.</p>
 *
 * @see SoakingRecipe
 */
public class SoakingBarrelBlockEntity extends BlockEntity {

    /** How many of a thing the barrel soaks at once. */
    public static final int MAX_BATCH = 16;

    /** {@link net.minecraft.world.item.ItemStack} counts stop at 99 when saved, so a batch's yield must too. */
    private static final int MAX_SAVED_COUNT = 99;

    private static final int NO_COLOR = SoakingRecipe.NO_COLOR;

    /** The batch soaking - or, once it is finished, what it made, waiting to be collected. */
    private ItemStack content = ItemStack.EMPTY;
    /** What {@link #content} becomes when the soak finishes. */
    private ItemStack result = ItemStack.EMPTY;
    /** Dissolved in the water, waiting for something to soak in it. */
    private ItemStack additive = ItemStack.EMPTY;
    /** The additive the running soak used up, given back if it is called off. */
    private ItemStack reserved = ItemStack.EMPTY;

    private boolean soaking;
    private int progress;
    private int duration;

    /** The colour the dissolved {@link #additive} gives the water, as {@code 0xRRGGBB}. */
    private int additiveColor = NO_COLOR;
    /** The colour the current batch gives the water while it is in there. */
    private int jobColor = NO_COLOR;
    /**
     * What the water is drawn as, or {@link SoakingRecipe#NO_COLOR} for plain water. Worked out on the
     * server from the two above and sent to clients; a client only ever holds this one.
     */
    private int waterColor = NO_COLOR;

    public SoakingBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.SOAKING_BARREL, pos, state);
    }

    /** The colour to draw the water, as {@code 0xRRGGBB}, or {@link SoakingRecipe#NO_COLOR} for plain water. */
    public int waterColor() {
        return this.waterColor;
    }

    // ---------------------------------------------------------------------------------- working

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        if (state.getValue(CauldronBehavior.LEVEL) <= 0) {
            // Nothing to soak in, and nothing left to hold an additive.
            if (!this.additive.isEmpty()) {
                this.additive = ItemStack.EMPTY;
                this.additiveColor = NO_COLOR;
                this.setChanged();
                this.refreshColor();
            }
            return;
        }

        if (!this.soaking) {
            return;
        }

        this.progress++;
        if (this.progress >= this.duration) {
            this.finish(level, pos);
            return;
        }

        if (this.progress % 8 == 0 && level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5,
                    2, 0.25, 0.0, 0.25, 0.01);
        }
        if (this.progress % 20 == 0) {
            this.setChanged();
            LeatherworkingUtil.notifyComparators(level, pos);
        }
    }

    private void finish(Level level, BlockPos pos) {
        this.content = this.result;
        this.result = ItemStack.EMPTY;
        this.reserved = ItemStack.EMPTY;
        this.soaking = false;
        this.progress = 0;
        this.duration = 0;
        this.setChanged();
        LeatherworkingUtil.ready(level, pos);
        LeatherworkingUtil.notifyComparators(level, pos);
    }

    /** 0-15, for a comparator: the water while idle, then 4 up to 13 as it soaks, then 15 when done. */
    public int signal(BlockState state) {
        if (this.soaking) {
            return 4 + Math.min(9, this.progress * 10 / Math.max(1, this.duration));
        }
        if (!this.content.isEmpty()) {
            return 15;
        }
        return state.getValue(CauldronBehavior.LEVEL);
    }

    // -------------------------------------------------------------------------------- interaction

    /**
     * A right-click with something in hand.
     *
     * @return what to answer, or null if the item has nothing to do with soaking - in which case the
     *         block carries on as the plain water barrel it always was
     */
    public @Nullable InteractionResult useItem(ItemStack held, Player player, InteractionHand hand) {
        Level level = this.level;
        if (level == null || level.isClientSide() || held.isEmpty()) {
            return null;
        }

        List<RecipeHolder<SoakingRecipe>> recipes = recipes(level);
        boolean isIngredient = false;
        boolean isAdditive = false;
        for (RecipeHolder<SoakingRecipe> holder : recipes) {
            isIngredient |= holder.value().input().test(held);
            isAdditive |= holder.value().acceptsAdditive(held);
        }
        if (!isIngredient && !isAdditive) {
            return null;
        }

        BlockState state = this.getBlockState();
        if (state.getValue(CauldronBehavior.LEVEL) <= 0) {
            LeatherworkingUtil.message(player, "message.conquest.soaking.needs_water");
            return InteractionResult.SUCCESS;
        }
        if (!this.content.isEmpty()) {
            LeatherworkingUtil.message(player, "message.conquest.soaking.busy");
            return InteractionResult.SUCCESS;
        }

        RecipeHolder<SoakingRecipe> match = this.match(recipes, held);
        if (match != null) {
            this.start(level, player, held, match.value());
            return InteractionResult.SUCCESS;
        }

        if (isAdditive && this.additive.isEmpty()) {
            this.dissolve(level, player, held, recipes);
            return InteractionResult.SUCCESS;
        }
        if (isAdditive) {
            LeatherworkingUtil.message(player, "message.conquest.soaking.already_treated", this.additive.getHoverName());
        } else {
            LeatherworkingUtil.message(player, "message.conquest.soaking.needs_additive");
        }
        return InteractionResult.SUCCESS;
    }

    /** A right-click with an empty hand. Never null: {@code PASS} is "nothing here for you". */
    public InteractionResult useEmpty(Player player) {
        Level level = this.level;
        if (level == null || level.isClientSide()) {
            return InteractionResult.PASS;
        }

        if (this.soaking) {
            if (player.isShiftKeyDown()) {
                this.cancel(level, player);
            } else {
                LeatherworkingUtil.progress(player, "message.conquest.soaking.progress", this.progress, this.duration);
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.content.isEmpty()) {
            LeatherworkingUtil.give(player, this.content);
            this.content = ItemStack.EMPTY;
            this.jobColor = NO_COLOR;
            level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
            this.setChanged();
            this.refreshColor();
            LeatherworkingUtil.notifyComparators(level, this.worldPosition);
            return InteractionResult.SUCCESS;
        }

        if (!this.additive.isEmpty()) {
            if (player.isShiftKeyDown()) {
                LeatherworkingUtil.give(player, this.additive);
                this.additive = ItemStack.EMPTY;
                this.additiveColor = NO_COLOR;
                this.setChanged();
                this.refreshColor();
            } else {
                LeatherworkingUtil.message(player, "message.conquest.soaking.treated", this.additive.getHoverName());
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static List<RecipeHolder<SoakingRecipe>> recipes(Level level) {
        return StationRecipes.<SingleRecipeInput, SoakingRecipe>allOf(level, LeatherworkingStations.SOAKING_TYPE,
                recipe -> true);
    }

    /**
     * The recipe that soaks {@code held} in the water as it is. One naming the additive already in the
     * water wins; failing that, one that wants no additive - the water it is in is simply left alone.
     */
    private @Nullable RecipeHolder<SoakingRecipe> match(List<RecipeHolder<SoakingRecipe>> recipes, ItemStack held) {
        RecipeHolder<SoakingRecipe> plain = null;
        for (RecipeHolder<SoakingRecipe> holder : recipes) {
            SoakingRecipe recipe = holder.value();
            if (!recipe.input().test(held)) {
                continue;
            }
            if (recipe.needsAdditive()) {
                if (!this.additive.isEmpty() && recipe.acceptsAdditive(this.additive)) {
                    return holder;
                }
            } else if (plain == null) {
                plain = holder;
            }
        }
        return plain;
    }

    private void start(Level level, Player player, ItemStack held, SoakingRecipe recipe) {
        int perItem = Math.max(1, recipe.resultFor(1).getCount());
        int batch = Math.max(1, Math.min(Math.min(held.getCount(), MAX_BATCH), MAX_SAVED_COUNT / perItem));

        this.content = held.copyWithCount(batch);
        this.result = recipe.resultFor(batch);
        this.reserved = ItemStack.EMPTY;

        // A recipe's own colour wins; without one it keeps the colour of the additive it is using up.
        this.jobColor = recipe.hasWaterColor() ? recipe.waterColor() : NO_COLOR;
        if (recipe.needsAdditive()) {
            if (!recipe.hasWaterColor()) {
                this.jobColor = this.additiveColor;
            }
            this.reserved = this.additive;
            this.additive = ItemStack.EMPTY;
            this.additiveColor = NO_COLOR;
        }

        this.duration = Math.max(1, recipe.time());
        this.progress = 0;
        this.soaking = true;

        if (!player.getAbilities().instabuild) {
            held.shrink(batch);
        }
        level.playSound(null, this.worldPosition, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5F, 1.2F);
        this.setChanged();
        this.refreshColor();
        LeatherworkingUtil.notifyComparators(level, this.worldPosition);
    }

    private void dissolve(Level level, Player player, ItemStack held, List<RecipeHolder<SoakingRecipe>> recipes) {
        this.additive = held.copyWithCount(1);
        this.additiveColor = additiveColorFor(recipes, held);
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        level.playSound(null, this.worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.5F, 1.4F);
        LeatherworkingUtil.message(player, "message.conquest.soaking.treated", this.additive.getHoverName());
        this.setChanged();
        this.refreshColor();
    }

    /** What colour dissolving {@code stack} makes the water: the first recipe naming it that has one. */
    private static int additiveColorFor(List<RecipeHolder<SoakingRecipe>> recipes, ItemStack stack) {
        for (RecipeHolder<SoakingRecipe> holder : recipes) {
            if (holder.value().acceptsAdditive(stack) && holder.value().hasWaterColor()) {
                return holder.value().waterColor();
            }
        }
        return NO_COLOR;
    }

    /** Calls off a soak: the batch and the additive go back to the player, and the progress is lost. */
    private void cancel(Level level, Player player) {
        LeatherworkingUtil.give(player, this.content);
        if (!this.reserved.isEmpty()) {
            LeatherworkingUtil.give(player, this.reserved);
        }
        this.content = ItemStack.EMPTY;
        this.result = ItemStack.EMPTY;
        this.reserved = ItemStack.EMPTY;
        this.jobColor = NO_COLOR;
        this.soaking = false;
        this.progress = 0;
        this.duration = 0;
        level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
        this.setChanged();
        this.refreshColor();
        LeatherworkingUtil.notifyComparators(level, this.worldPosition);
    }

    /**
     * Works out what colour the water should be drawn and, if that has changed, tells the clients. A batch
     * in the barrel colours it with the batch's colour when it has one; otherwise it is the additive's.
     */
    private void refreshColor() {
        int target = !this.content.isEmpty() && this.jobColor != NO_COLOR ? this.jobColor : this.additiveColor;
        if (target == this.waterColor) {
            return;
        }
        this.waterColor = target;
        this.setChanged();
        Level level = this.level;
        if (level != null && !level.isClientSide()) {
            BlockState state = this.getBlockState();
            level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null && !this.level.isClientSide()) {
            LeatherworkingUtil.dropAll(this.level, pos, this.content, this.reserved, this.additive);
        }
    }

    // ------------------------------------------------------------------------------------ syncing

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** Just the water colour: what a barrel holds is nobody's business but the player using it. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("water_color", this.waterColor);
        return tag;
    }

    // ------------------------------------------------------------------------------------ saving

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("content", ItemStack.OPTIONAL_CODEC, this.content);
        output.store("result", ItemStack.OPTIONAL_CODEC, this.result);
        output.store("additive", ItemStack.OPTIONAL_CODEC, this.additive);
        output.store("reserved", ItemStack.OPTIONAL_CODEC, this.reserved);
        output.putBoolean("soaking", this.soaking);
        output.putInt("progress", this.progress);
        output.putInt("duration", this.duration);
        output.putInt("additive_color", this.additiveColor);
        output.putInt("job_color", this.jobColor);
        output.putInt("water_color", this.waterColor);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int before = this.waterColor;

        this.content = input.read("content", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.result = input.read("result", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.additive = input.read("additive", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.reserved = input.read("reserved", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.soaking = input.getBooleanOr("soaking", false);
        this.progress = input.getIntOr("progress", 0);
        this.duration = input.getIntOr("duration", 0);
        this.additiveColor = input.getIntOr("additive_color", NO_COLOR);
        this.jobColor = input.getIntOr("job_color", NO_COLOR);
        this.waterColor = input.getIntOr("water_color", NO_COLOR);

        // On a client this is how a new colour arrives, and a chunk is not redrawn just because a
        // block entity changed - so ask for it.
        if (this.waterColor != before && this.level != null && this.level.isClientSide()) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_IMMEDIATE);
        }
    }
}
