package com.conquestrefabricated.content.blocks.tileentity;

import com.conquestrefabricated.content.blocks.tileentity.campfire.ModdedCampfireTileEntity;
import com.conquestrefabricated.content.blocks.tileentity.enchantment.ModdedEnchantingTableTileEntity;
import com.conquestrefabricated.content.blocks.tileentity.furnace.ModdedFurnaceTileEntity;
import com.conquestrefabricated.content.blocks.tileentity.loom.LoomBlockEntity;
import com.conquestrefabricated.content.blocks.tileentity.seat.SeatTileEntity;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;


// should not have any event listeners to avoid class-loading before blocks are registered
public class TileEntityTypes {

    public static void init() {
        //addVanilla(BlockEntityType.BED, "conquest:carved_spruce_wood_bed_with_bear_pelt");
    }

    public static final BlockEntityType<KilnTileEntity> KILN = create(
            KilnTileEntity::new,
            "conquest:kiln"
    );

    public static final BlockEntityType<LoomBlockEntity> LOOM = create(
            LoomBlockEntity::new,
            "conquest:loom"
    );

    public static final BlockEntityType<ModdedEnchantingTableTileEntity> ENCHANTING_TABLE = create(
            ModdedEnchantingTableTileEntity::new,
            "conquest:enchanter"
    );

    public static final BlockEntityType<ModdedCampfireTileEntity> CAMPFIRE = create(
            ModdedCampfireTileEntity::new,
            "conquest:campfire"
    );

    public static final BlockEntityType<ModdedFurnaceTileEntity> FURNACE = create(
            ModdedFurnaceTileEntity::new,
            "conquest:oven"
    );

    public static final BlockEntityType<AnimalTileEntity> ANIMAL = create(
            AnimalTileEntity::new,
            "conquest:animal"
    );

    public static final BlockEntityType<SeatTileEntity> SEAT = create(
            SeatTileEntity::new,
            "conquest:seat"
    );

    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> create(TileEntityFactory<T> factory, String name, String... blockNames) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }

    @ExpectPlatform
    public static void addVanilla(BlockEntityType vanillaBlockEntity, String... blockNames) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }

    @ExpectPlatform
    public static void add(BlockEntityType blockEntity, String... blockNames) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }
}
