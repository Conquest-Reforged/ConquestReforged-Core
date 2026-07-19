package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.properties.Waterloggable;
import net.minecraft.world.level.block.state.properties.WoodType;

@Assets(
        state = @State(name = "%s_fence_gate", template = "parent_fence_gate_horizontal"),
        item = @Model(name = "item/%s_fence_gate", parent = "block/%s_fence_gate", template = "item/acacia_fence_gate"),
        block = {
                @Model(name = "block/%s_fence_gate", template = "block/parent_fence_gate_horizontal"),
                @Model(name = "block/%s_fence_gate_open", template = "block/parent_fence_gate_horizontal_open")
        }
)
public class FenceGateHorizontal extends FenceGate implements Waterloggable {
    public FenceGateHorizontal(Properties properties, WoodType type) {
        super(properties, type);
    }
}