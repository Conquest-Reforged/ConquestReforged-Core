package com.conquestrefabricated.core.block.data;

import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.BlockName;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.factory.InitializationException;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public class BlockData {

    public final Block block;
    private final Props props;
    private final BlockName blockName;
    private final BlockTemplate template;
    public final Identifier registryName;

    private final List<TagKey<Block>> tags = new ArrayList<>();

    private Item item = null;

    public BlockData(Block block, BlockTemplate template, BlockName blockName, Props props) {
        this.template = template;
        this.registryName = template.getRegistryName(blockName);
        this.blockName = blockName;
        this.block = block;
        this.props = props;
        registerBlock(this);
    }

    @ExpectPlatform
    public static void registerBlock(BlockData blockData) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }

    public Block getBlock() {
        return block;
    }

    public Item getItem() throws InitializationException {
        if (item == null) {
            Item.Properties properties = new Item.Properties();
            properties.setId(ResourceKey.create(Registries.ITEM, registryName));

            try {
                item = new BlockItem(getBlock(), properties) {
                    @Override
                    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                        ItemDescription tooltipAnnotation = getBlock().getClass().getAnnotation(ItemDescription.class);
                        if (tooltipAnnotation != null) {
                            builder.accept(Component.translatable("tooltip.conquest.block." + tooltipAnnotation.description()));
                        }
                    }
                };

                return item;
            } catch (Throwable t) {
                throw new InitializationException(t);
            }
        }
        return item;
    }

    public Props getProps() {
        return props;
    }

    public BlockName getBlockName() {
        return blockName;
    }

    public Identifier getRegistryName() {
        return registryName;
    }

    public List<TagKey<Block>> getTags() {
        return props.getTags();
    }
}
