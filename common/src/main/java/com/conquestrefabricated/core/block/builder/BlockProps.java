package com.conquestrefabricated.core.block.builder;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.core.block.factory.InitializationException;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * Basically just a wrapper around Block.Properties
 */
public abstract class BlockProps<T extends BlockProps<T>> {

    /**
     * Used internally to create the Block.Properties builder which requires an instance of either:
     * 1. a Block
     * 2. a Material & MaterialColor
     * 3. just a Material
     */
    private final Block block;
    private final MapColor color;

    private CustomOffsetType customOffsetType = null;
    private DyeColor dyeColor = null;
    private SoundType sound = null;
    private CreativeModeTab group = CreativeModeTabs.searchTab();
    private ToIntFunction<BlockState> light = null;
    private Float resistance = null;
    private Float hardness = null;
    private Float slipperiness = null;
    private Boolean randomTick = null;
    private Boolean variableOpacity = null;
    private Boolean dynamicBounds = null;
    private Boolean blocksMovement = null;
    private Boolean solid = null;
    private BlockBehaviour.OffsetType offset = BlockBehaviour.OffsetType.NONE;

    protected BlockProps(Block block) {
        this.block = block;
        this.color = null;
    }


    protected BlockProps(BlockProps<T> props) {
        this.block = props.block;
        this.color = props.color;
        this.dyeColor = props.dyeColor;
        this.sound = props.sound;
        this.group = props.group;
        this.light = props.light;
        this.resistance = props.resistance;
        this.hardness = props.hardness;
        this.slipperiness = props.slipperiness;
        this.randomTick = props.randomTick;
        this.variableOpacity = props.variableOpacity;
        this.blocksMovement = props.blocksMovement;
        this.dynamicBounds = props.dynamicBounds;
        this.solid = props.solid;
        this.customOffsetType = props.customOffsetType;
        this.offset = props.offset;
    }

    public abstract T getProps();

    public T customOffsetType(CustomOffsetType offsetType) {
        this.customOffsetType = offsetType;
        return getProps();
    }

    public T dye(DyeColor color) {
        this.dyeColor = color;
        return getProps();
    }

    public T sound(SoundType sound) {
        this.sound = sound;
        return getProps();
    }

    public T light(ToIntFunction<BlockState> light) {
        this.light = light;
        return (T) this;
    }

    public T strength(double hardness, double resistance) {
        this.hardness = (float) hardness;
        this.resistance = (float) resistance;
        return getProps();
    }

    public T slipperiness(double slipperiness) {
        this.slipperiness = (float) slipperiness;
        return getProps();
    }

    public T randomTick(boolean randomTick) {
        this.randomTick = randomTick;
        return getProps();
    }

    public T dynamicBounds(boolean dynamicBounds) {
        this.dynamicBounds = dynamicBounds;
        return getProps();
    }

    public T opacity(boolean variableOpacity) {
        this.variableOpacity = variableOpacity;
        return getProps();
    }

    public T blocking(boolean blocksMovement) {
        this.blocksMovement = blocksMovement;
        return getProps();
    }

    public T solid(boolean solid) {
        this.solid = solid;
        return getProps();
    }

    public T group(CreativeModeTab group) {
        this.group = group;
        return getProps();
    }

    public T offset(BlockBehaviour.OffsetType offsetType) {
        this.offset = offsetType;
        return getProps();
    }

    public CreativeModeTab group() {
        return group;
    }

    public DyeColor dye() {
        return dyeColor == null ? DyeColor.BLACK : dyeColor;
    }

    public MapColor color() {
        return color == null ? MapColor.COLOR_BLACK : color;
    }

    public BlockBehaviour.Properties toSettings() throws InitializationException {
        BlockBehaviour.Properties builder = createBuilder();
        applyNonNull(sound, builder::sound);
        applyNonNull(light, builder::lightLevel);
        applyNonNull(slipperiness, builder::friction);
        applyNonNull(solid, false, builder::noOcclusion);
        applyNonNull(randomTick, true, builder::randomTicks);

        applyNonNull(dynamicBounds, true, builder::dynamicShape);
        applyNonNull(variableOpacity, true, builder::dynamicShape);
        applyNonNull(blocksMovement, false, builder::noCollision);
        if (customOffsetType != null) {
            // Cast builder to custom interface
            builder = ((BlockSettingsAccessor)builder).setCustomOffsetter(customOffsetType);
        }
        applyNonNull(offset, builder::offsetType);
        if (hardness != null && resistance != null) {
            builder.strength(hardness, resistance);
        }
        return builder;
    }


    private BlockBehaviour.Properties createBuilder() throws InitializationException {
        BlockBehaviour.Properties props;

        if (block != null) {
            props = BlockBehaviour.Properties.ofFullCopy(block);
        } else if (color != null) {
            props = BlockBehaviour.Properties.of().mapColor(color);
        } else {
            throw new InitializationException("Block.Builder requires a Material");
        }

        return props;
    }

    private static <T> void applyNonNull(Boolean value, boolean condition, Runnable runnable) {
        if (value != null && value == condition) {
            runnable.run();
        }
    }

    private static <T> void applyNonNull(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    //todo finish this i guess?
    protected abstract <T> void applyNonNull(Integer light, Consumer<T> lightLevel);

}
