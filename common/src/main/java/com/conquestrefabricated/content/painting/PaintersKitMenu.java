package com.conquestrefabricated.content.painting;

import com.conquestrefabricated.content.station.StationMenu;
import com.conquestrefabricated.content.station.StationRecipes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

/**
 * The painter's kit picker: a base slot, a paint slot and a result slot.
 *
 * <p>Two things set it apart from the single-ingredient kits.</p>
 *
 * <p>It lists what the base <i>could</i> become before any paint is in, marking each option with the
 * paint it is waiting for, so you can see that cobblestone takes lime or a dye without having to
 * guess. Those options are inert until the paint is there - see {@link #missingFor}.</p>
 *
 * <p>And it only offers family shapes for blocks it painted itself: reshaping is not painting, so a
 * kit will cut the slabs of a stucco it made, but has no business cutting the raw cobblestone it
 * paints onto - and cutting costs no paint.</p>
 */
public class PaintersKitMenu extends StationMenu<PaintingRecipe, PaintingInput> {

    /** Menu index of the paint slot. The base and result keep the shared slots 0 and 1. */
    public static final int PAINT_SLOT = 2;

    protected final Slot baseSlot;
    protected final Slot paintSlot;
    protected final Slot resultSlot;

    public final Container container = new SimpleContainer(PaintingInput.SLOT_COUNT) {
        @Override
        public void setChanged() {
            super.setChanged();
            PaintersKitMenu.this.slotsChanged(this);
            PaintersKitMenu.this.notifyScreen();
        }
    };

    private final ResultContainer resultContainer = new ResultContainer();

    public PaintersKitMenu(int containerId, Inventory inventory) {
        super(PaintersKit.MENU, containerId, inventory, ContainerLevelAccess.NULL);

        this.baseSlot = this.addSlot(new Slot(this.container, PaintingInput.BASE_SLOT, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player, stack.getCount());
                PaintersKitMenu.this.resultContainer.awardUsedRecipes(
                        player, List.of(PaintersKitMenu.this.baseSlot.getItem()));
                PaintersKitMenu.this.spendIngredients();
                PaintersKitMenu.this.playTakeSound(player);
                super.onTake(player, stack);
            }
        });
        this.paintSlot = this.addSlot(new Slot(this.container, PaintingInput.PAINT_SLOT, 20, 51));
        this.addPlayerInventory(inventory);
    }

    // ------------------------------------------------------------------------------------- inputs

    @Override
    protected RecipeType<PaintingRecipe> recipeType() {
        return PaintersKit.RECIPE_TYPE;
    }

    @Override
    protected ItemStack stationInput() {
        return this.baseSlot.getItem();
    }

    @Override
    protected PaintingInput recipeInput() {
        return new PaintingInput(this.baseSlot.getItem(), this.paintSlot.getItem());
    }

    @Override
    protected PaintingInput emptyRecipeInput() {
        return PaintingInput.EMPTY;
    }

    @Override
    protected PaintingInput inputWith(ItemStack stack) {
        return new PaintingInput(stack, this.paintSlot.getItem());
    }

    /** Both slots matter, so a paint arriving rebuilds the list even though the base has not moved. */
    @Override
    protected Object inputSignature() {
        return List.of(this.baseSlot.getItem().getItem(), this.paintSlot.getItem().getItem());
    }

    // ------------------------------------------------------------------------------------ options

    /**
     * Everything the base could become, whether or not the paint for it is in yet. What is missing
     * is reported by {@link #missingFor} rather than by leaving the option out.
     */
    @Override
    protected List<RecipeHolder<PaintingRecipe>> offeredRecipes() {
        ItemStack base = this.baseSlot.getItem();
        if (base.isEmpty()) {
            return List.of();
        }

        List<RecipeHolder<PaintingRecipe>> matching = new ArrayList<>();
        for (RecipeHolder<PaintingRecipe> holder
                : StationRecipes.<PaintingInput, PaintingRecipe>allOf(this.level, this.recipeType(), this::accepts)) {
            if (holder.value().matchesBase(base)) {
                matching.add(holder);
            }
        }
        return matching;
    }

    @Override
    protected ItemStack missingFor(PaintingRecipe recipe) {
        if (recipe.matchesPaint(this.paintSlot.getItem())) {
            return ItemStack.EMPTY;
        }
        // Name one thing that would do. A recipe whose paint tag is empty can never be satisfied, and
        // a barrier says so plainly rather than the option looking ready.
        return recipe.paint().items().findFirst()
                .map(ItemStack::new)
                .orElseGet(() -> new ItemStack(Items.BARRIER));
    }

    /**
     * A kit shapes what it painted, and nothing else. Deliberately narrower than the crafting tools,
     * which also shape anything they have a recipe for: being able to paint cobblestone should not
     * turn a painter's kit into a stonecutter.
     */
    @Override
    protected boolean worksWith(ItemStack input) {
        return StationRecipes.produces(this.level, this.recipeType(), this::accepts, input,
                this.emptyRecipeInput())
                || this.isShapeWhitelisted(input);
    }

    /** Extra materials this kit may shape, on top of what it painted itself. */
    @Override
    protected Optional<TagKey<Item>> shapesTag() {
        return Optional.of(StationMenu.shapesTagFor(PaintersKit.ID));
    }

    @Override
    public boolean supportsVariants() {
        return true;
    }

    // ----------------------------------------------------------------------------------- crafting

    @Override
    protected void clearSelection() {
        this.resultSlot.set(ItemStack.EMPTY);
        this.resultContainer.setRecipeUsed(null);
    }

    @Override
    protected void selectOption(int index) {
        this.setupResultSlot(index);
    }

    private void setupResultSlot(int index) {
        if (this.level.isClientSide()) {
            return;
        }

        Option option = this.optionAt(index);
        if (option != null && option.requirement().isEmpty()) {
            this.resultContainer.setRecipeUsed(option.used());
            this.resultSlot.set(option.result().copy());
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
            this.resultContainer.setRecipeUsed(null);
        }

        this.broadcastChanges();
    }

    private @Nullable Option optionAt(int index) {
        List<Option> options = this.options();
        return index >= 0 && index < options.size() ? options.get(index) : null;
    }

    /**
     * Spends what the craft just taken cost: a base always, and a paint unless it was a family shape.
     * Cutting a painted block into its slabs is shaping, not painting, so it costs no paint.
     */
    private void spendIngredients() {
        Option option = this.optionAt(this.getSelectedRecipeIndex());
        boolean variant = option != null && option.variant();

        this.baseSlot.remove(1);
        if (!variant) {
            this.paintSlot.remove(1);
        }

        if (!this.baseSlot.getItem().isEmpty() && (variant || !this.paintSlot.getItem().isEmpty())) {
            this.setupResultSlot(this.getSelectedRecipeIndex());
        } else {
            this.setupResultSlot(-1);
        }
    }

    // -------------------------------------------------------------------------------- odds & ends

    /** Shift-clicking sorts an item into whichever of the two slots can use it. */
    @Override
    protected boolean moveIntoStation(ItemStack stack) {
        List<RecipeHolder<PaintingRecipe>> all =
                StationRecipes.allOf(this.level, this.recipeType(), this::accepts);

        for (RecipeHolder<PaintingRecipe> holder : all) {
            if (holder.value().matchesBase(stack)
                    && this.moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return true;
            }
        }
        for (RecipeHolder<PaintingRecipe> holder : all) {
            if (holder.value().matchesPaint(stack)
                    && this.moveItemStackTo(stack, PAINT_SLOT, PAINT_SLOT + 1, false)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(RESULT_SLOT);
        this.clearContainer(player, this.container);
    }

    @Override
    public MenuType<?> getType() {
        return PaintersKit.MENU;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem().is(PaintersKit.item())
                || player.getOffhandItem().is(PaintersKit.item());
    }
}
