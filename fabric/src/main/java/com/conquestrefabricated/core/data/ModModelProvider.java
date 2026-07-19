package com.conquestrefabricated.core.data;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.block.arch.ArchSmall;
import com.conquestrefabricated.content.blocks.block.arch.ArchSmallHalf;
import com.conquestrefabricated.content.blocks.block.arch.ArchTwoMeter;
import com.conquestrefabricated.content.blocks.block.arch.ArchTwoMeterHalf;
import com.conquestrefabricated.content.blocks.block.beam.BeamBoards;
import com.conquestrefabricated.content.blocks.block.decor.DoorFrameLintels;
import com.conquestrefabricated.content.blocks.block.decor.DoorFramePost;
import com.conquestrefabricated.content.blocks.block.decor.Lintels;
import com.conquestrefabricated.content.blocks.block.decor.Posts;
import com.conquestrefabricated.content.blocks.block.glass.Glass;
import com.conquestrefabricated.content.blocks.block.glass.Pane;
import com.conquestrefabricated.content.blocks.block.overlay_wall.OverlayPillar;
import com.conquestrefabricated.content.blocks.block.overlay_wall.OverlayVerticalCorner;
import com.conquestrefabricated.content.blocks.block.overlay_wall.OverlayVerticalQuarter;
import com.conquestrefabricated.content.blocks.block.overlay_wall.OverlayVerticalSlab;
import com.conquestrefabricated.content.blocks.block.tracery.GlassTracery;
import com.conquestrefabricated.content.blocks.block.trees.*;
import com.conquestrefabricated.content.blocks.block.uniquetexture.BeamPillar;
import com.conquestrefabricated.content.blocks.block.windows.ArrowSlit;
import com.conquestrefabricated.content.blocks.block.windows.WindowSmall;
import com.conquestrefabricated.content.blocks.block.windows.WindowSmallHalf;
import com.conquestrefabricated.core.block.base.DirectionalShape;
import com.conquestrefabricated.core.block.base.HorizontalDirectionalShape;
import com.conquestrefabricated.core.block.base.WaterloggedBidirectionalShape;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.block.data.ColorType;
import com.conquestrefabricated.core.block.properties.BidirectionalShape;
import com.conquestrefabricated.core.block.properties.CapitalDirection;
import com.conquestrefabricated.core.block.properties.ModdedWallShape;
import com.conquestrefabricated.core.block.properties.SphereShape;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.block.dispatch.multipart.Condition;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        //Get data from CR's BlockDataRegistry, so we can get each block's Props (specifically the texture paths)
        BlockDataRegistry.getInstance().getData("conquest").forEach(blockData -> {
            Identifier id = blockData.getRegistryName();
            Block block = BuiltInRegistries.BLOCK.get(id).get().value();
            //Some blocks have unique models, so we check to see which ones were marked as "manual" before proceeding
            if (!blockData.getProps().isManual()) {
                if (block instanceof Cube || block instanceof Glass || block instanceof GlassTracery) {
                    registerCube(blockStateModelGenerator, block, blockData);
                    return;
                }

                if (block instanceof Log) {
                    registerLog(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Bark) {
                    registerBark(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Cover) {
                    registerCover(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof ArchSmall) {
                    registerArchSmall(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof ArchSmallHalf) {
                    registerArchSmallHalf(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof ArchTwoMeter) {
                    registerArchTwoMeter(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof ArchTwoMeterHalf) {
                    registerArchTwoMeterHalf(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof ArrowSlit) {
                    registerArrowSlit(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof WindowSmall) {
                    registerWindowSmall(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof WindowSmallHalf) {
                    registerWindowSmallHalf(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Balustrade) {
                    registerBalustrade(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Capital) {
                    registerCapital(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Sphere) {
                    registerSphere(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Slab) {
                    registerSlab(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof SlabLessLayers) {
                    registerSlabLessLayers(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof SlabQuarter && id.getPath().contains("beam")) {
                    registerSlabQuarterBeam(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof SlabQuarter) {
                    registerSlabQuarter(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof SlabCorner) {
                    registerSlabCorner(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof SlabEighth) {
                    registerSlabEighth(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Bit) {
                    registerBit(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof VerticalSlabCorner) {
                    registerVerticalSlabCorner(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof VerticalSlab && !(block instanceof OverlayVerticalSlab)) {
                    registerVerticalSlab(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof VerticalCorner && !(block instanceof OverlayVerticalCorner)) {
                    registerVerticalCorner(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof VerticalQuarter && !(block instanceof OverlayVerticalQuarter)) {
                    registerVerticalQuarter(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof LogVerticalQuarter && !(block instanceof LogVerticalQuarterStump)) {
                    registerLogVerticalQuarter(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Stairs) {
                    registerStairs(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof PlatformHorizontal) {
                    registerPlatformHorizontal(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof WallNew) {
                    registerWalls(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BeamPillar) {
                    registerBeamPillar(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Pillar && !(block instanceof OverlayPillar)) {
                    registerPillar(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof LogPillar && !(block instanceof Stump)) {
                    registerLogPillar(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Stump) {
                    registerStump(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof LogVerticalQuarterStump) {
                    registerVerticalQuarterStump(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BeamBoards) {
                    registerBeamBoards(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BeamHorizontal) {
                    registerBeamHorizontal(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BeamVertical) {
                    registerBeamVertical(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof DoorFrameLintels) {
                    registerDoorFrameLintel(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof DoorFramePost) {
                    registerDoorFramePost(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Lintels) {
                    registerLintel(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Posts) {
                    registerPost(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Layer) {
                    registerLayer(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Branch) {
                    registerBranch(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BranchLarge && !(block instanceof BranchSmall) && !(block instanceof Branch)) {
                    registerBranchLarge(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BranchSmall) {
                    registerBranchSmall(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BoardsVertical) {
                    registerBoardsVertical(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof BoardsHorizontal) {
                    registerBoardsHorizontal(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof FenceCross) {
                    registerFenceCross(blockStateModelGenerator, block, blockData);
                    return;
                }
                if (block instanceof Pane) {
                    registerPane(blockStateModelGenerator, block, blockData);
                    return;
                } else {
                    if (blockData.getProps().getColorType() == ColorType.FOLIAGE) {
                        registerManualItemTinted(blockStateModelGenerator, block, ItemModelUtils.constantTint(-12012264));
                    } else if (blockData.getProps().getColorType() == ColorType.GRASS) {
                        registerManualItemTinted(blockStateModelGenerator, block, new GrassColorSource());
                    } else {
                        registerManualItem(blockStateModelGenerator, block);
                    }
                }
            } else {
                if (blockData.getProps().getColorType() == ColorType.FOLIAGE) {
                    registerManualItemTinted(blockStateModelGenerator, block, ItemModelUtils.constantTint(-12012264));
                } else if (blockData.getProps().getColorType() == ColorType.GRASS) {
                    registerManualItemTinted(blockStateModelGenerator, block, new GrassColorSource());
                } else {
                    registerManualItem(blockStateModelGenerator, block);
                }
            }
        });
    }

    private void registerTintedOrPlain(BlockModelGenerators generator, Block block, Identifier modelId, BlockData blockData) {
        if (blockData.getProps().getColorType() == ColorType.FOLIAGE) {
            generator.registerSimpleTintedItemModel(block, modelId, ItemModelUtils.constantTint(-12012264));
        } else if (blockData.getProps().getColorType() == ColorType.GRASS) {
            generator.registerSimpleTintedItemModel(block, modelId, new GrassColorSource());
        } else {
            generator.registerSimpleItemModel(block, modelId);
        }
    }

    private void registerManualItem(BlockModelGenerators generator, Block block) {
        Identifier modelId = ModelLocationUtils.getModelLocation(block); // conquest:block/<block_path>
        generator.registerSimpleItemModel(block, modelId);
    }

    private void registerManualItemTinted(BlockModelGenerators generator, Block block, ItemTintSource tint) {
        Identifier modelId = ModelLocationUtils.getModelLocation(block); // conquest:block/<block_path>
        generator.registerSimpleTintedItemModel(block, modelId, tint);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }

    private void registerBoardsVertical(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierShortThin = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_vertical_short_thin")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_short_thin", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierShortMid = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_vertical_short_mid")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_short_mid", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierShortWide = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_vertical_short_wide")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_short_wide", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLongThin = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_vertical_long_thin")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_long_thin", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLongMid = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_vertical_long_mid")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_long_mid", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLongWide = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_vertical_long_wide")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_long_wide", textureMap, blockStateModelGenerator.modelOutput);


        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLongThin, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, BoardsVertical.LENGTH, BoardsVertical.WIDTH)
                        // TOP half
                        .select(Half.TOP, 0, 1, BlockModelGenerators.plainVariant(identifierShortThin))
                        .select(Half.TOP, 0, 2, BlockModelGenerators.plainVariant(identifierShortMid))
                        .select(Half.TOP, 0, 3, BlockModelGenerators.plainVariant(identifierShortWide))
                        .select(Half.TOP, 1, 1, BlockModelGenerators.plainVariant(identifierLongThin))
                        .select(Half.TOP, 1, 2, BlockModelGenerators.plainVariant(identifierLongMid))
                        .select(Half.TOP, 1, 3, BlockModelGenerators.plainVariant(identifierLongWide))
                        .select(Half.BOTTOM, 0, 1, BlockModelGenerators.plainVariant(identifierShortThin).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 0, 2, BlockModelGenerators.plainVariant(identifierShortMid).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 0, 3, BlockModelGenerators.plainVariant(identifierShortWide).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 1, 1, BlockModelGenerators.plainVariant(identifierLongThin).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 1, 2, BlockModelGenerators.plainVariant(identifierLongMid).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 1, 3, BlockModelGenerators.plainVariant(identifierLongWide).with(OffsetVariantSetting.yOffset(-8)))
                )
                .with(BoardsHorizontal.createEastDefaultSymmetricalRotationStates()));
    }

    private void registerBoardsHorizontal(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierShortThin = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_horizontal_short_thin")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_short_thin", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierShortMid = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_horizontal_short_mid")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_short_mid", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierShortWide = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_horizontal_short_wide")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_short_wide", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLongThin = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_horizontal_long_thin")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_long_thin", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLongMid = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_horizontal_long_mid")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_long_mid", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLongWide = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_boards_horizontal_long_wide")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_long_wide", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLongThin, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, BoardsHorizontal.LENGTH, BoardsHorizontal.WIDTH)
                        .select(Half.TOP, 0, 1, BlockModelGenerators.plainVariant(identifierShortThin))
                        .select(Half.TOP, 0, 2, BlockModelGenerators.plainVariant(identifierShortMid))
                        .select(Half.TOP, 0, 3, BlockModelGenerators.plainVariant(identifierShortWide))
                        .select(Half.TOP, 1, 1, BlockModelGenerators.plainVariant(identifierLongThin))
                        .select(Half.TOP, 1, 2, BlockModelGenerators.plainVariant(identifierLongMid))
                        .select(Half.TOP, 1, 3, BlockModelGenerators.plainVariant(identifierLongWide))
                        .select(Half.BOTTOM, 0, 1, BlockModelGenerators.plainVariant(identifierShortThin).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 0, 2, BlockModelGenerators.plainVariant(identifierShortMid).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 0, 3, BlockModelGenerators.plainVariant(identifierShortWide).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 1, 1, BlockModelGenerators.plainVariant(identifierLongThin).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 1, 2, BlockModelGenerators.plainVariant(identifierLongMid).with(OffsetVariantSetting.yOffset(-8)))
                        .select(Half.BOTTOM, 1, 3, BlockModelGenerators.plainVariant(identifierLongWide).with(OffsetVariantSetting.yOffset(-8)))
                )
                .with(BoardsHorizontal.createEastDefaultSymmetricalRotationStates()));
    }

    private void registerPost(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierSingle = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_posts_single")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_single", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLeft = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_posts_left")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRight = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_posts_right")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSingle, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(DoorFramePost.TOGGLE)
                        .select(1, BlockModelGenerators.plainVariant(identifierSingle))
                        .select(2, BlockModelGenerators.plainVariant(identifierLeft))
                        .select(3, BlockModelGenerators.plainVariant(identifierRight))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerDoorFramePost(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierSingle = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_single")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_single", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLeft = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_left")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRight = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_right")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSingle, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(DoorFramePost.TOGGLE)
                        .select(1, BlockModelGenerators.plainVariant(identifierSingle))
                        .select(2, BlockModelGenerators.plainVariant(identifierLeft))
                        .select(3, BlockModelGenerators.plainVariant(identifierRight))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerLintel(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierSingleBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_single_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_single_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLeftBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_left_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRightBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_right_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierMiddleBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_middle_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_middle_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSingleTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_single_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_single_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLeftTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_left_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRightTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_right_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierMiddleTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_lintel_middle_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_middle_top", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSingleTop, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, DoorFrameLintels.TOGGLE)
                        .select(Half.TOP, 1, BlockModelGenerators.plainVariant(identifierSingleBottom))
                        .select(Half.TOP, 2, BlockModelGenerators.plainVariant(identifierLeftBottom))
                        .select(Half.TOP, 3, BlockModelGenerators.plainVariant(identifierRightBottom))
                        .select(Half.TOP, 4, BlockModelGenerators.plainVariant(identifierMiddleBottom))

                        .select(Half.BOTTOM, 1, BlockModelGenerators.plainVariant(identifierSingleTop))
                        .select(Half.BOTTOM, 2, BlockModelGenerators.plainVariant(identifierLeftTop))
                        .select(Half.BOTTOM, 3, BlockModelGenerators.plainVariant(identifierRightTop))
                        .select(Half.BOTTOM, 4, BlockModelGenerators.plainVariant(identifierMiddleTop))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }


    private void registerDoorFrameLintel(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierSingleBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_single_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_single_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLeftBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_left_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRightBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_right_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierMiddleBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_middle_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_middle_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSingleTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_single_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_single_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLeftTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_left_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRightTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_right_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierMiddleTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_door_frame_middle_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_middle_top", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSingleTop, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, DoorFrameLintels.TOGGLE)
                        .select(Half.TOP, 1, BlockModelGenerators.plainVariant(identifierSingleBottom))
                        .select(Half.TOP, 2, BlockModelGenerators.plainVariant(identifierLeftBottom))
                        .select(Half.TOP, 3, BlockModelGenerators.plainVariant(identifierRightBottom))
                        .select(Half.TOP, 4, BlockModelGenerators.plainVariant(identifierMiddleBottom))

                        .select(Half.BOTTOM, 1, BlockModelGenerators.plainVariant(identifierSingleTop))
                        .select(Half.BOTTOM, 2, BlockModelGenerators.plainVariant(identifierLeftTop))
                        .select(Half.BOTTOM, 3, BlockModelGenerators.plainVariant(identifierRightTop))
                        .select(Half.BOTTOM, 4, BlockModelGenerators.plainVariant(identifierMiddleTop))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerBeamVertical(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierVertical = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_vertical")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_beam_vertical", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_support")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_beam_support", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSupport3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_support_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_beam_support_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSupport1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_support_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_beam_support_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSupport2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_support_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_beam_support_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSupportBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_support_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_beam_support_bottom", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer2, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BeamVertical.TOGGLE)
                        .select(1, BlockModelGenerators.plainVariant(identifierVertical))
                        .select(2, BlockModelGenerators.plainVariant(identifierLayer2))
                        .select(3, BlockModelGenerators.plainVariant(identifierSupport3))
                        .select(4, BlockModelGenerators.plainVariant(identifierSupport1))
                        .select(5, BlockModelGenerators.plainVariant(identifierSupport2))
                        .select(6, BlockModelGenerators.plainVariant(identifierSupportBottom))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, BlockModelGenerators.NOP).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_90).select(Direction.WEST, BlockModelGenerators.Y_ROT_180).select(Direction.NORTH, BlockModelGenerators.Y_ROT_270)));
    }

    private void registerBeamHorizontal(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierNS = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_ns")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_ne")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_nse")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_nsew")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBottomNS = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_ns_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBottomNE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_ne_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBottomNSE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_nse_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse_bottom", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBottomNSEW = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_beam_horizontal_nsew_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew_bottom", textureMap, blockStateModelGenerator.modelOutput);

        registerTintedOrPlain(blockStateModelGenerator, block, identifierNS, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, BeamHorizontal.ACTIVATED, HorizontalDirectionalShape.DIRECTION)
                        .select(Half.TOP, 1, Direction.NORTH, BlockModelGenerators.plainVariant(identifierNS))
                        .select(Half.TOP, 1, Direction.EAST, BlockModelGenerators.plainVariant(identifierNS).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.TOP, 1, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierNS).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.TOP, 1, Direction.WEST, BlockModelGenerators.plainVariant(identifierNS).with(BlockModelGenerators.Y_ROT_270))

                        .select(Half.TOP, 2, Direction.NORTH, BlockModelGenerators.plainVariant(identifierNE))
                        .select(Half.TOP, 2, Direction.EAST, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.TOP, 2, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.TOP, 2, Direction.WEST, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_270))

                        .select(Half.TOP, 3, Direction.NORTH, BlockModelGenerators.plainVariant(identifierNSE))
                        .select(Half.TOP, 3, Direction.EAST, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.TOP, 3, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.TOP, 3, Direction.WEST, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_270))

                        .select(Half.TOP, 4, Direction.NORTH, BlockModelGenerators.plainVariant(identifierNSEW))
                        .select(Half.TOP, 4, Direction.EAST, BlockModelGenerators.plainVariant(identifierNSEW).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.TOP, 4, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierNSEW).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.TOP, 4, Direction.WEST, BlockModelGenerators.plainVariant(identifierNSEW).with(BlockModelGenerators.Y_ROT_270))


                        .select(Half.BOTTOM, 1, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBottomNS))
                        .select(Half.BOTTOM, 1, Direction.EAST, BlockModelGenerators.plainVariant(identifierBottomNS).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.BOTTOM, 1, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBottomNS).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.BOTTOM, 1, Direction.WEST, BlockModelGenerators.plainVariant(identifierBottomNS).with(BlockModelGenerators.Y_ROT_270))

                        .select(Half.BOTTOM, 2, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBottomNE))
                        .select(Half.BOTTOM, 2, Direction.EAST, BlockModelGenerators.plainVariant(identifierBottomNE).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.BOTTOM, 2, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBottomNE).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.BOTTOM, 2, Direction.WEST, BlockModelGenerators.plainVariant(identifierBottomNE).with(BlockModelGenerators.Y_ROT_270))

                        .select(Half.BOTTOM, 3, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBottomNSE))
                        .select(Half.BOTTOM, 3, Direction.EAST, BlockModelGenerators.plainVariant(identifierBottomNSE).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.BOTTOM, 3, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBottomNSE).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.BOTTOM, 3, Direction.WEST, BlockModelGenerators.plainVariant(identifierBottomNSE).with(BlockModelGenerators.Y_ROT_270))

                        .select(Half.BOTTOM, 4, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBottomNSEW))
                        .select(Half.BOTTOM, 4, Direction.EAST, BlockModelGenerators.plainVariant(identifierBottomNSEW).with(BlockModelGenerators.Y_ROT_90))
                        .select(Half.BOTTOM, 4, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBottomNSEW).with(BlockModelGenerators.Y_ROT_180))
                        .select(Half.BOTTOM, 4, Direction.WEST, BlockModelGenerators.plainVariant(identifierBottomNSEW).with(BlockModelGenerators.Y_ROT_270))

                ));

    }

    private void registerBeamBoards(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierBoard1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoard2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoard3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardFlat1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_flat_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_flat_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardFlat2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_flat_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_flat_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardFlat3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_flat_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_flat_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardLeft1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_left_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardLeft2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_left_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardLeft3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_left_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardRight1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_right_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardRight2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_right_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardRight3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_right_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardUp1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_up_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_up_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardUp2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_up_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_up_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBoardUp3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_board_up_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_up_3", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierBoard1, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BeamBoards.TOGGLE, BeamBoards.LAYERS, DirectionalShape.DIRECTION)
                        .select(1, 1, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat1))
                        .select(2, 1, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat1).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, 1, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat1).with(BlockModelGenerators.Y_ROT_180))
                        .select(4, 1, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat1).with(BlockModelGenerators.Y_ROT_270))

                        .select(1, 2, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat2))
                        .select(2, 2, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat2).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, 2, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat2).with(BlockModelGenerators.Y_ROT_180))
                        .select(4, 2, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat2).with(BlockModelGenerators.Y_ROT_270))

                        .select(1, 3, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat3))
                        .select(2, 3, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat3).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, 3, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat3).with(BlockModelGenerators.Y_ROT_180))
                        .select(4, 3, Direction.UP, BlockModelGenerators.plainVariant(identifierBoardFlat3).with(BlockModelGenerators.Y_ROT_270))


                        .select(1, 1, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat1).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, 1, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat1).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, 1, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat1).with(BlockModelGenerators.X_ROT_180))
                        .select(4, 1, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat1).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_90))

                        .select(1, 2, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat2).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, 2, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat2).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, 2, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat2).with(BlockModelGenerators.X_ROT_180))
                        .select(4, 2, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat2).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_90))

                        .select(1, 3, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat3).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, 3, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat3).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, 3, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat3).with(BlockModelGenerators.X_ROT_180))
                        .select(4, 3, Direction.DOWN, BlockModelGenerators.plainVariant(identifierBoardFlat3).with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_90))


                        .select(1, 1, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoard1))
                        .select(2, 1, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardLeft1))
                        .select(3, 1, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardUp1))
                        .select(4, 1, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardRight1))

                        .select(1, 2, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoard2))
                        .select(2, 2, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardLeft2))
                        .select(3, 2, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardUp2))
                        .select(4, 2, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardRight2))

                        .select(1, 3, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoard3))
                        .select(2, 3, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardLeft3))
                        .select(3, 3, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardUp3))
                        .select(4, 3, Direction.NORTH, BlockModelGenerators.plainVariant(identifierBoardRight3))


                        .select(1, 1, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoard1).with(BlockModelGenerators.Y_ROT_90))
                        .select(2, 1, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardLeft1).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, 1, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardUp1).with(BlockModelGenerators.Y_ROT_90))
                        .select(4, 1, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardRight1).with(BlockModelGenerators.Y_ROT_90))

                        .select(1, 2, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoard2).with(BlockModelGenerators.Y_ROT_90))
                        .select(2, 2, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardLeft2).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, 2, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardUp2).with(BlockModelGenerators.Y_ROT_90))
                        .select(4, 2, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardRight2).with(BlockModelGenerators.Y_ROT_90))

                        .select(1, 3, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoard3).with(BlockModelGenerators.Y_ROT_90))
                        .select(2, 3, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardLeft3).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, 3, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardUp3).with(BlockModelGenerators.Y_ROT_90))
                        .select(4, 3, Direction.EAST, BlockModelGenerators.plainVariant(identifierBoardRight3).with(BlockModelGenerators.Y_ROT_90))


                        .select(1, 1, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoard1).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, 1, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardLeft1).with(BlockModelGenerators.Y_ROT_180))
                        .select(3, 1, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardUp1).with(BlockModelGenerators.Y_ROT_180))
                        .select(4, 1, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardRight1).with(BlockModelGenerators.Y_ROT_180))

                        .select(1, 2, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoard2).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, 2, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardLeft2).with(BlockModelGenerators.Y_ROT_180))
                        .select(3, 2, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardUp2).with(BlockModelGenerators.Y_ROT_180))
                        .select(4, 2, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardRight2).with(BlockModelGenerators.Y_ROT_180))

                        .select(1, 3, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoard3).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, 3, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardLeft3).with(BlockModelGenerators.Y_ROT_180))
                        .select(3, 3, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardUp3).with(BlockModelGenerators.Y_ROT_180))
                        .select(4, 3, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierBoardRight3).with(BlockModelGenerators.Y_ROT_180))


                        .select(1, 1, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoard1).with(BlockModelGenerators.Y_ROT_270))
                        .select(2, 1, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardLeft1).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, 1, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardUp1).with(BlockModelGenerators.Y_ROT_270))
                        .select(4, 1, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardRight1).with(BlockModelGenerators.Y_ROT_270))

                        .select(1, 2, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoard2).with(BlockModelGenerators.Y_ROT_270))
                        .select(2, 2, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardLeft2).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, 2, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardUp2).with(BlockModelGenerators.Y_ROT_270))
                        .select(4, 2, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardRight2).with(BlockModelGenerators.Y_ROT_270))

                        .select(1, 3, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoard3).with(BlockModelGenerators.Y_ROT_270))
                        .select(2, 3, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardLeft3).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, 3, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardUp3).with(BlockModelGenerators.Y_ROT_270))
                        .select(4, 3, Direction.WEST, BlockModelGenerators.plainVariant(identifierBoardRight3).with(BlockModelGenerators.Y_ROT_270))
                ));

    }

    private void registerSphere(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, false);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();
        /*com.conquestrefabricated.core.asset.annotation.Model[] models = Sphere.class.getAnnotation(Assets.class).block();
        List<BlockStateVariant> stateVariants = new ArrayList<>();
        VariantsBlockStateSupplier blockStateSupplier = VariantsBlockStateSupplier.create(block);
        PropertiesMap propertiesMap = PropertiesMap.withValues(SphereShape.EGG, SphereShape.LARGE);
        BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(Sphere.TYPE).register(SphereShape,stateVariants);
        for (int i = 0; i <= models.length; i++) {
            Identifier modelIdentifier = new Model(Optional.of(new Identifier("conquest", models[i].template())), Optional.empty(), textureKeys.toArray(new TextureKey[textureKeys.size()])).upload(block, "", textureMap, blockStateModelGenerator.modelCollector);
            stateVariants.add(BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifier));
        }*/
        Identifier identifierSphere = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_sphere")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierDragonEgg = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_dragonegg")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_dragonegg", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSmallSphere = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_sphere_small")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_small", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierDragonEgg, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Sphere.TYPE)
                        .select(SphereShape.LARGE, BlockModelGenerators.plainVariant(identifierSphere))
                        .select(SphereShape.EGG, BlockModelGenerators.plainVariant(identifierDragonEgg))
                        .select(SphereShape.SMALL, BlockModelGenerators.plainVariant(identifierSmallSphere))));
    }

    private void registerVerticalSlab(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_slab_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_slab_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_slab_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_slab_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(VerticalSlab.LAYERS)
                        .select(1, BlockModelGenerators.plainVariant(identifierLayer1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, BlockModelGenerators.plainVariant(identifierLayer2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, BlockModelGenerators.plainVariant(identifierLayer3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, BlockModelGenerators.plainVariant(identifierLayer4).with(VariantMutator.UV_LOCK.withValue(true))))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerVerticalCorner(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(VerticalCorner.LAYERS)
                        .select(1, BlockModelGenerators.plainVariant(identifierLayer1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, BlockModelGenerators.plainVariant(identifierLayer2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, BlockModelGenerators.plainVariant(identifierLayer3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, BlockModelGenerators.plainVariant(identifierLayer4).with(VariantMutator.UV_LOCK.withValue(true))))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerVerticalQuarter(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(VerticalQuarter.LAYERS)
                        .select(1, BlockModelGenerators.plainVariant(identifierLayer1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, BlockModelGenerators.plainVariant(identifierLayer2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, BlockModelGenerators.plainVariant(identifierLayer3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, BlockModelGenerators.plainVariant(identifierLayer4).with(VariantMutator.UV_LOCK.withValue(true))))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerLogVerticalQuarter(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer5 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_3", textureMap, blockStateModelGenerator.modelOutput);

        Identifier identifierLayerDown1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_down_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_down_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_down_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_down_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown5 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_quarter_down_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_3", textureMap, blockStateModelGenerator.modelOutput);

        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(LogVerticalQuarter.LAYERS, LogVerticalQuarter.DOWN)
                        .select(1, false, BlockModelGenerators.plainVariant(identifierLayer1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, false, BlockModelGenerators.plainVariant(identifierLayer2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, false, BlockModelGenerators.plainVariant(identifierLayer3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, false, BlockModelGenerators.plainVariant(identifierLayer4).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(5, false, BlockModelGenerators.plainVariant(identifierLayer5).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, true, BlockModelGenerators.plainVariant(identifierLayerDown1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, true, BlockModelGenerators.plainVariant(identifierLayerDown2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, true, BlockModelGenerators.plainVariant(identifierLayerDown3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, true, BlockModelGenerators.plainVariant(identifierLayerDown4).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(5, true, BlockModelGenerators.plainVariant(identifierLayerDown5).with(VariantMutator.UV_LOCK.withValue(true))))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerPillar(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Pillar.LAYERS)
                        .select(1, BlockModelGenerators.plainVariant(identifierLayer2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, BlockModelGenerators.plainVariant(identifierLayer3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, BlockModelGenerators.plainVariant(identifierLayer4).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerLogPillar(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayerDown1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_6", textureMap, blockStateModelGenerator.modelOutput);

        Identifier identifierLayer1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(LogPillar.LAYERS, LogPillar.DOWN)
                        .select(1, false, BlockModelGenerators.plainVariant(identifierLayer2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, false, BlockModelGenerators.plainVariant(identifierLayer4).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, false, BlockModelGenerators.plainVariant(identifierLayer6).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, false, BlockModelGenerators.plainVariant(identifierLayer3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(5, false, BlockModelGenerators.plainVariant(identifierLayer1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, true, BlockModelGenerators.plainVariant(identifierLayerDown2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, true, BlockModelGenerators.plainVariant(identifierLayerDown4).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, true, BlockModelGenerators.plainVariant(identifierLayerDown6).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, true, BlockModelGenerators.plainVariant(identifierLayerDown3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(5, true, BlockModelGenerators.plainVariant(identifierLayerDown1).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerBeamPillar(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_pillar_down_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_6", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Stump.LAYERS, Stump.DOWN)
                        .select(1, false, BlockModelGenerators.plainVariant(identifierLayer2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, false, BlockModelGenerators.plainVariant(identifierLayer4).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, false, BlockModelGenerators.plainVariant(identifierLayer6).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, false, BlockModelGenerators.plainVariant(identifierLayer3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(5, false, BlockModelGenerators.plainVariant(identifierLayer1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, true, BlockModelGenerators.plainVariant(identifierLayerDown2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, true, BlockModelGenerators.plainVariant(identifierLayerDown4).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, true, BlockModelGenerators.plainVariant(identifierLayerDown6).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(4, true, BlockModelGenerators.plainVariant(identifierLayerDown3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(5, true, BlockModelGenerators.plainVariant(identifierLayerDown1).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerStump(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayer6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_down_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_down_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_down_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_down_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLayerDown6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stump_down_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_6", textureMap, blockStateModelGenerator.modelOutput);
        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file with randomized rotations
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Stump.LAYERS, Stump.DOWN)
                        .generate((layers, down) -> {
                            if (layers == 1 && !down) return createRandomRotations(identifierLayer2);
                            if (layers == 2 && !down) return createRandomRotations(identifierLayer4);
                            if (layers == 3 && !down) return createRandomRotations(identifierLayer6);
                            if (layers == 4 && !down) return createRandomRotations(identifierLayer3);
                            if (layers == 5 && !down) return createRandomRotations(identifierLayer1);
                            if (layers == 1 && down) return createRandomRotations(identifierLayerDown2);
                            if (layers == 2 && down) return createRandomRotations(identifierLayerDown4);
                            if (layers == 3 && down) return createRandomRotations(identifierLayerDown6);
                            if (layers == 4 && down) return createRandomRotations(identifierLayerDown3);
                            if (layers == 5 && down) return createRandomRotations(identifierLayerDown1);
                            return BlockModelGenerators.plainVariant(identifierLayerDown6); // fallback
                        })));
    }

    private void registerVerticalQuarterStump(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        //Create both the TextureMap and TextureKey list as a pair with one method since they require all the same methods to create
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        String blockModelName = ModelLocationUtils.getModelLocation(block).toString().replace("_vertical_quarter", "");
        //Create our models and the identifiers for them that we'll use in our blockStates
        Identifier identifierLayer1 = Identifier.parse(blockModelName + "_1");
        Identifier identifierLayer2 = Identifier.parse(blockModelName + "_2");
        Identifier identifierLayer3 = Identifier.parse(blockModelName + "_3");
        Identifier identifierLayer4 = Identifier.parse(blockModelName + "_4");
        Identifier identifierLayer6 = Identifier.parse(blockModelName + "_6");

        Identifier identifierLayerDown1 = Identifier.parse(blockModelName + "_down_1");
        Identifier identifierLayerDown2 = Identifier.parse(blockModelName + "_down_2");
        Identifier identifierLayerDown3 = Identifier.parse(blockModelName + "_down_3");
        Identifier identifierLayerDown4 = Identifier.parse(blockModelName + "_down_4");
        Identifier identifierLayerDown6 = Identifier.parse(blockModelName + "_down_6");

        //Create our item model
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLayer3, blockData);
        //Setting up the blockState file
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(createLayeredHorizontalRotationStates(
                        LogVerticalQuarter.LAYERS,
                        LogVerticalQuarter.DOWN,
                        new int[]{ 7, 6, 4, 2, 5 },
                        new Identifier[]{ identifierLayer1, identifierLayer2, identifierLayer4, identifierLayer6, identifierLayer3 },
                        new Identifier[]{ identifierLayerDown1, identifierLayerDown2, identifierLayerDown4, identifierLayerDown6, identifierLayerDown3 }
                )));
    }

    private MultiVariant createRandomRotations(Identifier modelId) {
        Variant base = BlockModelGenerators.plainModel(modelId);

        return BlockModelGenerators.variants(
                base.with(BlockModelGenerators.UV_LOCK),                    // R0
                base.with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.UV_LOCK),
                base.with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.UV_LOCK),
                base.with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.UV_LOCK)
        );
    }

    private void registerArrowSlit(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierArrowslitFront = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_arrowslit_front")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_front", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierArrowslitBack = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_arrowslit_back")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_back", textureMap, blockStateModelGenerator.modelOutput);

        registerTintedOrPlain(blockStateModelGenerator, block, identifierArrowslitFront, blockData);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(ArrowSlit.POSITION)
                        .select(ArrowSlit.Position.FRONT, BlockModelGenerators.plainVariant(identifierArrowslitFront).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(ArrowSlit.Position.BACK, BlockModelGenerators.plainVariant(identifierArrowslitBack).with(VariantMutator.UV_LOCK.withValue(true)))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST,BlockModelGenerators.Y_ROT_90).select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180).select(Direction.WEST, BlockModelGenerators.Y_ROT_270).select(Direction.NORTH, BlockModelGenerators.NOP)));
    }

    private void registerCube(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierCube = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cube")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierCube, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(identifierCube)));
    }

    private void registerLog(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createLogTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierLog = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_log_horizontal")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLog, blockData);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Log.AXIS)
                        .select(Direction.Axis.Y, BlockModelGenerators.plainVariant(identifierLog))
                        .select(Direction.Axis.Z, BlockModelGenerators.plainVariant(identifierLog).with(BlockModelGenerators.X_ROT_90))
                        .select(Direction.Axis.X, BlockModelGenerators.plainVariant(identifierLog).with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_90))));
    }

    private void registerBark(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierLog = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_bark")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLog, blockData);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Log.AXIS)
                        .select(Direction.Axis.Y, BlockModelGenerators.plainVariant(identifierLog))
                        .select(Direction.Axis.Z, BlockModelGenerators.plainVariant(identifierLog).with(BlockModelGenerators.X_ROT_90))
                        .select(Direction.Axis.X, BlockModelGenerators.plainVariant(identifierLog).with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_90))));
    }

    private void registerCover(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierCover = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cover")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierCover, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(identifierCover)));
    }

    private void registerArchSmall(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSmallArch = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_arch")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSmallArch, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(WaterloggedBidirectionalShape.DIRECTION)
                        .select(BidirectionalShape.NORTH_SOUTH, BlockModelGenerators.plainVariant(identifierSmallArch))
                        .select(BidirectionalShape.EAST_WEST, BlockModelGenerators.plainVariant(identifierSmallArch).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true))))
        );
    }

    private void registerArchSmallHalf(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSmallArchHalf = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_arch_half")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSmallArchHalf, blockData);

        MultiVariant base = BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(VariantMutator.UV_LOCK.withValue(true));

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING)
                        .generate(direction -> switch (direction) {
                            case EAST -> base.with(BlockModelGenerators.Y_ROT_90);
                            case SOUTH -> base.with(BlockModelGenerators.Y_ROT_180);
                            case WEST -> base.with(BlockModelGenerators.Y_ROT_270);
                            default -> base;
                        })));
    }


    private void registerArchTwoMeter(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSmallArchHalf = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_arch_twometer")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSmallArchHalf, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, HorizontalDirectionalShape.DIRECTION)
                        .select(Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSmallArchHalf))
                        .select(Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSmallArchHalf).with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
        ;
    }

    private void registerArchTwoMeterHalf(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSmallArchHalfLeft = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_arch_twometer_half_left")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSmallArchHalfRight = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_arch_twometer_half_right")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSmallArchHalfRight, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, VerticalSlabCorner.HINGE, WaterloggedHorizontalDirectionalShape.DIRECTION)
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.WEST, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.EAST, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.WEST, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.EAST, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(BlockModelGenerators.X_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.WEST, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.EAST, BlockModelGenerators.plainVariant(identifierSmallArchHalfLeft).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.WEST, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.EAST, BlockModelGenerators.plainVariant(identifierSmallArchHalfRight).with(VariantMutator.UV_LOCK.withValue(true)))
                        ));

    }

    private void registerWindowSmall(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierWindowUpDown = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small_updown")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_updown", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierWindowSmallDown = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small_down")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierWindowSmallUp = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierWindowSmall = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierWindowUpDown, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(WindowSmall.UP, WindowSmall.DOWN)
                        .select(false, false, BlockModelGenerators.plainVariant(identifierWindowUpDown))
                        .select(true, false, BlockModelGenerators.plainVariant(identifierWindowSmallDown))
                        .select(false, true, BlockModelGenerators.plainVariant(identifierWindowSmallUp))
                        .select(true, true, BlockModelGenerators.plainVariant(identifierWindowSmall))));
    }

    private void registerWindowSmallHalf(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierWindowUpDown = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small_half_updown")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_updown", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierWindowSmallDown = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small_half_down")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierWindowSmallUp = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small_half_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierWindowSmall = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_window_small_half")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierWindowUpDown, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(WindowSmall.UP, WindowSmall.DOWN)
                        .select(false, false, BlockModelGenerators.plainVariant(identifierWindowUpDown).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, false, BlockModelGenerators.plainVariant(identifierWindowSmallDown).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, BlockModelGenerators.plainVariant(identifierWindowSmallUp).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, BlockModelGenerators.plainVariant(identifierWindowSmall).with(VariantMutator.UV_LOCK.withValue(true))))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.SOUTH, BlockModelGenerators.NOP).select(Direction.WEST, BlockModelGenerators.Y_ROT_90).select(Direction.NORTH, BlockModelGenerators.Y_ROT_180).select(Direction.EAST, BlockModelGenerators.Y_ROT_270)));
    }

    private void registerBalustrade(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierBalustradeBase = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_balustrade_base")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_base", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBalustrade = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_balustrade")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierBalustrade, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(RotatedPillarBlock.AXIS)
                        .select(Direction.Axis.Z, BlockModelGenerators.plainVariant(identifierBalustrade).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Direction.Axis.Y, BlockModelGenerators.plainVariant(identifierBalustradeBase).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Direction.Axis.X, BlockModelGenerators.plainVariant(identifierBalustrade).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerCapital(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierCapitalUpSide = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_capital_up_side")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_up_side", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierCapitalUpFLat = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_capital_up_flat")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_up_flat", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierCapitalDownSide = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_capital_down_side")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_side", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierCapitalDownFlat = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_capital_down_flat")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_down_flat", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierCapitalDownFlat, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Capital.FACING, Capital.TYPE)
                        .select(CapitalDirection.NORTH, Half.TOP, BlockModelGenerators.plainVariant(identifierCapitalUpSide).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.EAST, Half.TOP, BlockModelGenerators.plainVariant(identifierCapitalUpSide).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.SOUTH, Half.TOP, BlockModelGenerators.plainVariant(identifierCapitalUpSide).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.WEST, Half.TOP, BlockModelGenerators.plainVariant(identifierCapitalUpSide).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.FLAT, Half.TOP, BlockModelGenerators.plainVariant(identifierCapitalUpFLat).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(CapitalDirection.NORTH, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierCapitalDownSide).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.EAST, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierCapitalDownSide).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.SOUTH, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierCapitalDownSide).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.WEST, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierCapitalDownSide).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(CapitalDirection.FLAT, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierCapitalDownFlat).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerSlab(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSlabBottom1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom5 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_5")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_5", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom7 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_7")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_7", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom8 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_8")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_8", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop5 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_5")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_5", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop7 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_7")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_7", textureMap, blockStateModelGenerator.modelOutput);

//        Identifier identifierCube;
//        if (Registries.BLOCK.getId(blockData.getProps().getParent().getBlock()).getNamespace().equals("minecraft")) {
//            identifierCube = new Identifier("minecraft", "block/" + Registries.BLOCK.getId(blockData.getProps().getParent().getBlock()).getPath());
//        } else {
//            identifierCube = new Identifier(Registries.BLOCK.getId(block).getNamespace(), "block/" + Registries.BLOCK.getId(block).getPath().replace("_slab", ""));
//        }

        registerTintedOrPlain(blockStateModelGenerator, block, identifierSlabBottom4, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Slab.LAYERS, TYPE_UPDOWN)
                        .select(1, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop1))
                        .select(2, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop2))
                        .select(3, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop3))
                        .select(4, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop4))
                        .select(5, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop5))
                        .select(6, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop6))
                        .select(7, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop7))
                        .select(8, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabBottom8))

                        .select(1, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom1))
                        .select(2, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom2))
                        .select(3, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom3))
                        .select(4, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom4))
                        .select(5, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom5))
                        .select(6, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom6))
                        .select(7, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom7))
                        .select(8, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom8))));
    }

    private void registerSlabLessLayers(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSlabBottom1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_top_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_6", textureMap, blockStateModelGenerator.modelOutput);

        registerTintedOrPlain(blockStateModelGenerator, block, identifierSlabBottom4, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(SlabLessLayers.LAYERS, TYPE_UPDOWN)
                        .select(1, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop1))
                        .select(2, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop2))
                        .select(3, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop4))
                        .select(4, Half.TOP, BlockModelGenerators.plainVariant(identifierSlabTop6))

                        .select(1, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom1))
                        .select(2, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom2))
                        .select(3, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom4))
                        .select(4, Half.BOTTOM, BlockModelGenerators.plainVariant(identifierSlabBottom6))
                ));
    }

    private void registerLayer(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSlabBottom1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_3")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_3", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom4 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom5 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_5")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_5", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom6 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom7 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_7")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_7", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom8 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_bottom_8")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_8", textureMap, blockStateModelGenerator.modelOutput);

        registerTintedOrPlain(blockStateModelGenerator, block, identifierSlabBottom4, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Layer.LAYERS)
                        .select(1, BlockModelGenerators.plainVariant(identifierSlabBottom1))
                        .select(2, BlockModelGenerators.plainVariant(identifierSlabBottom2))
                        .select(3, BlockModelGenerators.plainVariant(identifierSlabBottom3))
                        .select(4, BlockModelGenerators.plainVariant(identifierSlabBottom4))
                        .select(5, BlockModelGenerators.plainVariant(identifierSlabBottom5))
                        .select(6, BlockModelGenerators.plainVariant(identifierSlabBottom6))
                        .select(7, BlockModelGenerators.plainVariant(identifierSlabBottom7))
                        .select(8, BlockModelGenerators.plainVariant(identifierSlabBottom8))
                ));
    }


    private void registerSlabQuarterBeam(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSlabBottom1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/beams/parent_slab_quarter_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/beams/parent_slab_quarter_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/beams/parent_slab_quarter_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/beams/parent_slab_quarter_2_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/beams/parent_slab_quarter_4_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/beams/parent_slab_quarter_6_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6_top", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSlabBottom2, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(SlabQuarter.LAYERS, TYPE_UPDOWN, BlockStateProperties.HORIZONTAL_FACING)
                        .select(1, Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop1))
                        .select(1, Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop1).with(BlockModelGenerators.Y_ROT_90))
                        .select(1, Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop1).with(BlockModelGenerators.Y_ROT_180))
                        .select(1, Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop1).with(BlockModelGenerators.Y_ROT_270))
                        .select(2, Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop2))
                        .select(2, Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop2).with(BlockModelGenerators.Y_ROT_90))
                        .select(2, Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop2).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop2).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop3))
                        .select(3, Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop3).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop3).with(BlockModelGenerators.Y_ROT_180))
                        .select(3, Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop3).with(BlockModelGenerators.Y_ROT_270))

                        .select(1, Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom1).with(BlockModelGenerators.Y_ROT_180))
                        .select(1, Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom1).with(BlockModelGenerators.Y_ROT_270))
                        .select(1, Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom1))
                        .select(1, Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom1).with(BlockModelGenerators.Y_ROT_90))
                        .select(2, Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom2).with(BlockModelGenerators.Y_ROT_180))
                        .select(2, Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom2).with(BlockModelGenerators.Y_ROT_270))
                        .select(2, Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom2))
                        .select(2, Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom2).with(BlockModelGenerators.Y_ROT_90))
                        .select(3, Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom3).with(BlockModelGenerators.Y_ROT_180))
                        .select(3, Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom3).with(BlockModelGenerators.Y_ROT_270))
                        .select(3, Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom3))
                        .select(3, Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom3).with(BlockModelGenerators.Y_ROT_90))
                ));
    }

    private void registerSlabQuarter(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSlabBottom1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_quarter_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_quarter_4")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabBottom3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_quarter_6")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_quarter_2_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_quarter_4_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_4_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop3 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_quarter_6_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_6_top", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSlabBottom2, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(SlabQuarter.LAYERS, TYPE_UPDOWN, BlockStateProperties.HORIZONTAL_FACING)
                        .select(1, Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop2).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop2).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop2).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop3).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop3).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop3).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(1, Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(1, Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom2).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom2).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(2, Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom2).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom3).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom3).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom3).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(3, Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom3).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerSlabCorner(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSlabBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_corner")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_corner_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSlabBottom, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, BlockStateProperties.HORIZONTAL_FACING)
                        .select(Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                ));
    }

    private void registerSlabEighth(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierSlabBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_eighth")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierSlabTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_slab_eighth_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierSlabBottom, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, BlockStateProperties.HORIZONTAL_FACING)
                        .select(Half.TOP, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabTop).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabTop).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabTop).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabTop).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.NORTH, BlockModelGenerators.plainVariant(identifierSlabBottom).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.EAST, BlockModelGenerators.plainVariant(identifierSlabBottom).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.SOUTH, BlockModelGenerators.plainVariant(identifierSlabBottom).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, Direction.WEST, BlockModelGenerators.plainVariant(identifierSlabBottom).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerBit(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierBit1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_bit_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierBit2 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_bit_2")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_2", textureMap, blockStateModelGenerator.modelOutput);

        registerTintedOrPlain(blockStateModelGenerator, block, identifierBit1, blockData);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, Bit.TOGGLE)
                        .select(Half.BOTTOM, 1, BlockModelGenerators.plainVariant(identifierBit1).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, 2, BlockModelGenerators.plainVariant(identifierBit2).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, 1, BlockModelGenerators.plainVariant(identifierBit1).with(OffsetVariantSetting.yOffset(8)).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, 2, BlockModelGenerators.plainVariant(identifierBit2).with(OffsetVariantSetting.yOffset(8)).with(VariantMutator.UV_LOCK.withValue(true)))
                ));
    }

    private void registerVerticalSlabCorner(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierLeft = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_slab_left")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_left", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRight = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_slab_right")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_right", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierLeftBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_slab_bottom_left")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_left", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierRightBottom = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_vertical_corner_slab_bottom_right")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_bottom_right", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierLeft, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(TYPE_UPDOWN, VerticalSlabCorner.HINGE, WaterloggedHorizontalDirectionalShape.DIRECTION, VerticalSlabCorner.WP_COMPAT)
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.SOUTH, false, BlockModelGenerators.plainVariant(identifierLeftBottom).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.WEST, false, BlockModelGenerators.plainVariant(identifierLeftBottom).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.NORTH, false, BlockModelGenerators.plainVariant(identifierRightBottom).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.EAST, false, BlockModelGenerators.plainVariant(identifierRightBottom).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.SOUTH, false, BlockModelGenerators.plainVariant(identifierRightBottom).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.WEST, false, BlockModelGenerators.plainVariant(identifierRightBottom).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.NORTH, false, BlockModelGenerators.plainVariant(identifierLeftBottom).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.EAST, false, BlockModelGenerators.plainVariant(identifierLeftBottom).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.SOUTH, false, BlockModelGenerators.plainVariant(identifierRight).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.WEST, false, BlockModelGenerators.plainVariant(identifierRight).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.NORTH, false, BlockModelGenerators.plainVariant(identifierLeft).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.EAST, false, BlockModelGenerators.plainVariant(identifierLeft).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.SOUTH, false, BlockModelGenerators.plainVariant(identifierLeft).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.WEST, false, BlockModelGenerators.plainVariant(identifierLeft).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.NORTH, false, BlockModelGenerators.plainVariant(identifierRight).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.EAST, false, BlockModelGenerators.plainVariant(identifierRight).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))


                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.SOUTH, true, BlockModelGenerators.plainVariant(identifierLeftBottom).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.WEST, true, BlockModelGenerators.plainVariant(identifierLeftBottom).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.NORTH, true, BlockModelGenerators.plainVariant(identifierLeftBottom).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.LEFT, Direction.EAST, true, BlockModelGenerators.plainVariant(identifierLeftBottom).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.SOUTH, true, BlockModelGenerators.plainVariant(identifierRightBottom).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.WEST, true, BlockModelGenerators.plainVariant(identifierRightBottom).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.NORTH, true, BlockModelGenerators.plainVariant(identifierRightBottom).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.BOTTOM, DoorHingeSide.RIGHT, Direction.EAST, true, BlockModelGenerators.plainVariant(identifierRightBottom).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.SOUTH, true, BlockModelGenerators.plainVariant(identifierRight).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.WEST, true, BlockModelGenerators.plainVariant(identifierRight).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.NORTH, true, BlockModelGenerators.plainVariant(identifierRight).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.RIGHT, Direction.EAST, true, BlockModelGenerators.plainVariant(identifierRight).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.SOUTH, true, BlockModelGenerators.plainVariant(identifierLeft).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.WEST, true, BlockModelGenerators.plainVariant(identifierLeft).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.NORTH, true, BlockModelGenerators.plainVariant(identifierLeft).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(Half.TOP, DoorHingeSide.LEFT, Direction.EAST, true, BlockModelGenerators.plainVariant(identifierLeft).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                ));


    }

    private void registerStairs(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierStairs = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stairs")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierStairsInner = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stairs_inner")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_inner", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierStairsOuter = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_stairs_outer")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_outer", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierStairs, blockData);
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createStairs(block, BlockModelGenerators.plainVariant(identifierStairsInner), BlockModelGenerators.plainVariant(identifierStairs), BlockModelGenerators.plainVariant(identifierStairsOuter)));
    }

    private void registerPlatformHorizontal(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier identifierStairs = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_platform_horizontal_bottom")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierStairsTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_platform_horizontal_top")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierStairsInner = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_platform_horizontal_bottom_inner")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_inner", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierStairsInnerTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_platform_horizontal_top_inner")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_inner", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierStairsOuter = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_platform_horizontal_bottom_outer")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_outer", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierStairsOuterTop = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_platform_horizontal_top_outer")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_top_outer", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierStairs, blockData);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch
                .initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE)
                .select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairs).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairs).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairs).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairs))

                .select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuter))
                .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuter).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuter).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuter).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuter).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuter).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuter))
                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuter).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInner))
                .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInner).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInner).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInner).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInner).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInner).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInner))
                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInner).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairsTop).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairsTop).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairsTop).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, BlockModelGenerators.plainVariant(identifierStairsTop).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, BlockModelGenerators.plainVariant(identifierStairsOuterTop).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(VariantMutator.UV_LOCK.withValue(true)))

                .select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                .select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, BlockModelGenerators.plainVariant(identifierStairsInnerTop).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
        )
    );
    }

    private void registerWalls(BlockModelGenerators generator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        Identifier inventory = createWallModel(generator, block, "_inventory", "conquest:block/templates/parent_wall_inventory", textureKeys, textureMap);
        Identifier post      = createWallModel(generator, block, "_post",      "conquest:block/templates/parent_wall_post", textureKeys, textureMap);
        Identifier side      = createWallModel(generator, block, "_side",      "conquest:block/templates/parent_wall_side", textureKeys, textureMap);
        Identifier sideTall  = createWallModel(generator, block, "_side_tall", "conquest:block/templates/parent_wall_side_tall", textureKeys, textureMap);

        registerTintedOrPlain(generator, block, inventory, blockData);

        generator.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(upCondition(), BlockModelGenerators.plainVariant(post))

                        // Low sides
                        .with(lowSideCondition(WallNew.NORTH_SHAPE_CUSTOM), lowSide(side))
                        .with(lowSideCondition(WallNew.EAST_SHAPE_CUSTOM),  lowSide(side).with(BlockModelGenerators.Y_ROT_90))
                        .with(lowSideCondition(WallNew.SOUTH_SHAPE_CUSTOM), lowSide(side).with(BlockModelGenerators.Y_ROT_180))
                        .with(lowSideCondition(WallNew.WEST_SHAPE_CUSTOM),  lowSide(side).with(BlockModelGenerators.Y_ROT_270))

                        // Tall sides
                        .with(tallSideCondition(WallNew.NORTH_SHAPE_CUSTOM), tallSide(sideTall))
                        .with(tallSideCondition(WallNew.EAST_SHAPE_CUSTOM),  tallSide(sideTall).with(BlockModelGenerators.Y_ROT_90))
                        .with(tallSideCondition(WallNew.SOUTH_SHAPE_CUSTOM), tallSide(sideTall).with(BlockModelGenerators.Y_ROT_180))
                        .with(tallSideCondition(WallNew.WEST_SHAPE_CUSTOM),  tallSide(sideTall).with(BlockModelGenerators.Y_ROT_270))
        );
    }

    // ==================== Condition Helpers ====================

    private Condition upCondition() {
        return BlockModelGenerators.condition().term(BlockStateProperties.UP, true).build();
    }

    private Condition lowSideCondition(Property<ModdedWallShape> prop) {
        return BlockModelGenerators.condition().term(prop, ModdedWallShape.LOW).build();
    }

    private Condition tallSideCondition(Property<ModdedWallShape> prop) {
        return BlockModelGenerators.condition().term(prop, ModdedWallShape.TALL).build();
    }

    // ==================== Variant Helpers ====================

    private MultiVariant lowSide(Identifier model) {
        return BlockModelGenerators.plainVariant(model).with(BlockModelGenerators.UV_LOCK);
    }

    private MultiVariant tallSide(Identifier model) {
        return BlockModelGenerators.plainVariant(model).with(BlockModelGenerators.UV_LOCK);
    }

    private Identifier createWallModel(BlockModelGenerators gen, Block block, String suffix,
                                       String parent, List<TextureSlot> keys, TextureMapping map) {
        return new ModelTemplate(Optional.of(Identifier.parse(parent)), Optional.empty(),
                keys.toArray(new TextureSlot[0]))
                .createWithSuffix(block, suffix, map, gen.modelOutput);
    }

    private void registerPane(BlockModelGenerators generator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        // Generate all models
        Identifier post       = createPaneModel(generator, block, "_post",       "conquest:block/templates/parent_flatpane_post", textureKeys, textureMap);
        Identifier n          = createPaneModel(generator, block, "_n",          "conquest:block/templates/parent_flatpane_n", textureKeys, textureMap);
        Identifier ne         = createPaneModel(generator, block, "_ne",         "conquest:block/templates/parent_flatpane_ne", textureKeys, textureMap);
        Identifier ns         = createPaneModel(generator, block, "_ns",         "conquest:block/templates/parent_flatpane_ns", textureKeys, textureMap);
        Identifier nse        = createPaneModel(generator, block, "_nse",        "conquest:block/templates/parent_flatpane_nse", textureKeys, textureMap);
        Identifier nsew       = createPaneModel(generator, block, "_nsew",       "conquest:block/templates/parent_flatpane_nsew", textureKeys, textureMap);

        Identifier postUpDown = createPaneModel(generator, block, "_post_updown", "conquest:block/templates/parent_flatpane_post_updown", textureKeys, textureMap);
        Identifier nUpDown    = createPaneModel(generator, block, "_n_updown",    "conquest:block/templates/parent_flatpane_n_updown", textureKeys, textureMap);
        Identifier neUpDown   = createPaneModel(generator, block, "_ne_updown",   "conquest:block/templates/parent_flatpane_ne_updown", textureKeys, textureMap);
        Identifier nsUpDown   = createPaneModel(generator, block, "_ns_updown",   "conquest:block/templates/parent_flatpane_ns_updown", textureKeys, textureMap);
        Identifier nseUpDown  = createPaneModel(generator, block, "_nse_updown",  "conquest:block/templates/parent_flatpane_nse_updown", textureKeys, textureMap);
        Identifier nsewUpDown = createPaneModel(generator, block, "_nsew_updown", "conquest:block/templates/parent_flatpane_nsew_updown", textureKeys, textureMap);

        Identifier postDown   = createPaneModel(generator, block, "_post_down",   "conquest:block/templates/parent_flatpane_post_down", textureKeys, textureMap);
        Identifier nDown      = createPaneModel(generator, block, "_n_down",      "conquest:block/templates/parent_flatpane_n_down", textureKeys, textureMap);
        Identifier neDown     = createPaneModel(generator, block, "_ne_down",     "conquest:block/templates/parent_flatpane_ne_down", textureKeys, textureMap);
        Identifier nsDown     = createPaneModel(generator, block, "_ns_down",     "conquest:block/templates/parent_flatpane_ns_down", textureKeys, textureMap);
        Identifier nseDown    = createPaneModel(generator, block, "_nse_down",    "conquest:block/templates/parent_flatpane_nse_down", textureKeys, textureMap);
        Identifier nsewDown   = createPaneModel(generator, block, "_nsew_down",   "conquest:block/templates/parent_flatpane_nsew_down", textureKeys, textureMap);

        Identifier postUp     = createPaneModel(generator, block, "_post_up",     "conquest:block/templates/parent_flatpane_post_up", textureKeys, textureMap);
        Identifier nUp        = createPaneModel(generator, block, "_n_up",        "conquest:block/templates/parent_flatpane_n_up", textureKeys, textureMap);
        Identifier neUp       = createPaneModel(generator, block, "_ne_up",       "conquest:block/templates/parent_flatpane_ne_up", textureKeys, textureMap);
        Identifier nsUp       = createPaneModel(generator, block, "_ns_up",       "conquest:block/templates/parent_flatpane_ns_up", textureKeys, textureMap);
        Identifier nseUp      = createPaneModel(generator, block, "_nse_up",      "conquest:block/templates/parent_flatpane_nse_up", textureKeys, textureMap);
        Identifier nsewUp     = createPaneModel(generator, block, "_nsew_up",     "conquest:block/templates/parent_flatpane_nsew_up", textureKeys, textureMap);

        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(block), TextureMapping.layer0(new Material(Identifier.parse(blockData.getProps().textures().getTextures().get("*")))), generator.modelOutput);
        //ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(block), TextureMapping.layer0(TextureMapping.getBlockTexture(block, blockData.getProps().textures().getTextures().get("*"))), generator.modelOutput);
        //generator.registerSimpleFlatItemModel(block, blockData.getProps().textures().getTextures().get("*"));

        generator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(
                                SextupleProperty.<MultiVariant, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean>initial(Pane.DOWN, Pane.UP, Pane.NORTH, Pane.EAST, Pane.SOUTH, Pane.WEST)
                                        // === UP = false ===
                                        .select(false, false, false, false, false, false, plain(post))

                                        .select(false, false, true,  false, false, false, plain(n))
                                        .select(false, false, false, true,  false, false, rot90(n))
                                        .select(false, false, false, false, true,  false, rot180(n))
                                        .select(false, false, false, false, false, true,  rot270(n))

                                        .select(false, false, true,  true,  false, false, plain(ne))
                                        .select(false, false, false, true,  true,  false, rot90(ne))
                                        .select(false, false, false, false, true,  true,  rot180(ne))
                                        .select(false, false, true,  false, false, true,  rot270(ne))

                                        .select(false, false, true,  false, true,  false, plain(ns))
                                        .select(false, false, false, true,  false, true,  rot90(ns))

                                        .select(false, false, true,  true,  true,  false, plain(nse))
                                        .select(false, false, false, true,  true,  true,  rot90(nse))
                                        .select(false, false, true,  false, true,  true,  rot180(nse))
                                        .select(false, false, true,  true,  false, true,  rot270(nse))

                                        .select(false, false, true,  true,  true,  true,  plain(nsew))

                                        // === UP = true ===
                                        .select(false, true, false, false, false, false, plain(postUp))

                                        .select(false, true, true,  false, false, false, plain(nUp))
                                        .select(false, true, false, true,  false, false, rot90(nUp))
                                        .select(false, true, false, false, true,  false, rot180(nUp))
                                        .select(false, true, false, false, false, true,  rot270(nUp))

                                        .select(false, true, true,  true,  false, false, plain(neUp))
                                        .select(false, true, false, true,  true,  false, rot90(neUp))
                                        .select(false, true, false, false, true,  true,  rot180(neUp))
                                        .select(false, true, true,  false, false, true,  rot270(neUp))

                                        .select(false, true, true,  false, true,  false, plain(nsUp))
                                        .select(false, true, false, true,  false, true,  rot90(nsUp))

                                        .select(false, true, true,  true,  true,  false, plain(nseUp))
                                        .select(false, true, false, true,  true,  true,  rot90(nseUp))
                                        .select(false, true, true,  false, true,  true,  rot180(nseUp))
                                        .select(false, true, true,  true,  false, true,  rot270(nseUp))

                                        .select(false, true, true,  true,  true,  true,  plain(nsewUp))

                                        // === DOWN = true (UPDOWN) ===
                                        .select(true, true, false, false, false, false, plain(postUpDown))

                                        .select(true, true, true,  false, false, false, plain(nUpDown))
                                        .select(true, true, false, true,  false, false, rot90(nUpDown))
                                        .select(true, true, false, false, true,  false, rot180(nUpDown))
                                        .select(true, true, false, false, false, true,  rot270(nUpDown))

                                        .select(true, true, true,  true,  false, false, plain(neUpDown))
                                        .select(true, true, false, true,  true,  false, rot90(neUpDown))
                                        .select(true, true, false, false, true,  true,  rot180(neUpDown))
                                        .select(true, true, true,  false, false, true,  rot270(neUpDown))

                                        .select(true, true, true,  false, true,  false, plain(nsUpDown))
                                        .select(true, true, false, true,  false, true,  rot90(nsUpDown))

                                        .select(true, true, true,  true,  true,  false, plain(nseUpDown))
                                        .select(true, true, false, true,  true,  true,  rot90(nseUpDown))
                                        .select(true, true, true,  false, true,  true,  rot180(nseUpDown))
                                        .select(true, true, true,  true,  false, true,  rot270(nseUpDown))

                                        .select(true, true, true,  true,  true,  true,  plain(nsewUpDown))

                                        // === DOWN = true, UP = false ===
                                        .select(true, false, false, false, false, false, plain(postDown))

                                        .select(true, false, true,  false, false, false, plain(nDown))
                                        .select(true, false, false, true,  false, false, rot90(nDown))
                                        .select(true, false, false, false, true,  false, rot180(nDown))
                                        .select(true, false, false, false, false, true,  rot270(nDown))

                                        .select(true, false, true,  true,  false, false, plain(neDown))
                                        .select(true, false, false, true,  true,  false, rot90(neDown))
                                        .select(true, false, false, false, true,  true,  rot180(neDown))
                                        .select(true, false, true,  false, false, true,  rot270(neDown))

                                        .select(true, false, true,  false, true,  false, plain(nsDown))
                                        .select(true, false, false, true,  false, true,  rot90(nsDown))

                                        .select(true, false, true,  true,  true,  false, plain(nseDown))
                                        .select(true, false, false, true,  true,  true,  rot90(nseDown))
                                        .select(true, false, true,  false, true,  true,  rot180(nseDown))
                                        .select(true, false, true,  true,  false, true,  rot270(nseDown))

                                        .select(true, false, true,  true,  true,  true,  plain(nsewDown))
                        )
        );
    }

// ==================== Helpers ====================

    private Identifier createPaneModel(BlockModelGenerators gen, Block block, String suffix,
                                       String parent, List<TextureSlot> keys, TextureMapping map) {
        return new ModelTemplate(Optional.of(Identifier.parse(parent)), Optional.empty(),
                keys.toArray(new TextureSlot[0]))
                .createWithSuffix(block, suffix, map, gen.modelOutput);
    }

    private MultiVariant plain(Identifier model) {
        return BlockModelGenerators.plainVariant(model).with(BlockModelGenerators.UV_LOCK);
    }

    private MultiVariant rot90(Identifier model) {
        return BlockModelGenerators.plainVariant(model)
                .with(BlockModelGenerators.Y_ROT_90)
                .with(BlockModelGenerators.UV_LOCK);
    }

    private MultiVariant rot180(Identifier model) {
        return BlockModelGenerators.plainVariant(model)
                .with(BlockModelGenerators.Y_ROT_180)
                .with(BlockModelGenerators.UV_LOCK);
    }

    private MultiVariant rot270(Identifier model) {
        return BlockModelGenerators.plainVariant(model)
                .with(BlockModelGenerators.Y_ROT_270)
                .with(BlockModelGenerators.UV_LOCK);
    }

    private void registerBranch(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Multipart models and blockState set-ups ate up RAM like crazy in the past, so we construct our files like this -- fully enumerating all the states.
        Identifier identifierPost = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_post")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_post", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_n")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_n_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_n_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_Up_1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_n_up_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_up_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_ne")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_ne_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNS = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_ns")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNS_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_ns_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_nse")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_nse_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_nsew")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_branch_nsew_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew_up", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierPost, blockData);

        List<Variant> northrandom = List.of(
                BlockModelGenerators.plainModel(identifierN),
                BlockModelGenerators.plainModel(identifierN_1)
        );
        List<Variant> eastrandom = List.of(
                BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> southrandom = List.of(
                BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> westrandom = List.of(
                BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true))
        );

//        eastrandom.add(BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)));
//        eastrandom.add(BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)));
//        southrandom.add(BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)));
//        southrandom.add(BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)));
//        westrandom.add(BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)));
//        westrandom.add(BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)));

        List<Variant> northUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up),
                BlockModelGenerators.plainModel(identifierN_Up_1)
        );
        List<Variant> eastUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> southUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> westUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true))
        );
        //northUprandom.add(BlockModelGenerators.plainModel(identifierN_Up));
        //northUprandom.add(BlockModelGenerators.plainModel(identifierN_Up_1));
        //eastUprandom.add(BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)));
        //eastUprandom.add(BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)));
        //southUprandom.add(BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)));
        //southUprandom.add(BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)));
        //westUprandom.add(BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)));
        //westUprandom.add(BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)));


        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Branch.UP, Branch.NORTH, Branch.EAST, Branch.SOUTH, Branch.WEST)
                        //up = false variants
                        .select(false, false, false, false, false, BlockModelGenerators.plainVariant(identifierPost))

                        .select(false, true, false, false, false, weightedRandom(northrandom))
                        .select(false, false, true, false, false, weightedRandom(eastrandom))
                        .select(false, false, false, true, false, weightedRandom(southrandom))
                        .select(false, false, false, false, true, weightedRandom(westrandom))

                        .select(false, true, true, false, false, BlockModelGenerators.plainVariant(identifierNE))
                        .select(false, false, true, true, false, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, false, true, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, false, false, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, false, true, false, BlockModelGenerators.plainVariant(identifierNS))
                        .select(false, false, true, false, true, BlockModelGenerators.plainVariant(identifierNS).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, true, true, false, BlockModelGenerators.plainVariant(identifierNSE))
                        .select(false, false, true, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, false, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, true, false, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, true, true, true, BlockModelGenerators.plainVariant(identifierNSEW))

                        //up = true variants
                        .select(true, false, false, false, false, BlockModelGenerators.plainVariant(identifierPost))

                        .select(true, true, false, false, false, weightedRandom(northUprandom))
                        .select(true, false, true, false, false, weightedRandom(eastUprandom))
                        .select(true, false, false, true, false, weightedRandom(southUprandom))
                        .select(true, false, false, false, true, weightedRandom(westUprandom))

                        .select(true, true, true, false, false, BlockModelGenerators.plainVariant(identifierNE_Up))
                        .select(true, false, true, true, false, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, false, false, true, true, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, false, false, true, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, false, true, false, BlockModelGenerators.plainVariant(identifierNS_Up))
                        .select(true, false, true, false, true, BlockModelGenerators.plainVariant(identifierNS_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, true, true, false, BlockModelGenerators.plainVariant(identifierNSE_Up))
                        .select(true, false, true, true, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, false, true, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, true, false, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, true, true, true, BlockModelGenerators.plainVariant(identifierNSEW_Up)))
                        );

    }

    private static MultiVariant weightedRandom(List<Variant> variants) {
        WeightedList.Builder<Variant> builder = WeightedList.builder();
        variants.forEach(builder::add);
        return new MultiVariant(builder.build());
    }

    private void registerBranchLarge(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Multipart models and blockState set-ups ate up RAM like crazy in the past, so we construct our files like this -- fully enumerating all the states.
        Identifier identifierPost = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_post")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_post", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_n")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_n_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_n_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_Up_1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_n_up_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_up_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_ne")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_ne_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNS = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_ns")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNS_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_ns_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_nse")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_nse_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_nsew")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_large_branch_nsew_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew_up", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierPost, blockData);


        List<Variant> northrandom = List.of(
                BlockModelGenerators.plainModel(identifierN),
                BlockModelGenerators.plainModel(identifierN_1)
        );
        List<Variant> eastrandom = List.of(
                BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> southrandom = List.of(
                BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> westrandom = List.of(
                BlockModelGenerators.plainModel(identifierN).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true))
        );

        List<Variant> northUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up),
                BlockModelGenerators.plainModel(identifierN_Up_1)
        );
        List<Variant> eastUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> southUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> westUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true))
        );


        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Branch.UP, Branch.NORTH, Branch.EAST, Branch.SOUTH, Branch.WEST)
                        //up = false variants
                        .select(false, false, false, false, false, BlockModelGenerators.plainVariant(identifierPost))

                        .select(false, true, false, false, false, weightedRandom(northrandom))
                        .select(false, false, true, false, false, weightedRandom(eastrandom))
                        .select(false, false, false, true, false, weightedRandom(southrandom))
                        .select(false, false, false, false, true, weightedRandom(westrandom))

                        .select(false, true, true, false, false, BlockModelGenerators.plainVariant(identifierNE))
                        .select(false, false, true, true, false, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, false, true, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, false, false, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, false, true, false, BlockModelGenerators.plainVariant(identifierNS))
                        .select(false, false, true, false, true, BlockModelGenerators.plainVariant(identifierNS).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, true, true, false, BlockModelGenerators.plainVariant(identifierNSE))
                        .select(false, false, true, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, false, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, true, false, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, true, true, true, BlockModelGenerators.plainVariant(identifierNSEW))

                        //up = true variants
                        .select(true, false, false, false, false, BlockModelGenerators.plainVariant(identifierPost))

                        .select(true, true, false, false, false, weightedRandom(northUprandom))
                        .select(true, false, true, false, false, weightedRandom(eastUprandom))
                        .select(true, false, false, true, false, weightedRandom(southUprandom))
                        .select(true, false, false, false, true, weightedRandom(westUprandom))

                        .select(true, true, true, false, false, BlockModelGenerators.plainVariant(identifierNE_Up))
                        .select(true, false, true, true, false, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, false, false, true, true, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, false, false, true, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, false, true, false, BlockModelGenerators.plainVariant(identifierNS_Up))
                        .select(true, false, true, false, true, BlockModelGenerators.plainVariant(identifierNS_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, true, true, false, BlockModelGenerators.plainVariant(identifierNSE_Up))
                        .select(true, false, true, true, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, false, true, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, true, false, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, true, true, true, BlockModelGenerators.plainVariant(identifierNSEW_Up))));

    }

    private void registerBranchSmall(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Multipart models and blockState set-ups ate up RAM like crazy in the past, so we construct our files like this -- fully enumerating all the states.
        Identifier identifierPost = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_post")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_post", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_n")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_n_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN_Up_1 = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_n_up_1")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n_up_1", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_ne")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_ne_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNS = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_ns")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNS_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_ns_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_nse")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_nse_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse_up", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_nsew")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW_Up = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_small_branch_nsew_up")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew_up", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierPost, blockData);

        List<Variant> northUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up),
                BlockModelGenerators.plainModel(identifierN_Up_1)
        );
        List<Variant> eastUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> southUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true))
        );
        List<Variant> westUprandom = List.of(
                BlockModelGenerators.plainModel(identifierN_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)),
                BlockModelGenerators.plainModel(identifierN_Up_1).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true))
        );

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(Branch.UP, Branch.NORTH, Branch.EAST, Branch.SOUTH, Branch.WEST)
                        //up = false variants
                        .select(false, false, false, false, false, BlockModelGenerators.plainVariant(identifierPost))

                        .select(false, true, false, false, false, BlockModelGenerators.plainVariant(identifierN))
                        .select(false, false, true, false, false, BlockModelGenerators.plainVariant(identifierN).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, false, true, false, BlockModelGenerators.plainVariant(identifierN).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, false, false, true, BlockModelGenerators.plainVariant(identifierN).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, true, false, false, BlockModelGenerators.plainVariant(identifierNE))
                        .select(false, false, true, true, false, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, false, true, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, false, false, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, false, true, false, BlockModelGenerators.plainVariant(identifierNS))
                        .select(false, false, true, false, true, BlockModelGenerators.plainVariant(identifierNS).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, true, true, false, BlockModelGenerators.plainVariant(identifierNSE))
                        .select(false, false, true, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, false, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, true, true, false, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(false, true, true, true, true, BlockModelGenerators.plainVariant(identifierNSEW))

                        //up = true variants
                        .select(true, false, false, false, false, BlockModelGenerators.plainVariant(identifierPost))

                        .select(true, true, false, false, false, weightedRandom(northUprandom))
                        .select(true, false, true, false, false, weightedRandom(eastUprandom))
                        .select(true, false, false, true, false, weightedRandom(southUprandom))
                        .select(true, false, false, false, true, weightedRandom(westUprandom))

                        .select(true, true, true, false, false, BlockModelGenerators.plainVariant(identifierNE_Up))
                        .select(true, false, true, true, false, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, false, false, true, true, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, false, false, true, BlockModelGenerators.plainVariant(identifierNE_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, false, true, false, BlockModelGenerators.plainVariant(identifierNS_Up))
                        .select(true, false, true, false, true, BlockModelGenerators.plainVariant(identifierNS_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, true, true, false, BlockModelGenerators.plainVariant(identifierNSE_Up))
                        .select(true, false, true, true, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, false, true, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, true, false, true, BlockModelGenerators.plainVariant(identifierNSE_Up).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, true, true, true, BlockModelGenerators.plainVariant(identifierNSEW_Up))));

    }

    private void registerFenceCross(BlockModelGenerators blockStateModelGenerator, Block block, BlockData blockData) {
        Tuple<TextureMapping, List<TextureSlot>> textures = createTextures(blockData, true);
        TextureMapping textureMap = textures.getA();
        List<TextureSlot> textureKeys = textures.getB();

        //Multipart models and blockState set-ups ate up RAM like crazy in the past, so we construct our files like this -- fully enumerating all the states.
        Identifier identifierPost = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cross_fence_post")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_post", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierN = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cross_fence_n")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_n", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cross_fence_ne")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ne", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNS = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cross_fence_ns")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_ns", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSE = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cross_fence_nse")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nse", textureMap, blockStateModelGenerator.modelOutput);
        Identifier identifierNSEW = new ModelTemplate(Optional.of(Identifier.parse("conquest:block/templates/parent_cross_fence_nsew")), Optional.empty(), textureKeys.toArray(new TextureSlot[textureKeys.size()])).createWithSuffix(block, "_nsew", textureMap, blockStateModelGenerator.modelOutput);
        registerTintedOrPlain(blockStateModelGenerator, block, identifierPost, blockData);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(FenceCross.NORTH, FenceCross.EAST, FenceCross.SOUTH, FenceCross.WEST)
                        //up = false variants
                        .select(false, false, false, false, BlockModelGenerators.plainVariant(identifierPost))

                        .select(true, false, false, false, BlockModelGenerators.plainVariant(identifierN))
                        .select(false, true, false, false, BlockModelGenerators.plainVariant(identifierN).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, true, false, BlockModelGenerators.plainVariant(identifierN).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, false, true, BlockModelGenerators.plainVariant(identifierN).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, false, false, BlockModelGenerators.plainVariant(identifierNE))
                        .select(false, true, true, false, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(false, false, true, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, false, false, true, BlockModelGenerators.plainVariant(identifierNE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, false, true, false, BlockModelGenerators.plainVariant(identifierNS))
                        .select(false, true, false, true, BlockModelGenerators.plainVariant(identifierNS).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))

                        .select(true, true, true, false, BlockModelGenerators.plainVariant(identifierNSE))
                        .select(false, true, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_90).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, false, true, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_180).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, false, true, BlockModelGenerators.plainVariant(identifierNSE).with(BlockModelGenerators.Y_ROT_270).with(VariantMutator.UV_LOCK.withValue(true)))
                        .select(true, true, true, true, BlockModelGenerators.plainVariant(identifierNSEW)))
                );
    }

    public static PropertyDispatch<MultiVariant> createLayeredHorizontalRotationStates(
            Property<Integer> layersProp,
            Property<Boolean> downProp,
            int[] layerOffsets,           // one offset per layer, index 0 = layer 1
            Identifier[] models,          // upright models, index 0 = layer 1
            Identifier[] downModels       // down models, index 0 = layer 1
    ) {
        PropertyDispatch.C3<MultiVariant, Integer, Boolean, Direction> map =
                PropertyDispatch.initial(layersProp, downProp, BlockStateProperties.HORIZONTAL_FACING);

        for (int i = 0; i < layerOffsets.length; i++) {
            int layer = i + 1;
            int offset = layerOffsets[i];
            Identifier model = models[i];
            Identifier downModel = downModels[i];

            for (boolean down : new boolean[]{false, true}) {
                Identifier activeModel = down ? downModel : model;

                map.select(layer, down, Direction.NORTH,
                        BlockModelGenerators.plainVariant(activeModel)
                                .with(OffsetVariantSetting.xOffset(offset)));

                map.select(layer, down, Direction.EAST,
                        BlockModelGenerators.plainVariant(activeModel)
                                .with(BlockModelGenerators.Y_ROT_90)
                                .with(OffsetVariantSetting.xOffset(-offset))
                                .with(OffsetVariantSetting.zOffset(offset)));

                map.select(layer, down, Direction.SOUTH,
                        BlockModelGenerators.plainVariant(activeModel)
                                .with(BlockModelGenerators.Y_ROT_180)
                                .with(OffsetVariantSetting.xOffset(-offset))
                                .with(OffsetVariantSetting.zOffset(-offset)));

                map.select(layer, down, Direction.WEST,
                        BlockModelGenerators.plainVariant(activeModel)
                                .with(BlockModelGenerators.Y_ROT_270)
                                .with(OffsetVariantSetting.xOffset(offset))
                                .with(OffsetVariantSetting.zOffset(-offset)));
            }
        }

        return map;
    }


    private Tuple<TextureMapping, List<TextureSlot>> createTextures(BlockData blockData, boolean hasTopBottom) {
        Map<String, String> textures = blockData.getProps().textures().getTextures();

        List<Object> textureKeys = new ArrayList<>();
        textureKeys.add(TextureSlot.SIDE);

        Identifier sideTexture = Identifier.parse(textures.get("*"));
        TextureMapping textureMap = new TextureMapping().put(TextureSlot.SIDE, new Material(sideTexture));

        if (hasTopBottom) {
            String topTexture = null;
            String bottomTexture = null;
            String middleTexture = null;

            try {
                topTexture = textures.get("top");
            } catch (NullPointerException exception) {

            }
            try {
                bottomTexture = textures.get("bottom");
            } catch (NullPointerException exception) {

            }
            try {
                middleTexture = textures.get("middle");
            } catch (NullPointerException exception) {

            }

            if (topTexture != null) {
                textureMap.put(TextureSlot.TOP, new Material(Identifier.parse(topTexture)));
                textureKeys.add(TextureSlot.TOP);
            }
            if (bottomTexture != null) {
                textureMap.put(TextureSlot.BOTTOM, new Material(Identifier.parse(bottomTexture)));
                textureKeys.add(TextureSlot.BOTTOM);
            }
            if (middleTexture != null) {
                TextureSlot middle = TextureSlot.create("middle");
                textureMap.put(middle, new Material(Identifier.parse(middleTexture)));
                textureKeys.add(middle);
            }
        }
        return new Tuple(textureMap, textureKeys);
    }

    private Tuple<TextureMapping, List<TextureSlot>> createLogTextures(BlockData blockData, boolean hasTopBottom) {
        Map<String, String> textures = blockData.getProps().textures().getTextures();

        List<Object> textureKeys = new ArrayList<>();
        textureKeys.add(TextureSlot.SIDE);

        Identifier sideTexture = Identifier.parse(textures.get("*"));
        TextureMapping textureMap = new TextureMapping().put(TextureSlot.SIDE, new Material(sideTexture));

        if (hasTopBottom) {
            String topTexture = null;
            String bottomTexture = null;
            String middleTexture = null;
            String endTexture = null;

            try {
                topTexture = textures.get("top");
            } catch (NullPointerException exception) {

            }
            try {
                bottomTexture = textures.get("bottom");
            } catch (NullPointerException exception) {

            }
            try {
                middleTexture = textures.get("middle");
            } catch (NullPointerException exception) {

            }
            try {
                endTexture = textures.get("end");
            } catch (NullPointerException exception) {

            }

            if (topTexture != null) {
                textureMap.put(TextureSlot.TOP, new Material(Identifier.parse(topTexture)));
                textureKeys.add(TextureSlot.TOP);
            }
            if (bottomTexture != null) {
                textureMap.put(TextureSlot.BOTTOM, new Material(Identifier.parse(bottomTexture)));
                textureKeys.add(TextureSlot.BOTTOM);
            }
            if (middleTexture != null) {
                TextureSlot middle = TextureSlot.create("middle");
                textureMap.put(middle, new Material(Identifier.parse(middleTexture)));
                textureKeys.add(middle);
            }
            if (endTexture != null) {
                textureMap.put(TextureSlot.END, new Material(Identifier.parse(endTexture)));
                textureKeys.add(TextureSlot.END);
            }
        }
        return new Tuple(textureMap, textureKeys);
    }
}
