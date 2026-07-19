package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.core.asset.annotation.*;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;

@Assets(
        state = @State(name = "%s_boards", template = "parent_boards_vertical"),
        item = @Model(name = "item/%s_boards", parent = "block/%s_boards_horizontal_long_thin", template = "item/acacia_slab"),
        render = @Render(RenderLayer.CUTOUT)
)

@ItemDescription(description = "board_toggle")
public class BoardsVertical extends BoardsHorizontal {

    public BoardsVertical(Props properties) {
        super(properties);
    }
}