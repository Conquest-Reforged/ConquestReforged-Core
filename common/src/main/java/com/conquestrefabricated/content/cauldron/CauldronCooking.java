package com.conquestrefabricated.content.cauldron;

import com.conquestrefabricated.content.station.StationRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * One tick of one cauldron over its fire: much the same bargain as a furnace.
 *
 * <p>Fuel is only lit when there is something the pot could make and room for it, burns whether or not
 * the cooking carries on, and the work slips backwards while the fire is out. The recipe is whichever
 * satisfied one asks for the most ingredients, so a stew is not pre-empted by the broth it contains.</p>
 */
final class CauldronCooking {

    private CauldronCooking() {
    }

    /** @return whether anything about the cauldron changed, so it needs saving */
    static boolean step(ServerLevel level, BlockPos pos, BlockState state, CauldronData data) {
        boolean changed = false;
        if (data.litTime > 0) {
            data.litTime--;
            changed = true;
        }

        int water = CauldronBlocks.fluidLevel(state);
        if (water <= 0 && data.brine) {
            data.brine = false;
            changed = true;
        }
        CauldronRecipe.Input input = new CauldronRecipe.Input(
                List.copyOf(data.items.subList(0, CauldronData.INGREDIENT_SLOTS)), water, data.brine);
        RecipeHolder<CauldronRecipe> found = find(level, input);
        CauldronRecipe recipe = found == null ? null : found.value();

        if (recipe != null && fits(data, recipe.resultStack())) {
            if (data.litTime <= 0) {
                changed |= light(level, data);
            }
            if (data.litTime > 0) {
                data.total = recipe.time();
                data.progress++;
                changed = true;
                effects(level, pos);
                if (data.progress >= data.total) {
                    finish(level, pos, state, data, recipe, input);
                }
            } else if (data.progress > 0) {
                data.progress = Math.max(0, data.progress - 2);
                changed = true;
            }
        } else if (data.progress > 0) {
            data.progress = Math.max(0, data.progress - 2);
            changed = true;
        }
        return changed;
    }

    /** The recipe the contents make, preferring the one that wants the most ingredients. */
    static @Nullable RecipeHolder<CauldronRecipe> find(ServerLevel level, CauldronRecipe.Input input) {
        RecipeHolder<CauldronRecipe> best = null;
        for (RecipeHolder<CauldronRecipe> holder : StationRecipes.<CauldronRecipe.Input, CauldronRecipe>allOf(level,
                CauldronRecipe.TYPE, recipe -> true)) {
            if (holder.value().assign(input) != null
                    && (best == null || holder.value().ingredients().size() > best.value().ingredients().size())) {
                best = holder;
            }
        }
        return best;
    }

    private static boolean light(ServerLevel level, CauldronData data) {
        ItemStack fuel = data.items.get(CauldronData.FUEL_SLOT);
        if (fuel.isEmpty() || !level.fuelValues().isFuel(fuel)) {
            return false;
        }
        int burn = level.fuelValues().burnDuration(fuel);
        if (burn <= 0) {
            return false;
        }
        data.litTime = burn;
        data.litDuration = burn;
        ItemStack remainder = remainderOf(fuel);
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            data.items.set(CauldronData.FUEL_SLOT, remainder.isEmpty() ? ItemStack.EMPTY : remainder);
        }
        return true;
    }

    /** What is left of a stack's item when one is used: an empty bucket for lava, say. */
    private static ItemStack remainderOf(ItemStack stack) {
        ItemStackTemplate template = stack.getCraftingRemainder();
        return template == null ? ItemStack.EMPTY : template.create();
    }

    private static boolean fits(CauldronData data, ItemStack result) {
        ItemStack output = data.items.get(CauldronData.OUTPUT_SLOT);
        return output.isEmpty() || (ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= output.getMaxStackSize());
    }

    private static void finish(ServerLevel level, BlockPos pos, BlockState state, CauldronData data,
                               CauldronRecipe recipe, CauldronRecipe.Input input) {
        int[] slots = recipe.assign(input);
        if (slots == null) {
            data.progress = 0;
            return;
        }
        List<ItemStack> leftovers = new ArrayList<>();
        for (int slot : slots) {
            ItemStack stack = data.items.get(slot);
            ItemStack remainder = remainderOf(stack);
            stack.shrink(1);
            if (!remainder.isEmpty()) {
                leftovers.add(remainder);
            }
        }
        for (ItemStack remainder : leftovers) {
            net.minecraft.world.Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, remainder);
        }

        ItemStack result = recipe.resultStack();
        ItemStack output = data.items.get(CauldronData.OUTPUT_SLOT);
        if (output.isEmpty()) {
            data.items.set(CauldronData.OUTPUT_SLOT, result);
        } else {
            output.grow(result.getCount());
        }

        if (recipe.fluidUse() > 0) {
            int left = CauldronBlocks.fluidLevel(state) - recipe.fluidUse();
            level.setBlockAndUpdate(pos, CauldronBlocks.withFluidLevel(state, left));
            if (left <= 0) {
                data.brine = false;
            }
        }
        data.progress = 0;
    }

    /** Steam over the pot while it works. */
    private static void effects(ServerLevel level, BlockPos pos) {
        if (level.getGameTime() % 10 == 0) {
            level.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    1, 0.2, 0.0, 0.2, 0.01);
        }
        if (level.getGameTime() % 40 == 0) {
            level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.4F, 0.9F);
        }
    }
}
