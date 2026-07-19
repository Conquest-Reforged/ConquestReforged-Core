package com.conquestrefabricated.core.block.builder;

import com.conquestrefabricated.api.tags.ModTags;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.group.ModGroups;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class VanillaProps {

    public static Props stone() {
        return Props.create(Blocks.STONE).getProps().tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.STONE).group((ModGroups.UTILITY));
    }

    public static Props cobblestone() {
        return Props.create(Blocks.STONE).getProps().tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.COBBLESTONES).group((ModGroups.UTILITY));
    }

    public static Props naturalStone() {
        return Props.create(Blocks.STONE).getProps().tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.NATURALSTONES).group((ModGroups.UTILITY));
    }

    public static Props plaster() {
        return Props.create(Blocks.STONE).getProps().tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.PLASTER).group((ModGroups.UTILITY));
    }

    public static Props mosaic() {
        return Props.create(Blocks.GRAY_GLAZED_TERRACOTTA).tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.MOSAIC).group((ModGroups.UTILITY));
    }

    public static Props wood() {
        return planks().tags(BlockTags.MINEABLE_WITH_AXE).group((ModGroups.UTILITY));
    }

    public static Props woodLike() {
        return Props.create(Blocks.OAK_WOOD).group((ModGroups.UTILITY));
    }

    public static Props bricks() {
        return Props.create(Blocks.BRICKS).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.BRICKS);
    }

    public static Props planks() {
        return Props.create(Blocks.OAK_PLANKS).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_AXE, BlockTags.PLANKS);
    }

    //TODO taking props from logs block forces axis prop...recreate log material -- OAK PLANKS PLACEHOLDER
    public static Props logs() {
        return Props.create(Blocks.OAK_PLANKS).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS);
    }

    public static Props sand() {
        return Props.create(Blocks.SAND).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public static Props gravel() {
        return Props.create(Blocks.GRAVEL).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public static Props grass() {
        return Props.create(Blocks.GRASS_BLOCK).group((ModGroups.UTILITY)).grassColor().tags(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public static Props grassLike() {
        return Props.create(Blocks.DIRT).group((ModGroups.UTILITY)).strength(0.6F, 0.6F).sound(SoundType.GRASS).grassColor().offset(BlockBehaviour.OffsetType.NONE).tags(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public static Props glass() {
        return Props.create(Blocks.GLASS).group((ModGroups.UTILITY));
    }

    public static Props cloth() {
        return Props.create(Blocks.WHITE_WOOL).group((ModGroups.UTILITY));
    }

    public static Props plants() {
        return Props.create(Blocks.SHORT_GRASS).group((ModGroups.UTILITY)).strength(0.0D, 0.0D).offset(BlockBehaviour.OffsetType.XYZ).customOffsetType(CustomOffsetType.PLANT_XYZ).dynamicBounds(true);
    }

    public static Props hangingPlants() {
        return Props.create(Blocks.SHORT_GRASS).group((ModGroups.UTILITY)).strength(0.0D, 0.0D).offset(BlockBehaviour.OffsetType.XYZ);
    }

    public static Props plantsPlain() {
        return Props.create(Blocks.SHORT_GRASS).group((ModGroups.UTILITY)).strength(0.0D, 0.0D);
    }


    public static Props flowers() {
        return Props.create(Blocks.SHORT_GRASS).group((ModGroups.UTILITY)).strength(0.0D, 0.0D).offset(BlockBehaviour.OffsetType.XZ).customOffsetType(CustomOffsetType.PLANT_XZ).dynamicBounds(true);
    }

    public static Props saplings() {
        return Props.create(Blocks.CHERRY_SAPLING).group((ModGroups.UTILITY)).strength(0.0D, 0.0D).offset(BlockBehaviour.OffsetType.XYZ).customOffsetType(CustomOffsetType.PLANT_XYZ).dynamicBounds(true);
    }

    public static Props crops() {
        return Props.create(Blocks.WHEAT).group((ModGroups.UTILITY)).strength(0.0D, 0.0D).offset(BlockBehaviour.OffsetType.XZ).customOffsetType(CustomOffsetType.PLANT_XZ).dynamicBounds(true).tags(BlockTags.CROPS);
    }

    public static Props beetroot() {
        return Props.create(Blocks.BEETROOTS).group((ModGroups.UTILITY)).strength(0.0D, 0.0D).offset(BlockBehaviour.OffsetType.XZ).customOffsetType(CustomOffsetType.PLANT_XZ).dynamicBounds(true).tags(BlockTags.CROPS);
    }

    public static Props earth() {
        return Props.create(Blocks.DIRT).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.DIRT);
    }

    // blocks movement - not sure what is desirable
    public static Props plantLike() {
        return Props.create(Blocks.OAK_PLANKS).sound(SoundType.GRASS).group((ModGroups.UTILITY));
    }

    public static Props ice() {
        return Props.create(Blocks.PACKED_ICE).group((ModGroups.UTILITY));
    }

    public static Props metal() {
        return Props.create(Blocks.IRON_BLOCK).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.METAL);
    }

    public static Props leaves() {
        return Props.create(Blocks.OAK_LEAVES).group((ModGroups.UTILITY)).tags(BlockTags.LEAVES);
    }

    public static Props leafLike() {
        return Props.create(Blocks.SHORT_GRASS).group((ModGroups.UTILITY)).tags(BlockTags.LEAVES);
    }

    public static Props bamboo() {
        return Props.create(Blocks.BAMBOO).group((ModGroups.UTILITY));
    }

    public static Props chain() {
        return Props.create(Blocks.IRON_CHAIN).group((ModGroups.UTILITY)).tags(BlockTags.MINEABLE_WITH_PICKAXE);
    }


    /**
     * Stone props but uses grass color
     */
    public static Props grassyStone() {
        return stone().grassColor().tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.COBBLESTONES);
    }

    /**
     * Earth props but uses grass color
     */
    public static Props grassyEarth() {
        return earth().grassColor().tags(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.DIRT);
    }

    /**
     * Sand props but uses grass color
     */
    public static Props grassySand() {
        return sand().grassColor().tags(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.SAND);
    }

    /**
     * Gravel props but uses grass color
     */
    public static Props grassyGravel() {
        return gravel().grassColor().tags(BlockTags.MINEABLE_WITH_SHOVEL, ModTags.GRAVELS);
    }
}
