package com.conquestrefabricated.content.pottery;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.station.WorkstationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A pottery wheel at work. Everything is {@link WorkstationBlockEntity}; unlike a loom the wheel shows
 * nothing of what it holds, so there is nothing to add.
 *
 * <p>A wheel is two blocks, and only the foot carries this - see {@code PotteryWheel}, which sends
 * both halves here.</p>
 */
public class PotteryWheelBlockEntity extends WorkstationBlockEntity {

    public PotteryWheelBlockEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.POTTERY_WHEEL, pos, state);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PotteryWheelMenu(containerId, inventory, this, this.getDataAccess());
    }
}
