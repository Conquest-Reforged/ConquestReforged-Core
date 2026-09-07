package com.conquestrefabricated.content.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** Right-click with a set of crafting tools to open its picker. */
public class CraftingToolItem extends Item {

    private final CraftingTool tool;

    public CraftingToolItem(CraftingTool tool, Properties properties) {
        super(properties);
        this.tool = tool;
    }

    public CraftingTool tool() {
        return this.tool;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, opener) -> new ToolCraftingMenu(this.tool, containerId, inventory),
                    this.tool.title()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Component.translatable(this.tool.tooltipKey()));
    }
}
