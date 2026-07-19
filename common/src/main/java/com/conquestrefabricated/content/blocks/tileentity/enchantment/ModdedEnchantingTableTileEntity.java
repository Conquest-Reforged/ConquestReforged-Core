package com.conquestrefabricated.content.blocks.tileentity.enchantment;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ModdedEnchantingTableTileEntity extends BlockEntity implements Nameable {

    public int ticks;
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;
    private static final net.minecraft.util.RandomSource RANDOM = RandomSource.create();
    @Nullable
    private Component customName;

    public ModdedEnchantingTableTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.ENCHANTING_TABLE, pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("CustomName", ComponentSerialization.CODEC, this.customName);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.customName = parseCustomNameSafe(input, "CustomName");
    }

    public static void tick(final Level level, final BlockPos worldPosition, final BlockState state, final ModdedEnchantingTableTileEntity entity) {
        entity.oOpen = entity.open;
        entity.oRot = entity.rot;
        Player player = level.getNearestPlayer((double)worldPosition.getX() + (double)0.5F, (double)worldPosition.getY() + (double)0.5F, (double)worldPosition.getZ() + (double)0.5F, (double)3.0F, false);
        if (player != null) {
            double xd = player.getX() - ((double)worldPosition.getX() + (double)0.5F);
            double zd = player.getZ() - ((double)worldPosition.getZ() + (double)0.5F);
            entity.tRot = (float)Mth.atan2(zd, xd);
            entity.open += 0.1F;
            if (entity.open < 0.5F || RANDOM.nextInt(40) == 0) {
                float old = entity.flipT;

                do {
                    entity.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while(old == entity.flipT);
            }
        } else {
            entity.tRot += 0.02F;
            entity.open -= 0.1F;
        }

        while(entity.rot >= (float)Math.PI) {
            entity.rot -= ((float)Math.PI * 2F);
        }

        while(entity.rot < -(float)Math.PI) {
            entity.rot += ((float)Math.PI * 2F);
        }

        while(entity.tRot >= (float)Math.PI) {
            entity.tRot -= ((float)Math.PI * 2F);
        }

        while(entity.tRot < -(float)Math.PI) {
            entity.tRot += ((float)Math.PI * 2F);
        }

        float rotDir;
        for(rotDir = entity.tRot - entity.rot; rotDir >= (float)Math.PI; rotDir -= ((float)Math.PI * 2F)) {
        }

        while(rotDir < -(float)Math.PI) {
            rotDir += ((float)Math.PI * 2F);
        }

        entity.rot += rotDir * 0.4F;
        entity.open = Mth.clamp(entity.open, 0.0F, 1.0F);
        ++entity.time;
        entity.oFlip = entity.flip;
        float diff = (entity.flipT - entity.flip) * 0.4F;
        float max = 0.2F;
        diff = Mth.clamp(diff, -0.2F, 0.2F);
        entity.flipA += (diff - entity.flipA) * 0.9F;
        entity.flip += entity.flipA;
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : Component.translatable("container.enchant");

    }

    public void setCustomName(@Nullable Component customName) {
        this.customName = customName;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.customName;
    }

    @Override
    protected void applyImplicitComponents(final DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.customName = (Component)components.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder componentMapBuilder) {
        super.collectImplicitComponents(componentMapBuilder);
        componentMapBuilder.set(DataComponents.CUSTOM_NAME, this.customName);
    }

    public void removeComponentsFromTag(final ValueOutput output) {
        output.discard("CustomName");
    }
}

