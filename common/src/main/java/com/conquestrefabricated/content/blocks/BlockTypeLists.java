package com.conquestrefabricated.content.blocks;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.block.arch.ArchSmall;
import com.conquestrefabricated.content.blocks.block.arch.ArchSmallHalf;
import com.conquestrefabricated.content.blocks.block.arch.ArchTwoMeter;
import com.conquestrefabricated.content.blocks.block.arch.ArchTwoMeterHalf;
import com.conquestrefabricated.content.blocks.block.beam.*;
import com.conquestrefabricated.content.blocks.block.classical.*;
import com.conquestrefabricated.content.blocks.block.decor.DoorFrameLintels;
import com.conquestrefabricated.content.blocks.block.decor.DoorFramePost;
import com.conquestrefabricated.content.blocks.block.decor.Lintels;
import com.conquestrefabricated.content.blocks.block.decor.Posts;
import com.conquestrefabricated.content.blocks.block.overlay_tinted.TintedOverlayLayer;
import com.conquestrefabricated.content.blocks.block.overlay_tinted.TintedOverlayStairs;
import com.conquestrefabricated.content.blocks.block.overlay_top.inverted.*;
import com.conquestrefabricated.content.blocks.block.overlay_wall.*;
import com.conquestrefabricated.content.blocks.block.topography.Rocks;
import com.conquestrefabricated.content.blocks.block.trees.*;
import com.conquestrefabricated.content.blocks.block.tudor.TudorVerticalCorner;
import com.conquestrefabricated.content.blocks.block.tudor.TudorVerticalQuarter;
import com.conquestrefabricated.content.blocks.block.tudor.TudorVerticalSlab;
import com.conquestrefabricated.content.blocks.block.uniquetexture.BeamPillar;
import com.conquestrefabricated.content.blocks.block.uniquetexture.WallUniqueTexture;
import com.conquestrefabricated.content.blocks.block.windows.ArrowSlit;
import com.conquestrefabricated.content.blocks.block.windows.WindowSmall;
import com.conquestrefabricated.content.blocks.block.windows.WindowSmallHalf;
import com.conquestrefabricated.core.block.factory.TypeList;

public class BlockTypeLists {
    public static TypeList refinedStoneCobbleBrickShapes = TypeList.of(Cube.class,
            ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class,
            ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class,
            Balustrade.class, Capital.class, Sphere.class,
            Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
            VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
            Stairs.class, WallNew.class, Pillar.class);
    public static TypeList refinedStoneCobbleBrickShapesVanilla = refinedStoneCobbleBrickShapes.removeAll(Cube.class, Stairs.class);
    public static TypeList refinedStoneCobbleBrickShapesVanillaNoWall = refinedStoneCobbleBrickShapesVanilla.remove(WallNew.class);
    public static TypeList refinedStoneCobbleBrickShapesTopOverlay = TypeList.of(TopOverlayInvertedCube.class, ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class, TopOverlayInvertedArrowSlit.class, TopOverlayInvertedWindowSmall.class, TopOverlayInvertedWindowSmallHalf.class, TopOverlayInvertedBalustrade.class, TopOverlayInvertedCapital.class, Sphere.class, TopOverlayInvertedSlab.class, TopOverlayInvertedSlabQuarter.class, TopOverlayInvertedSlabCorner.class, TopOverlayInvertedSlabEighth.class, TopOverlayInvertedVerticalSlabCorner.class, TopOverlayInvertedVerticalSlab.class, TopOverlayInvertedVerticalCorner.class, TopOverlayInvertedVerticalQuarter.class, TopOverlayInvertedStairs.class, WallNew.class, TopOverlayInvertedPillar.class);

    public static TypeList roadShapes = TypeList.of(Cube.class,
            Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
            VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
            Stairs.class);

    public static TypeList largeStoneSlabShapes = TypeList.of(Cube.class,
            ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class,
            ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class,
            Balustrade.class, Capital.class,
            Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
            VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
            Stairs.class, WallNew.class, Pillar.class);
    public static TypeList largeStoneSlabShapesVanilla = largeStoneSlabShapes.remove(Cube.class);
    public static TypeList largeStoneSlabShapesVanillaNoStairs = largeStoneSlabShapesVanilla.remove(Stairs.class);

    public static TypeList wallCarvingsDesignsShapes = TypeList.of(Cube.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, WallNew.class, Pillar.class);
    public static TypeList wallCarvingsDesignsNoWallShapes = wallCarvingsDesignsShapes.removeAll(WallNew.class, Pillar.class);
    public static TypeList wallCarvingsDesignsNoWallOverlayShapes = TypeList.of(OverlayCube.class, OverlayVerticalSlab.class, OverlayVerticalCorner.class, OverlayVerticalQuarter.class);
    public static TypeList wallCarvingsDesignsPillarOverlayShapes = wallCarvingsDesignsNoWallOverlayShapes.add(OverlayPillar.class);

    public static TypeList tudorShapes = TypeList.of(Cube.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class);
    public static TypeList tudorSlashShapes = TypeList.of(Cube.class, TudorVerticalSlab.class, TudorVerticalCorner.class, TudorVerticalQuarter.class);

    public static TypeList columnShapes = TypeList.of(Cube.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, WallNew.class, Pillar.class);
    public static TypeList columnShapesVanilla = columnShapes.remove(Cube.class);
    public static TypeList columnShapes2 = columnShapes.replace(WallNew.class, WallColumn.class);
    public static TypeList columnShapesLog = columnShapes2.replace(Cube.class, Log.class);
    public static TypeList columnDoricCapitalShapes = TypeList.of(CubeCapital.class, VerticalSlabCapital.class, VerticalCornerCapital.class, VerticalQuarterCapital.class, PillarCapital.class, WallCapital.class);
    public static TypeList columnDoricBaseShapes = TypeList.of(CubeBase.class, VerticalSlabBase.class, VerticalCornerBase.class, VerticalQuarterBase.class, PillarBase.class, WallBase.class);

    public static TypeList plasterShapes = TypeList.of(Cube.class,
            ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class,
            ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class,
            Balustrade.class, Capital.class, Sphere.class,
            SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
            VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
            Stairs.class, WallNew.class, Pillar.class);
    public static TypeList plasterShapesVanilla = plasterShapes.remove(Cube.class);

    public static TypeList floorCeilingPatternShapes = TypeList.of(Cube.class,
            Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
            VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class,
            Stairs.class);
    public static TypeList floorCeilingPatternShapesVanilla = floorCeilingPatternShapes.remove(Cube.class);

    public static TypeList strippedLogShapes = TypeList.of(Log.class, Bark.class, WallUniqueTexture.class, BeamPillar.class, BeamSlabLessLayers.class, BeamSlabQuarter.class, BeamSlabCorner.class, BeamSlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class, BeamHorizontal.class, BeamVertical.class, DoorFrameLintels.class, DoorFramePost.class, Lintels.class, Posts.class, BeamBoards.class);
    public static TypeList strippedLogVanillaShapes = strippedLogShapes.removeAll(Log.class, Bark.class);

    public static TypeList logShapes = TypeList.of(Log.class, Bark.class, BranchLarge.class, LogPillar.class, Branch.class, BranchSmall.class, Stump.class, SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, LogVerticalQuarter.class, LogVerticalQuarterStump.class, Stairs.class);
    public static TypeList logShapesVanilla = logShapes.removeAll(Log.class, Bark.class);

    public static TypeList planksShapes = TypeList.of(Cube.class,
            WindowSmall.class, WindowSmallHalf.class,
            Balustrade.class, Capital.class, Sphere.class,
            SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
            VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
            Stairs.class, WallNew.class, Pillar.class);
    public static TypeList planksVerticalShapes = planksShapes.add(BoardsVertical.class);
    public static TypeList planksVerticalCrossFenceShapes = planksVerticalShapes.add(FenceCross.class);
    public static TypeList planksHorizontalShapes = planksShapes.add(BoardsHorizontal.class);

    public static TypeList thatchShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, WallNew.class, Pillar.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class, Capital.class, Bit.class);
    public static TypeList clothShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, Stairs.class);
    public static TypeList clothShapesVanilla = clothShapes.remove(Cube.class);

    public static TypeList roughNaturalRockShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class);
    public static TypeList roughNaturalRockShapesVanilla = roughNaturalRockShapes.remove(Cube.class);
    public static TypeList roughNaturalRockShapesRocks = roughNaturalRockShapes.add(Rocks.class);
    public static TypeList roughNaturalRockShapesRocksVanilla = roughNaturalRockShapesVanilla.add(Rocks.class);

    public static TypeList smoothNaturalRockShapes = TypeList.of(Cube.class, ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class, Balustrade.class, Capital.class, Sphere.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class, WallNew.class, Pillar.class);
    public static TypeList smoothNaturalRockShapesRocks = smoothNaturalRockShapes.add(Rocks.class);
    public static TypeList smoothNaturalRockShapesVanilla = smoothNaturalRockShapesRocks.removeAll(Cube.class, Stairs.class);
    public static TypeList smoothNaturalRockShapesVanillaNoWall = smoothNaturalRockShapesVanilla.remove(WallNew.class);

    public static TypeList sandGravelShapes = TypeList.of(Cube.class, Layer.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, Stairs.class);
    public static TypeList sandGravelShapesVanilla = sandGravelShapes.remove(Cube.class);
    public static TypeList sandGravelShapesOverlay = TypeList.of(TintedOverlayLayer.class, TintedOverlayStairs.class);

    public static TypeList grassGroundShapes = TypeList.of(Cube.class, Layer.class, Stairs.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class);

    public static TypeList dirtShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, Stairs.class);
    public static TypeList dirtShapesVanilla = dirtShapes.remove(Cube.class);

    public static TypeList roofTileShapes = TypeList.of(Cube.class, SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, WallNew.class, Pillar.class, Capital.class, Stairs.class);
    public static TypeList roofTileShapesVanilla = roofTileShapes.remove(Cube.class);

}
