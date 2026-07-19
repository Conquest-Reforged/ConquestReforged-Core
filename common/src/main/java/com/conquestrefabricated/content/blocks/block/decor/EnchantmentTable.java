package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.tileentity.enchantment.ModdedEnchantingTableMenu;
import com.conquestrefabricated.content.blocks.tileentity.enchantment.ModdedEnchantingTableTileEntity;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


@Render(RenderLayer.CUTOUT)
public class EnchantmentTable extends EnchantingTableBlock {

    public EnchantmentTable(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new ModdedEnchantingTableTileEntity(blockPos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? createTickerHelper(type, TileEntityTypes.ENCHANTING_TABLE, ModdedEnchantingTableTileEntity::tick) : null;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ModdedEnchantingTableTileEntity) {
            Component itextcomponent = ((Nameable) tileentity).getDisplayName();
            return new SimpleMenuProvider((p_220147_2_, p_220147_3_, p_220147_4_) -> new ModdedEnchantingTableMenu(p_220147_2_, p_220147_3_, ContainerLevelAccess.create(worldIn, pos)), itextcomponent);
        } else {
            return null;
        }
    }
}
