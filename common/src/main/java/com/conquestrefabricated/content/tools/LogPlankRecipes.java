package com.conquestrefabricated.content.tools;

import com.conquestrefabricated.api.tags.ModTags;
import com.conquestrefabricated.core.Namespaces;
import com.conquestrefabricated.core.block.builder.ToolRecipeSpec;
import com.conquestrefabricated.core.block.data.BlockData;
import net.minecraft.tags.BlockTags;
import com.conquestrefabricated.core.block.builder.RecipeIngredient;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Writes a woodworking recipe turning each log into its planks ({@link LogPlanks}), unless a plank
 * block already declares one for that log through {@code Props.craftedWith}.
 */
public final class LogPlankRecipes {

    /** Planks per log, as with vanilla's. */
    public static final int PLANKS_PER_LOG = 4;

    private LogPlankRecipes() {
    }

    /** A log and the planks it is cut into. */
    public record Pair(Block log, Block planks) {
    }

    /** Every log this module registers that has planks, once per log. */
    public static List<Pair> pairs() {
        List<Pair> pairs = new ArrayList<>();
        Namespaces.stream().flatMap(BlockDataRegistry.getInstance()::getData).forEach(data -> {
            if (data.isFamilyParent()) {
                LogPlanks.planksFor(data.registryName).ifPresent(planks -> pairs.add(new Pair(data.getBlock(), planks)));
            }
        });
        return pairs;
    }

    /**
     * How many a tool recipe declared on {@code planks} yields: planks cut from a log give
     * {@link #PLANKS_PER_LOG} whatever the declaration said, anything else what it declared.
     */
    public static int yield(ToolRecipeSpec spec, BlockData planks) {
        if (spec.count() != 1 || !planks.getTags().contains(BlockTags.PLANKS)) {
            return spec.count();
        }
        boolean fromLog = switch (spec.ingredient()) {
            case RecipeIngredient.OfId id -> id.id().getPath().endsWith("_log");
            case RecipeIngredient.OfItem item -> BuiltInRegistries.ITEM.getKey(item.item().asItem()).getPath().endsWith("_log");
            case RecipeIngredient.OfItemTag tag -> tag.tag().equals(ModTags.LOG_BASES);
            default -> false;
        };
        return fromLog ? PLANKS_PER_LOG : spec.count();
    }

    /** @return how many recipes were written */
    public static int generate(RecipeOutput output) {
        BlockDataRegistry registry = BlockDataRegistry.getInstance();

        Set<String> declared = new HashSet<>();
        Namespaces.stream().flatMap(registry::getData).forEach(data ->
                data.getProps().getToolRecipe().ifPresent(spec -> {
                    Identifier input = null;
                    if (spec.ingredient() instanceof RecipeIngredient.OfId id) {
                        input = id.id();
                    } else if (spec.ingredient() instanceof RecipeIngredient.OfItem item) {
                        input = BuiltInRegistries.ITEM.getKey(item.item().asItem());
                    }
                    if (input != null) {
                        declared.add(input + ">" + data.registryName);
                    }
                }));

        int[] written = {0};
        Namespaces.stream().flatMap(registry::getData).forEach(data -> {
            if (!data.isFamilyParent()) {
                return;
            }
            Identifier log = data.registryName;
            Optional<Block> planks = LogPlanks.planksFor(log);
            if (planks.isEmpty()) {
                return;
            }
            Identifier plankId = BuiltInRegistries.BLOCK.getKey(planks.get());
            if (declared.contains(log + ">" + plankId)) {
                return;
            }
            ToolCraftingRecipeBuilder.toolCrafting(CraftingTools.WOODWORKING.id(), data.getBlock(), planks.get())
                    .count(PLANKS_PER_LOG)
                    .save(output, Identifier.fromNamespaceAndPath(log.getNamespace(),
                            plankId.getPath() + "_from_" + log.getPath() + "_tools"));
            written[0]++;
        });
        return written[0];
    }
}
