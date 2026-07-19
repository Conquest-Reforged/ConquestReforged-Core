package com.conquestrefabricated.content.entities.seat;

import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.block.SlabLessLayers;
import com.conquestrefabricated.content.entities.EntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import com.conquestrefabricated.content.blocks.util.Sittable;

public class SeatEntity extends Entity {

    private BlockState seat;

    public SeatEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public SeatEntity(Level world) {
        this(EntityTypes.SEAT, world);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        Block block = getSeat().getBlock();
        if (!(block instanceof Sittable)) {
            if (this.level() instanceof ServerLevel serverLevel) {
                kill(serverLevel);
            }
            return Vec3.ZERO;
        }
        Sittable seat = (Sittable) block;
        double baseHeight = seat.getSeatHeight();

        BlockPos belowPos = this.blockPosition().relative(Direction.DOWN);
        BlockState belowState = this.level().getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();

        if (belowBlock instanceof Layer || belowBlock instanceof Slab) {
            int layers = belowState.getValue(BlockStateProperties.LAYERS);
            double layerHeight = (8 - layers) * 0.125;
            return new Vec3(0, baseHeight - layerHeight, 0);
        } else if (belowState.getBlock() instanceof SlabLessLayers) {
            int layers = belowState.getValue(SlabLessLayers.LAYERS);
            double layerHeight = switch (layers) {
                case 1 -> 0.875D;
                case 2 -> 0.75D;
                case 3 -> 0.5D;
                case 4 -> 0.25D;
                default -> 0D;
            };
            return new Vec3(0, baseHeight - layerHeight, 0);
        }
        return new Vec3(0, baseHeight, 0);
    }

    @Override
    public float getPickRadius() {
        return 0.0f;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + getSeat().hashCode();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void move(MoverType type, Vec3 pos) {

    }

    @Override
    public void playerTouch(Player player) {

    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            return;
        }
        if (getSeat().isAir() || !this.isVehicle()) {
            kill((ServerLevel) this.level());
        }
    }

    @Override
    protected boolean canRide(Entity entity) {
        return entity instanceof Player;
    }

    @Override
    protected boolean isAffectedByBlocks() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entry) {
        return new ClientboundAddEntityPacket(this, entry);
    }

    public BlockState getSeat() {
        if (this.seat == null) {
            this.seat = this.level().getBlockState(blockPosition());
        }
        return this.seat;
    }
}
