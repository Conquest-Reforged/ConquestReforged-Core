package com.conquestrefabricated.core.block.data;

import com.conquestrefabricated.core.Modules;
import com.conquestrefabricated.core.util.log.Log;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.asset.lang.Lore;
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
    private final Identifier loreId;
    private final String moduleId;

    private final List<TagKey<Block>> tags = new ArrayList<>();

    private Item item = null;

    public BlockData(Block block, BlockTemplate template, BlockName blockName, Props props) {
        this.template = template;
        this.registryName = template.getRegistryName(blockName);
        this.blockName = blockName;
        this.block = block;
        this.props = props;
        this.loreId = Lore.familyId(props.getFamily(), blockName);
        String module = props.getModule();
        if (module == null) {
            // only reachable if a Props constructor stops setting it; say so rather than
            // quietly crediting the block to Core
            Log.warn("Block {} has no module set; crediting it to {}", registryName, Modules.CORE);
            module = Modules.CORE;
        }
        this.moduleId = module;
        Lore.declare(loreId, props.getLore());
        registerBlock(this);
    }

    /**
     * @return the family id this block's lore is shared under
     */
    public Identifier getLoreId() {
        return loreId;
    }

    /**
     * @return the Conquest module this block was registered by
     */
    public String getModuleId() {
        return moduleId;
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
                Identifier lore = loreId;
                String module = moduleId;

                item = new BlockItem(getBlock(), properties) {
                    @Override
                    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                        ItemDescription tooltipAnnotation = getBlock().getClass().getAnnotation(ItemDescription.class);
                        if (tooltipAnnotation != null) {
                            builder.accept(Component.translatable("tooltip.conquest.block." + tooltipAnnotation.description()));
                        }
                        // the family's lore comes after whatever the block class contributes,
                        // and stays collapsed until the expand key is held. Resolved here rather
                        // than at registration so it doesn't matter which member declared it.
                        Lore.append(lore, builder);

                        // which module shipped this block, on F3+H only
                        if (tooltipFlag.isAdvanced()) {
                            Modules.appendTooltip(module, builder);
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

    /**
     * Whether this is the block its family is built from - the cube rather than the slab cut from it.
     *
     * <p>A builder that never had a parent set registered one block and that block is its own root;
     * otherwise the parent is whatever {@code Props.parent(..)} points at, which for cutout families
     * lands on a copied {@code Props} whose parent was never filled in.</p>
     */
    public boolean isFamilyParent() {
        return !props.hasParent() || props.getParent().getBlock() == block;
    }
}
