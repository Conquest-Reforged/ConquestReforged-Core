package com.conquestrefabricated.content.leatherworking;

import com.conquestrefabricated.content.blocks.block.decor.TanningFrame;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.station.StationRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * What a tanning frame has stretched on it, and where that has got to.
 *
 * <p>The frame holds one thing. Whatever it holds is worked by {@link StretchingRecipe}s: a manual step
 * waits for a tool, a timed step runs by itself, and what one makes is what the next looks for. Nothing
 * here names an item - it is all read from the recipes - so the frame drying a hide into parchment and
 * a frame doing something else entirely differ only in data.</p>
 *
 * <p>The block's {@code stretched} property follows whether anything is mounted; everything else lives
 * here and is never sent to the client.</p>
 */
public class TanningFrameBlockEntity extends BlockEntity {

    /** What is stretched on the frame. */
    private ItemStack mounted = ItemStack.EMPTY;
    /** What the running timed step turns {@link #mounted} into. */
    private ItemStack pending = ItemStack.EMPTY;
    private int progress;
    /** Length of the running timed step, or 0 when nothing is running. */
    private int duration;

    public TanningFrameBlockEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.TANNING_FRAME, pos, state);
    }

    // ---------------------------------------------------------------------------------- working

    public void tick(Level level) {
        if (level.isClientSide() || this.duration <= 0) {
            return;
        }

        this.progress++;
        if (this.progress < this.duration) {
            if (this.progress % 20 == 0) {
                this.setChanged();
                LeatherworkingUtil.notifyComparators(level, this.worldPosition);
            }
            return;
        }

        this.mounted = this.pending;
        this.pending = ItemStack.EMPTY;
        this.progress = 0;
        this.duration = 0;
        this.beginTimedStep(level);
        if (this.duration <= 0 && !this.hasManualStep(level, this.mounted)) {
            LeatherworkingUtil.ready(level, this.worldPosition);
        }
        this.setChanged();
        LeatherworkingUtil.notifyComparators(level, this.worldPosition);
    }

    /** Starts the timed step for what is mounted, if there is one. Steps chain, so this runs after each. */
    private void beginTimedStep(Level level) {
        RecipeHolder<StretchingRecipe> step = null;
        if (!this.mounted.isEmpty()) {
            for (RecipeHolder<StretchingRecipe> holder : recipes(level)) {
                if (!holder.value().isManual() && holder.value().input().test(this.mounted)) {
                    step = holder;
                    break;
                }
            }
        }
        if (step == null) {
            this.pending = ItemStack.EMPTY;
            this.progress = 0;
            this.duration = 0;
            return;
        }
        this.pending = step.value().made();
        this.progress = 0;
        this.duration = Math.max(1, step.value().time());
    }

    /**
     * 0-15, for a comparator: 0 when empty, 1 waiting for a tool, 2 up to 13 as a step runs, and 15 when
     * there is nothing more to do to what is on it.
     */
    public int signal(Level level) {
        if (this.mounted.isEmpty()) {
            return 0;
        }
        if (this.duration > 0) {
            return 2 + Math.min(11, this.progress * 12 / this.duration);
        }
        return this.hasManualStep(level, this.mounted) ? 1 : 15;
    }

    // -------------------------------------------------------------------------------- interaction

    /**
     * A right-click with something in hand.
     *
     * @return what to answer, or null if the item has nothing to do with the frame
     */
    public @Nullable InteractionResult useItem(ItemStack held, Player player, InteractionHand hand) {
        Level level = this.level;
        if (level == null || level.isClientSide() || held.isEmpty()) {
            return null;
        }

        if (this.mounted.isEmpty()) {
            if (!this.accepts(level, held)) {
                return null;
            }
            this.mounted = held.copyWithCount(1);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            this.beginTimedStep(level);
            this.setStretched(level, true);
            level.playSound(null, this.worldPosition, SoundEvents.WET_GRASS_PLACE, SoundSource.BLOCKS, 0.8F, 0.9F);
            this.changed(level);
            return InteractionResult.SUCCESS;
        }

        RecipeHolder<StretchingRecipe> step = null;
        for (RecipeHolder<StretchingRecipe> holder : recipes(level)) {
            StretchingRecipe recipe = holder.value();
            if (recipe.isManual() && recipe.input().test(this.mounted) && recipe.acceptsTool(held)) {
                step = holder;
                break;
            }
        }
        if (step == null) {
            return null;
        }

        this.mounted = step.value().made();
        held.hurtAndBreak(1, player, hand);
        this.beginTimedStep(level);
        level.playSound(null, this.worldPosition, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 0.8F, 1.1F);
        this.changed(level);
        return InteractionResult.SUCCESS;
    }

    /** A right-click with an empty hand. Never null: {@code PASS} is "nothing here for you". */
    public InteractionResult useEmpty(Player player) {
        Level level = this.level;
        if (level == null || level.isClientSide() || this.mounted.isEmpty()) {
            return InteractionResult.PASS;
        }

        // Taking it off early is how a hide is kept for tanning instead of left to dry.
        if (player.isShiftKeyDown()) {
            this.takeOff(level, player);
            return InteractionResult.SUCCESS;
        }
        if (this.duration > 0) {
            LeatherworkingUtil.progress(player, "message.conquest.stretching.progress", this.progress, this.duration);
            return InteractionResult.SUCCESS;
        }
        if (this.hasManualStep(level, this.mounted)) {
            LeatherworkingUtil.message(player, "message.conquest.stretching.needs_tool");
            return InteractionResult.SUCCESS;
        }
        this.takeOff(level, player);
        return InteractionResult.SUCCESS;
    }

    private void takeOff(Level level, Player player) {
        LeatherworkingUtil.give(player, this.mounted);
        this.mounted = ItemStack.EMPTY;
        this.pending = ItemStack.EMPTY;
        this.progress = 0;
        this.duration = 0;
        this.setStretched(level, false);
        level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
        this.changed(level);
    }

    private void changed(Level level) {
        this.setChanged();
        LeatherworkingUtil.notifyComparators(level, this.worldPosition);
    }

    private void setStretched(Level level, boolean stretched) {
        BlockState state = level.getBlockState(this.worldPosition);
        if (state.hasProperty(TanningFrame.STRETCHED) && state.getValue(TanningFrame.STRETCHED) != stretched) {
            level.setBlock(this.worldPosition, state.setValue(TanningFrame.STRETCHED, stretched), 3);
        }
    }

    private static List<RecipeHolder<StretchingRecipe>> recipes(Level level) {
        return StationRecipes.<SingleRecipeInput, StretchingRecipe>allOf(level, LeatherworkingStations.STRETCHING_TYPE,
                recipe -> true);
    }

    /** Whether any step, manual or timed, starts from {@code stack}. */
    private boolean accepts(Level level, ItemStack stack) {
        for (RecipeHolder<StretchingRecipe> holder : recipes(level)) {
            if (holder.value().input().test(stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasManualStep(Level level, ItemStack stack) {
        for (RecipeHolder<StretchingRecipe> holder : recipes(level)) {
            if (holder.value().isManual() && holder.value().input().test(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null && !this.level.isClientSide()) {
            LeatherworkingUtil.dropAll(this.level, pos, this.mounted);
        }
    }

    // ------------------------------------------------------------------------------------ saving

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("mounted", ItemStack.OPTIONAL_CODEC, this.mounted);
        output.store("pending", ItemStack.OPTIONAL_CODEC, this.pending);
        output.putInt("progress", this.progress);
        output.putInt("duration", this.duration);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.mounted = input.read("mounted", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.pending = input.read("pending", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.progress = input.getIntOr("progress", 0);
        this.duration = input.getIntOr("duration", 0);
    }
}
