package com.conquestrefabricated.content.spoilage;

import com.conquestrefabricated.content.salt.Salt;
import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * How fast food goes off where it is: a multiple of the normal pace (1.0 is a temperate biome, in the
 * open, in an ordinary container).
 *
 * <p>The factors, multiplied together:</p>
 * <ul>
 *   <li><b>Temperature</b> of the biome, doubling for every 0.5 degrees of its base temperature above
 *       temperate - a desert is three or four times plains, a snowfield less than half.</li>
 *   <li><b>Place</b> - see {@link Place}. A cellar is cool and steady for most things; an attic is dry and
 *       aired for grain, which would rot in the damp of a cellar.</li>
 *   <li><b>Heat and cold</b> within two blocks: a lit fire speeds it, ice and snow slow it.</li>
 *   <li><b>Container</b>: a block in {@link #PRESERVING} halves it, one in {@link #AIRTIGHT} quarters it.
 *       An ordinary chest or shulker box does nothing - food does not stop ageing for being put away.</li>
 * </ul>
 *
 * <p>"Ground" is the terrain as generated, not what has since been built on it, so a floor of planks
 * raised on posts counts as raised and a pit dug into the hillside counts as below.</p>
 */
public final class SpoilageEnvironment {

    /** Containers that keep things somewhat better: ceramic jars, crocks, barrels. Half pace. */
    public static final TagKey<Block> PRESERVING = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "spoilage/preserving_containers"));

    /** Containers that seal things: salt barrels, sealed jars, ice boxes. Quarter pace. */
    public static final TagKey<Block> AIRTIGHT = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "spoilage/airtight_containers"));

    /** Ice, snow and the like: within two blocks, slows spoilage. */
    public static final TagKey<Block> COLD = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "spoilage/cold_blocks"));

    /** Where a store sits, as far as keeping food goes. */
    public enum Place {
        /** Under the sky. */
        OPEN,
        /** Under a roof at ground level. */
        ROOFED,
        /** Under a roof, three or more blocks above the ground. */
        ATTIC,
        /** Roofed and three or more blocks below the ground. */
        CELLAR
    }

    private static final int RAISED_BY = 3;
    private static final int BURIED_BY = 3;
    private static final double CELLAR_TEMPERATURE = 0.55;

    private SpoilageEnvironment() {
    }

    public static Place placeOf(Level level, BlockPos pos) {
        if (level.canSeeSky(pos.above())) {
            return Place.OPEN;
        }
        int ground = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
        if (pos.getY() <= ground - BURIED_BY) {
            return Place.CELLAR;
        }
        if (pos.getY() >= ground + RAISED_BY) {
            return Place.ATTIC;
        }
        return Place.ROOFED;
    }

    /**
     * The pace for something carried by a creature at {@code pos}: biome temperature and nearby heat and
     * cold, but no credit for where the carrier happens to be standing.
     */
    public static double carried(Level level, BlockPos pos) {
        return temperature(level, pos, level.getBiome(pos).value().getBaseTemperature()) * nearby(level, pos);
    }

    /** The pace for {@code kind} kept in the block at {@code pos}, which is a container or the floor. */
    public static double stored(Level level, BlockPos pos, SpoilClass kind, BlockState container) {
        Place place = placeOf(level, pos);
        double base = level.getBiome(pos).value().getBaseTemperature();
        if (place == Place.CELLAR) {
            base = Math.min(base, CELLAR_TEMPERATURE);
        }
        double rate = temperature(level, pos, base) * nearby(level, pos) * placeFactor(place, kind);
        if (container.is(AIRTIGHT)) {
            rate *= 0.25;
        } else if (container.is(PRESERVING)) {
            rate *= 0.5;
        }
        return rate;
    }

    private static double temperature(Level level, BlockPos pos, double base) {
        return Math.clamp(Math.pow(2.0, (base - 0.8) * 2.0), 0.2, 6.0);
    }

    private static double placeFactor(Place place, SpoilClass kind) {
        boolean grain = kind.prefersAttic();
        return switch (place) {
            case OPEN -> grain ? 1.2 : 1.0;
            case ROOFED -> grain ? 0.8 : 0.95;
            case ATTIC -> grain ? 0.4 : 1.15;
            case CELLAR -> grain ? 1.5 : 0.45;
        };
    }

    /** Heat or cold within two blocks. Fire wins over ice: a stove in a snowbank is still a stove. */
    private static double nearby(Level level, BlockPos pos) {
        boolean cold = false;
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
            if (Salt.isHeat(level, near)) {
                return 1.8;
            }
            cold |= level.getBlockState(near).is(COLD);
        }
        return cold ? 0.5 : 1.0;
    }
}
