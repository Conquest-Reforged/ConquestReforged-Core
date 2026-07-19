package com.conquestrefabricated.content.blocks.init;


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

public class BlockRegistrar {

    //@SubscribeEvent
    public static void blocks() {
        com.conquestrefabricated.core.util.log.Log.info("Registering blocks");
        init();
    }

    //@SubscribeEvent
    public static void items() {
        com.conquestrefabricated.core.util.log.Log.info("Registering block items");
    }

    private static void init() {
        TypeList refinedStoneCobbleBrickShapes = TypeList.of(Cube.class,
                ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class,
                ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class,
                Balustrade.class, Capital.class, Sphere.class,
                Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
                VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
                Stairs.class, WallNew.class, Pillar.class);
        TypeList refinedStoneCobbleBrickShapesVanilla = refinedStoneCobbleBrickShapes.removeAll(Cube.class, Stairs.class);
        TypeList refinedStoneCobbleBrickShapesVanillaNoWall = refinedStoneCobbleBrickShapesVanilla.remove(WallNew.class);
        TypeList refinedStoneCobbleBrickShapesTopOverlay = TypeList.of(TopOverlayInvertedCube.class, ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class, TopOverlayInvertedArrowSlit.class, TopOverlayInvertedWindowSmall.class, TopOverlayInvertedWindowSmallHalf.class, TopOverlayInvertedBalustrade.class, TopOverlayInvertedCapital.class, Sphere.class, TopOverlayInvertedSlab.class, TopOverlayInvertedSlabQuarter.class, TopOverlayInvertedSlabCorner.class, TopOverlayInvertedSlabEighth.class, TopOverlayInvertedVerticalSlabCorner.class, TopOverlayInvertedVerticalSlab.class, TopOverlayInvertedVerticalCorner.class, TopOverlayInvertedVerticalQuarter.class, TopOverlayInvertedStairs.class, WallNew.class, TopOverlayInvertedPillar.class);

        TypeList roadShapes = TypeList.of(Cube.class,
                Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
                VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
                Stairs.class);

        TypeList largeStoneSlabShapes = TypeList.of(Cube.class,
                ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class,
                ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class,
                Balustrade.class, Capital.class,
                Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
                VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
                Stairs.class, WallNew.class, Pillar.class);
        TypeList largeStoneSlabShapesVanilla = largeStoneSlabShapes.remove(Cube.class);
        TypeList largeStoneSlabShapesVanillaNoStairs = largeStoneSlabShapesVanilla.remove(Stairs.class);

        TypeList wallCarvingsDesignsShapes = TypeList.of(Cube.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, WallNew.class, Pillar.class);
        TypeList wallCarvingsDesignsNoWallShapes = wallCarvingsDesignsShapes.removeAll(WallNew.class, Pillar.class);
        TypeList wallCarvingsDesignsNoWallOverlayShapes = TypeList.of(OverlayCube.class, OverlayVerticalSlab.class, OverlayVerticalCorner.class, OverlayVerticalQuarter.class);
        TypeList wallCarvingsDesignsPillarOverlayShapes = wallCarvingsDesignsNoWallOverlayShapes.add(OverlayPillar.class);

        TypeList tudorShapes = TypeList.of(Cube.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class);
        TypeList tudorSlashShapes = TypeList.of(Cube.class, TudorVerticalSlab.class, TudorVerticalCorner.class, TudorVerticalQuarter.class);

        TypeList columnShapes = TypeList.of(Cube.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, WallNew.class, Pillar.class);
        TypeList columnShapesVanilla = columnShapes.remove(Cube.class);
        TypeList columnShapes2 = columnShapes.replace(WallNew.class, WallColumn.class);
        TypeList columnShapesLog = columnShapes2.replace(Cube.class, Log.class);
        TypeList columnDoricCapitalShapes = TypeList.of(CubeCapital.class, VerticalSlabCapital.class, VerticalCornerCapital.class, VerticalQuarterCapital.class, PillarCapital.class, WallCapital.class);
        TypeList columnDoricBaseShapes = TypeList.of(CubeBase.class, VerticalSlabBase.class, VerticalCornerBase.class, VerticalQuarterBase.class, PillarBase.class, WallBase.class);

        TypeList plasterShapes = TypeList.of(Cube.class,
                ArchSmall.class, ArchSmallHalf.class, ArchTwoMeter.class, ArchTwoMeterHalf.class,
                ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class,
                Balustrade.class, Capital.class, Sphere.class,
                SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
                VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
                Stairs.class, WallNew.class, Pillar.class);
        TypeList plasterShapesVanilla = plasterShapes.remove(Cube.class);

        TypeList floorCeilingPatternShapes = TypeList.of(Cube.class,
                Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
                VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class,
                Stairs.class);
        TypeList floorCeilingPatternShapesVanilla = floorCeilingPatternShapes.remove(Cube.class);

        TypeList strippedLogShapes = TypeList.of(Log.class, Bark.class, WallUniqueTexture.class, BeamPillar.class, BeamSlabLessLayers.class, BeamSlabQuarter.class, BeamSlabCorner.class, BeamSlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class, BeamHorizontal.class, BeamVertical.class, DoorFrameLintels.class, DoorFramePost.class, Lintels.class, Posts.class, BeamBoards.class);
        TypeList strippedLogVanillaShapes = strippedLogShapes.removeAll(Log.class, Bark.class);

        TypeList logShapes = TypeList.of(Log.class, Bark.class, BranchLarge.class, LogPillar.class, Branch.class, BranchSmall.class, Stump.class, SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, LogVerticalQuarter.class, LogVerticalQuarterStump.class, Stairs.class);
        TypeList logShapesVanilla = logShapes.removeAll(Log.class, Bark.class);

        TypeList planksShapes = TypeList.of(Cube.class,
                WindowSmall.class, WindowSmallHalf.class,
                Balustrade.class, Capital.class, Sphere.class,
                SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class,
                VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class,
                Stairs.class, WallNew.class, Pillar.class);
        TypeList planksVerticalShapes = planksShapes.add(BoardsVertical.class);
        TypeList planksVerticalCrossFenceShapes = planksVerticalShapes.add(FenceCross.class);
        TypeList planksHorizontalShapes = planksShapes.add(BoardsHorizontal.class);

        TypeList thatchShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, WallNew.class, Pillar.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class, Capital.class, Bit.class);
        TypeList clothShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, Stairs.class);
        TypeList clothShapesVanilla = clothShapes.remove(Cube.class);

        TypeList roughNaturalRockShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class);
        TypeList roughNaturalRockShapesVanilla = roughNaturalRockShapes.remove(Cube.class);
        TypeList roughNaturalRockShapesRocks = roughNaturalRockShapes.add(Rocks.class);
        TypeList roughNaturalRockShapesRocksVanilla = roughNaturalRockShapesVanilla.add(Rocks.class);

        TypeList smoothNaturalRockShapes = TypeList.of(Cube.class, ArrowSlit.class, WindowSmall.class, WindowSmallHalf.class, Balustrade.class, Capital.class, Sphere.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlabExtendable.class, VerticalCornerExtendable.class, VerticalQuarterExtendable.class, Stairs.class, WallNew.class, Pillar.class);
        TypeList smoothNaturalRockShapesRocks = smoothNaturalRockShapes.add(Rocks.class);
        TypeList smoothNaturalRockShapesVanilla = smoothNaturalRockShapesRocks.removeAll(Cube.class, Stairs.class);
        TypeList smoothNaturalRockShapesVanillaNoWall = smoothNaturalRockShapesVanilla.remove(WallNew.class);

        TypeList sandGravelShapes = TypeList.of(Cube.class, Layer.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, Stairs.class);
        TypeList sandGravelShapesVanilla = sandGravelShapes.remove(Cube.class);
        TypeList sandGravelShapesOverlay = TypeList.of(TintedOverlayLayer.class, TintedOverlayStairs.class);

        TypeList grassGroundShapes = TypeList.of(Cube.class, Layer.class, Stairs.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class);

        TypeList dirtShapes = TypeList.of(Cube.class, Slab.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, Stairs.class);
        TypeList dirtShapesVanilla = dirtShapes.remove(Cube.class);

        TypeList roofTileShapes = TypeList.of(Cube.class, SlabLessLayers.class, SlabQuarter.class, SlabCorner.class, SlabEighth.class, VerticalSlabCorner.class, VerticalSlab.class, VerticalCorner.class, VerticalQuarter.class, WallNew.class, Pillar.class, Capital.class, Stairs.class);
        TypeList roofTileShapesVanilla = roofTileShapes.remove(Cube.class);

        //================================================================================================================================================================
        // Registering blocks
        //================================================================================================================================================================
//        RefinedStoneCobbleBrickInit.init(refinedStoneCobbleBrickShapes, refinedStoneCobbleBrickShapesVanilla, refinedStoneCobbleBrickShapesVanillaNoWall, refinedStoneCobbleBrickShapesTopOverlay);
//        LargeStoneSlabsInit.init(largeStoneSlabShapes, largeStoneSlabShapesVanilla, largeStoneSlabShapesVanillaNoStairs);
//        RoadsInit.init(roadShapes);
//        RoofTilesInit.init(roofTileShapes, roofTileShapesVanilla);
//        WallDesignsInit.init(wallCarvingsDesignsShapes);
//        WallDesignsNoWallInit.init(wallCarvingsDesignsNoWallShapes, wallCarvingsDesignsNoWallOverlayShapes, wallCarvingsDesignsPillarOverlayShapes);
//        TudorInit.init(tudorShapes, tudorSlashShapes);
//        ColumnsInit.init(columnShapes, columnDoricCapitalShapes, columnDoricBaseShapes, columnShapes2, columnShapesVanilla, columnShapesLog);
//        PlasterInit.init(plasterShapes, plasterShapesVanilla);
//        FloorCeilingPatternInit.init(floorCeilingPatternShapes, floorCeilingPatternShapesVanilla);
//        BeamsInit.init(strippedLogShapes, strippedLogVanillaShapes);
//        PlanksInit.init(planksShapes, planksHorizontalShapes, planksVerticalShapes, planksVerticalCrossFenceShapes);
//        LogsInit.init(logShapes, logShapesVanilla);
//        BranchesInit.init();
//        ThatchInit.init(thatchShapes);
//        WoolInit.init(clothShapesVanilla);
//        ClothInit.init(clothShapes);
//        MetalInit.init();
//        RoughNaturalRockInit.init(roughNaturalRockShapes, roughNaturalRockShapesRocks, roughNaturalRockShapesVanilla, roughNaturalRockShapesRocksVanilla);
//        SmoothNaturalRockInit.init(smoothNaturalRockShapes, smoothNaturalRockShapesRocks, smoothNaturalRockShapesVanillaNoWall, smoothNaturalRockShapesVanilla);
//        SandGravelInit.init(sandGravelShapes, sandGravelShapesVanilla, sandGravelShapesOverlay);
//        GrassGroundInit.init(grassGroundShapes);
//        DirtInit.init(dirtShapes, dirtShapesVanilla);
//        IrregularBlocksInit.init();
//        MiscInit.init();
//        DoorsInit.init();
//        GlassInit.init();
//        ContainersInit.init();
//        LightsInit.init();
//        LeavesInit.init();
//        SaplingsInit.init();
//        PlantsInit.init();
//        CropsInit.init();
//        WasteInit.init();
//        AirInit.init();
//        ArchesInit.init();
//
//        AnimalsInit.init();
//        BedsInit.init();
//        ChairsInit.init();
//        FencesInit.init();
//        FoodsInit.init();
//        TablesInit.init();
//        TextilesInit.init();
//        ToolsInit.init();
    }
}