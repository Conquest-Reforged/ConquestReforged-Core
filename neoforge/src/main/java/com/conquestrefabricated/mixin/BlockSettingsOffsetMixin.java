package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Mixin(BlockBehaviour.Properties.class)
public abstract class BlockSettingsOffsetMixin implements BlockSettingsAccessor {

    @Unique
    private CustomOffsetType customOffsetType = null;

    @Inject(method = "offsetType", at = @At("HEAD"), cancellable = true)
    private void customOffset(BlockBehaviour.OffsetType offsetType, CallbackInfoReturnable<BlockBehaviour.Properties> cir) {
        BlockBehaviour.Properties settings = (BlockBehaviour.Properties) (Object) this;
        if (customOffsetType != null) {
            if (customOffsetType == CustomOffsetType.LAYER_STATE_Y) {
                settings.offsetFunction = (state, pos) -> {
                    double layerY = 0.0;
                    int layerState = state.getValue(BlockStateProperties.LAYERS);
                    layerY = switch (layerState) {
                        case 7 -> -0.125;
                        case 6 -> -0.25;
                        case 5 -> -0.375;
                        case 4 -> -0.5;
                        case 3 -> -0.625;
                        case 2 -> -0.75;
                        case 1 -> -0.875;
                        default -> layerY;
                    };

                    if (offsetType == BlockBehaviour.OffsetType.XYZ) {
                        Block block = state.getBlock();
                        // Get the standard random offset
                        long l = Mth.getSeed(pos.getX(), 0, pos.getZ());
                        double d = ((double) ((float) (l >> 4 & 15L) / 15.0F) - (double) 1.0F) * (double) block.getMaxVerticalOffset() + layerY;
                        float f = block.getMaxHorizontalOffset();
                        double e = Mth.clamp(((double) ((float) (l & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                        double g = Mth.clamp(((double) ((float) (l >> 8 & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                        Vec3 randomOffset = new Vec3(e, d, g);
                        // Return standard random offset if no annotation present
                        return randomOffset;
                    } else {
                        return new Vec3(0, layerY, 0);
                    }
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.PLANT_XYZ) {
                settings.offsetFunction = (state, pos) -> {
                    double layerY = 0.0;
                    int layerState = state.getValue(BlockStateProperties.LAYERS);
                    layerY = switch (layerState) {
                        case 7 -> -0.125;
                        case 6 -> -0.25;
                        case 5 -> -0.375;
                        case 4 -> -0.5;
                        case 3 -> -0.625;
                        case 2 -> -0.75;
                        case 1 -> -0.875;
                        default -> layerY;
                    };

                    if (state.hasProperty(ModBlockProperties.OFFSET_TOGGLE) && state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        layerY += -1.0;
                    }
                    if (offsetType == BlockBehaviour.OffsetType.XYZ) {
                        Block block = state.getBlock();

                        // Get the standard random offset
                        long l = Mth.getSeed(pos.getX(), 0, pos.getZ());
                        double d = ((double) ((float) (l >> 4 & 15L) / 15.0F) - (double) 1.0F) * (double) block.getMaxVerticalOffset() + layerY;
                        float f = block.getMaxHorizontalOffset();
                        double e = Mth.clamp(((double) ((float) (l & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                        double g = Mth.clamp(((double) ((float) (l >> 8 & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                        Vec3 randomOffset = new Vec3(e, d, g);

                        // Return standard random offset if no annotation present
                        return randomOffset;
                    } else {
                        return new Vec3(0, layerY, 0);
                    }
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.PLANT_XZ) {
                settings.offsetFunction = (state, pos) -> {
                    double layerY = 0.0;
                    int layerState = state.getValue(BlockStateProperties.LAYERS);
                    layerY = switch (layerState) {
                        case 7 -> -0.125;
                        case 6 -> -0.25;
                        case 5 -> -0.375;
                        case 4 -> -0.5;
                        case 3 -> -0.625;
                        case 2 -> -0.75;
                        case 1 -> -0.875;
                        default -> layerY;
                    };

                    if (state.hasProperty(ModBlockProperties.OFFSET_TOGGLE) && state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        layerY += -1.0;
                    }

                    if (offsetType == BlockBehaviour.OffsetType.XZ) {
                        Block block = state.getBlock();

                        // Get the standard random offset
                        long l = Mth.getSeed(pos.getX(), 0, pos.getZ());
                        double d = ((double) ((float) (l >> 4 & 15L) / 15.0F) - (double) 1.0F) * (double) block.getMaxVerticalOffset() + layerY;
                        float f = block.getMaxHorizontalOffset();
                        double e = Mth.clamp(((double) ((float) (l & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                        double g = Mth.clamp(((double) ((float) (l >> 8 & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                        Vec3 randomOffset = new Vec3(e, d, g);

                        // Return standard random offset if no annotation present
                        return randomOffset;
                    } else {
                        return new Vec3(0, layerY, 0);
                    }
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.PLANT) {
                settings.offsetFunction = (state, pos) -> {
                    if (state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        double layerY = -1.0;
                        if (offsetType == BlockBehaviour.OffsetType.XYZ) {
                            Block block = state.getBlock();

                            // Get the standard random offset
                            long l = Mth.getSeed(pos.getX(), 0, pos.getZ());
                            double d = ((double) ((float) (l >> 4 & 15L) / 15.0F) - (double) 1.0F) * (double) block.getMaxVerticalOffset() + layerY;
                            float f = block.getMaxHorizontalOffset();
                            double e = Mth.clamp(((double) ((float) (l & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                            double g = Mth.clamp(((double) ((float) (l >> 8 & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, -f, f) + 0.001;
                            Vec3 randomOffset = new Vec3(e, d, g);

                            // Return standard random offset if no annotation present
                            return randomOffset;
                        } else {
                            return new Vec3(0, layerY, 0);
                        }
                    }
                    return new Vec3(0, 0, 0);
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.LAYERS_STATIC) {
                settings.offsetFunction = (state, pos) -> {
                    double layerY = 0.0;
                    int layerState = state.getValue(BlockStateProperties.LAYERS);
                    layerY = switch (layerState) {
                        case 7 -> -0.125;
                        case 6 -> -0.25;
                        case 5 -> -0.375;
                        case 4 -> -0.5;
                        case 3 -> -0.625;
                        case 2 -> -0.75;
                        case 1 -> -0.875;
                        default -> layerY;
                    };
                    return new Vec3(0, layerY, 0);
                };
                cir.setReturnValue(settings);
            } else if (offsetType == BlockBehaviour.OffsetType.XYZ) {
                // Create a custom offsetter that combines layer behavior with random XYZ offset
                settings.offsetFunction = (state, pos) -> {
                    Block block = state.getBlock();

                    // Get the standard random offset
                    long l = Mth.getSeed(pos.getX(), 0, pos.getZ());
                    double d = ((double) ((float) (l >> 4 & 15L) / 15.0F) - (double) 1.0F) * (double) block.getMaxVerticalOffset();
                    float f = block.getMaxHorizontalOffset();
                    double e = Mth.clamp(((double) ((float) (l & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, (double) (-f), (double) f);
                    double g = Mth.clamp(((double) ((float) (l >> 8 & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, (double) (-f), (double) f);
                    Vec3 randomOffset = new Vec3(e, d, g);

                    // Return standard random offset if no annotation present
                    return randomOffset;
                };

                cir.setReturnValue(settings);
            } else if (offsetType == BlockBehaviour.OffsetType.XZ) {
                // Handle XZ offset type similarly if needed
                // For now, just implementing the standard logic
                settings.offsetFunction = (state, pos) -> {
                    Block block = state.getBlock();
                    long l = Mth.getSeed(pos.getX(), 0, pos.getZ());
                    float f = block.getMaxHorizontalOffset();
                    double d = Mth.clamp(((double) ((float) (l & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, (double) (-f), (double) f);
                    double e = Mth.clamp(((double) ((float) (l >> 8 & 15L) / 15.0F) - (double) 0.5F) * (double) 0.5F, (double) (-f), (double) f);
                    return new Vec3(d, (double) 0.0F, e);
                };

                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.LAYER_Y) {
                settings.offsetFunction = (state, pos) -> {
                    if (state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        double layerY = 0.0;

                        Level world = Minecraft.getInstance().level;
                        if (world == null) return Vec3.ZERO;

                        BlockPos belowPos = pos.below();
                        BlockState belowState = world.getBlockState(belowPos);
                        Block belowBlock = belowState.getBlock();

                        // Apply layer offset logic based on block below
                        layerY = computeTranslationAmount(belowState);

                        return new Vec3(0, layerY, 0);
                    }
                    return new Vec3(0, 0, 0);
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.LAYER_XYZ) {
                settings.offsetFunction = (state, pos) -> {
                    if (state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        float translationAmountX = 0f;
                        float translationAmountZ = 0f;
                        float translationAmountY = 0f;

                        Level world = Minecraft.getInstance().level;
                        if (world == null) return Vec3.ZERO;

                        BlockState blockStateDown = world.getBlockState(pos.below());
                        Block blockDown = blockStateDown.getBlock();

                        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                            BlockState offsetBlock = world.getBlockState(pos.relative(direction.getOpposite(), 1));
                            if (offsetBlock.getBlock() instanceof VerticalSlab &&
                                    direction == offsetBlock.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                                switch (direction) {
                                    case NORTH -> translationAmountZ = -computeVerticalSlabTranslation(offsetBlock);
                                    case EAST -> translationAmountX = computeVerticalSlabTranslation(offsetBlock);
                                    case SOUTH -> translationAmountZ = computeVerticalSlabTranslation(offsetBlock);
                                    case WEST -> translationAmountX = -computeVerticalSlabTranslation(offsetBlock);
                                }
                            }
                        }
                        if (blockDown instanceof SlabBlock && blockStateDown.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                            translationAmountY = -0.5f;
                        } else {
                            translationAmountY = computeYTranslation(blockStateDown);
                        }

                        return new Vec3(translationAmountX, translationAmountY, translationAmountZ);
                    }
                    return new Vec3(0, 0, 0);
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.LAYER_XZ) {
                settings.offsetFunction = (state, pos) -> {
                    if (state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        float translationAmountX = 0f;
                        float translationAmountZ = 0f;

                        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                            Level world = Minecraft.getInstance().level;
                            if (world == null) return Vec3.ZERO;

                            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                            BlockState offsetBlock = world.getBlockState(pos.relative(direction.getOpposite(), 1));
                            if (offsetBlock.getBlock() instanceof VerticalSlab &&
                                    direction == offsetBlock.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                                switch (direction) {
                                    case NORTH -> translationAmountZ = -computeVerticalSlabTranslation(offsetBlock);
                                    case EAST -> translationAmountX = computeVerticalSlabTranslation(offsetBlock);
                                    case SOUTH -> translationAmountZ = computeVerticalSlabTranslation(offsetBlock);
                                    case WEST -> translationAmountX = -computeVerticalSlabTranslation(offsetBlock);
                                }
                            }
                        }

                        return new Vec3(translationAmountX, 0, translationAmountZ);
                    }
                    return new Vec3(0, 0, 0);
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.LAYER_XYZ_NSEWUD) {
                settings.offsetFunction = (state, pos) -> {
                    if (state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        float translationAmountX = 0f;
                        float translationAmountZ = 0f;
                        float translationAmountY = 0f;

                        Level world = Minecraft.getInstance().level;
                        if (world == null) return Vec3.ZERO;

                        BlockState blockStateDown = world.getBlockState(pos.below());
                        Block blockDown = blockStateDown.getBlock();

                        Direction direction = state.getValue(DirectionalBlock.FACING);
                        BlockState offsetBlock = world.getBlockState(pos.relative(direction.getOpposite(), 1));

                        if (offsetBlock.getBlock() instanceof VerticalSlab &&
                                direction == offsetBlock.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                            switch (direction) {
                                case NORTH -> translationAmountZ = -computeVerticalSlabTranslation(offsetBlock);
                                case EAST -> translationAmountX = computeVerticalSlabTranslation(offsetBlock);
                                case SOUTH -> translationAmountZ = computeVerticalSlabTranslation(offsetBlock);
                                case WEST -> translationAmountX = -computeVerticalSlabTranslation(offsetBlock);
                                default -> {
                                }
                            }
                        }


                        if (blockDown instanceof SlabBlock && blockStateDown.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                            translationAmountY = -0.5f;
                        } else {
                            translationAmountY = computeYTranslation(blockStateDown);
                        }

                        return new Vec3(translationAmountX, translationAmountY, translationAmountZ);
                    }
                    return new Vec3(0, 0, 0);
                };
                cir.setReturnValue(settings);
            } else if (customOffsetType == CustomOffsetType.LAYER_XYZ_LICHEN) {
                settings.offsetFunction = (state, pos) -> {
                    if (state.getValue(ModBlockProperties.OFFSET_TOGGLE)) {
                        float translationAmountX = 0f;
                        float translationAmountZ = 0f;
                        float translationAmountY = 0f;

                        Level world = Minecraft.getInstance().level;
                        if (world == null) return Vec3.ZERO;

                        // Determine the connection direction (order: NORTH, EAST, SOUTH, WEST; default NORTH)
                        Direction connectedDir = getLichenMossConnectionDirection(state);
                        // For XZ translation, we use the opposite of the connection direction
                        Direction directionXZ = connectedDir.getOpposite();

                        BlockState offsetBlock = world.getBlockState(pos.relative(connectedDir, 1));
                        if (offsetBlock.getBlock() instanceof VerticalSlab &&
                                offsetBlock.getValue(BlockStateProperties.HORIZONTAL_FACING) == directionXZ) {
                            switch (connectedDir) {
                                case NORTH -> translationAmountZ = computeVerticalSlabTranslation(offsetBlock);
                                case EAST -> translationAmountX = -computeVerticalSlabTranslation(offsetBlock);
                                case SOUTH -> translationAmountZ = -computeVerticalSlabTranslation(offsetBlock);
                                case WEST -> translationAmountX = computeVerticalSlabTranslation(offsetBlock);
                            }
                        }
                        // Only apply Y translation if the CONNECTING DOWN property is true
                        if (state.getValue(PipeBlock.DOWN)) {
                            BlockState blockStateDown = world.getBlockState(pos.below());
                            translationAmountY = computeYTranslation(blockStateDown);
                        }

                        return new Vec3(translationAmountX, translationAmountY, translationAmountZ);
                    }
                    return new Vec3(0, 0, 0);
                };
                cir.setReturnValue(settings);
            }
        }
        // Let default case fall through to vanilla implementation
    }

    /**
     * Returns the first connected direction for LichenMoss (NORTH, then EAST, SOUTH, WEST; default NORTH).
     */
    @Unique
    private Direction getLichenMossConnectionDirection(BlockState state) {
        if (state.getValue(PipeBlock.NORTH)) return Direction.NORTH;
        if (state.getValue(PipeBlock.EAST)) return Direction.EAST;
        if (state.getValue(PipeBlock.SOUTH)) return Direction.SOUTH;
        if (state.getValue(PipeBlock.WEST)) return Direction.WEST;
        return Direction.NORTH;
    }

    /**
     * Computes the XZ translation amount based on the number of vertical slab layers.
     */
    @Unique
    private float computeVerticalSlabTranslation(BlockState offsetBlock) {
        int layers = offsetBlock.getValue(VerticalSlab.LAYERS);
        return switch (layers) {
            case 1 -> -0.875f;
            case 2 -> -0.75f;
            case 3 -> -0.5f;
            case 4 -> -0.25f;
            default -> 0f;
        };
    }

    /**
     * Computes the Y translation amount for the block below.
     */
    @Unique
    private float computeYTranslation(BlockState downState) {
        Block block = downState.getBlock();
        if (block instanceof SlabBlock && downState.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
            return -0.5f;
        } else if (block instanceof Layer || block instanceof SnowLayerBlock) {
            int layers = downState.getValue(BlockStateProperties.LAYERS);
            return switch (layers) {
                case 7 -> -0.125f;
                case 6 -> -0.25f;
                case 5 -> -0.375f;
                case 4 -> -0.5f;
                case 3 -> -0.625f;
                case 2 -> -0.75f;
                case 1 -> -0.875f;
                default -> 0f;
            };
        } else if (block instanceof Slab && downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            int layers = downState.getValue(Slab.LAYERS);
            return switch (layers) {
                case 7 -> -0.125f;
                case 6 -> -0.25f;
                case 5 -> -0.375f;
                case 4 -> -0.5f;
                case 3 -> -0.625f;
                case 2 -> -0.75f;
                case 1 -> -0.875f;
                default -> 0f;
            };
        } else if (block instanceof SlabQuarter && downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            int layers = downState.getValue(SlabQuarter.LAYERS);
            return switch (layers) {
                case 3 -> -0.25f;
                case 2 -> -0.5f;
                case 1 -> -0.75f;
                default -> 0f;
            };
        } else if (block instanceof SlabEighth && downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            return -0.5f;
        } else if (block instanceof SlabCorner && downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            return -0.5f;
        } else if (block instanceof SlabLessLayers && downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            int layers = downState.getValue(SlabLessLayers.LAYERS);
            return switch (layers) {
                case 4 -> -0.25f;
                case 3 -> -0.5f;
                case 2 -> -0.75f;
                case 1 -> -0.875f;
                default -> 0f;
            };
        } else if (block instanceof BoardsHorizontal &&
                downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            return -0.53125f;
        }
        return 0f;
    }

    @Unique
    /**
     * Computes the Y translation based on the block state below.
     * Returns null if no translation should be applied.
     */
    private Float computeTranslationAmount(BlockState downState) {
        Block blockDown = downState.getBlock();
        if (blockDown instanceof SlabBlock && downState.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
            return -0.5f;
        } else if (blockDown instanceof Layer || blockDown instanceof SnowLayerBlock) {
            int layers = downState.getValue(BlockStateProperties.LAYERS);
            return getTranslationForStandardLayers(layers);
        } else if (blockDown instanceof Slab && downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            int layers = downState.getValue(Slab.LAYERS);
            return getTranslationForStandardLayers(layers);
        } else if (blockDown instanceof SlabLessLayers && downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            int layers = downState.getValue(SlabLessLayers.LAYERS);
            return getTranslationForSlabLessLayers(layers);
        } else if (blockDown instanceof BoardsHorizontal &&
                downState.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            return -0.53125f;
        }
        return 0f;
    }

    /**
     * Returns the translation amount for standard layered blocks (Layer, SnowBlock, Slab).
     */
    @Unique
    private float getTranslationForStandardLayers(int layers) {
        return switch (layers) {
            case 7 -> -0.125f;
            case 6 -> -0.25f;
            case 5 -> -0.375f;
            case 4 -> -0.5f;
            case 3 -> -0.625f;
            case 2 -> -0.75f;
            case 1 -> -0.875f;
            default -> 0f;
        };
    }

    /**
     * Returns the translation amount for SlabLessLayers.
     */
    @Unique
    private float getTranslationForSlabLessLayers(int layers) {
        return switch (layers) {
            case 4 -> -0.25f;
            case 3 -> -0.5f;
            case 2 -> -0.75f;
            case 1 -> -0.875f;
            default -> 0f;
        };
    }

    @Unique
    @Override
    public BlockBehaviour.Properties setCustomOffsetter(CustomOffsetType offsetter) {
        this.customOffsetType = offsetter;
        return (BlockBehaviour.Properties) (Object) this;
    }

}