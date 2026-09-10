package com.conquestrefabricated.content.lime;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The lime cycle: burn a calcareous stone for quicklime, slake it with water, cut it with sand.
 *
 * <p>Three plain items, but the middle step has a second route. Quicklime slakes on contact with
 * water, so dropping it in a pond turns it into slaked lime where it floats - see
 * {@link #slakeInWater}, which each loader's {@code ItemEntityMixin} calls once a tick.</p>
 *
 * <p>What burns down to quicklime is {@code ModTags.LIME_SOURCES}, the union of the calcareous
 * natural stones and vanilla calcite, so a module adding its own chalk joins the recipe by tagging
 * the block and nothing else.</p>
 */
public final class Lime {

    public static final Identifier QUICKLIME_ID = Namespaces.id("quicklime");
    public static final Identifier SLAKED_LIME_ID = Namespaces.id("slaked_lime");
    public static final Identifier LIME_PLASTER_ID = Namespaces.id("lime_plaster");

    public static final ResourceKey<Item> QUICKLIME_KEY = ResourceKey.create(Registries.ITEM, QUICKLIME_ID);
    public static final ResourceKey<Item> SLAKED_LIME_KEY = ResourceKey.create(Registries.ITEM, SLAKED_LIME_ID);
    public static final ResourceKey<Item> LIME_PLASTER_KEY = ResourceKey.create(Registries.ITEM, LIME_PLASTER_ID);

    /** Set during item registration. */
    public static Item QUICKLIME;
    public static Item SLAKED_LIME;
    public static Item LIME_PLASTER;

    private Lime() {
    }

    /** Builds the items. Call only while the item registry is open, then register the results. */
    public static Item createQuicklime() {
        QUICKLIME = new Item(new Item.Properties().setId(QUICKLIME_KEY));
        return QUICKLIME;
    }

    public static Item createSlakedLime() {
        SLAKED_LIME = new Item(new Item.Properties().setId(SLAKED_LIME_KEY));
        return SLAKED_LIME;
    }

    public static Item createLimePlaster() {
        LIME_PLASTER = new Item(new Item.Properties().setId(LIME_PLASTER_KEY));
        return LIME_PLASTER;
    }

    /**
     * Slakes dropped quicklime the moment it touches water, hissing as it goes.
     *
     * <p>Called every tick for every item entity in the world, so it gets out of the way as early as
     * it can: the common case is an item that is neither quicklime nor wet.</p>
     */
    public static void slakeInWater(ItemEntity entity) {
        if (QUICKLIME == null || SLAKED_LIME == null) {
            // Registration has not happened yet, which can only be true before any world exists.
            return;
        }

        ItemStack stack = entity.getItem();
        if (!stack.is(QUICKLIME) || !entity.isInWater()) {
            return;
        }

        Level level = entity.level();
        if (!(level instanceof ServerLevel server)) {
            return;
        }

        entity.setItem(new ItemStack(SLAKED_LIME, stack.getCount()));
        server.playSound(null, entity.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.0F);
        server.sendParticles(ParticleTypes.CLOUD,
                entity.getX(), entity.getY() + 0.2, entity.getZ(), 8, 0.15, 0.1, 0.15, 0.02);
    }
}
