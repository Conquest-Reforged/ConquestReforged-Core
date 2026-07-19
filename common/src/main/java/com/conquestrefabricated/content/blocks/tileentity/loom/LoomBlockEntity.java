package com.conquestrefabricated.content.blocks.tileentity.loom;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LoomBlockEntity extends BlockEntity {
    private String product = "test";

    public LoomBlockEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.LOOM, pos, state);
    }

    public void tick(Level world, BlockPos pos) {

    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        product = input.getStringOr("product", "test");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("product", product);
    }

    //Getters and setters
    public String getProduct() { return product; }
    public void setProduct(String value) {
        product = value;
        //markDirty();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }
}
