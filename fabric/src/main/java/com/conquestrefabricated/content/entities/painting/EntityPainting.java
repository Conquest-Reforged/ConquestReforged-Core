package com.conquestrefabricated.content.entities.painting;

import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.content.entities.painting.art.ArtType;
import com.conquestrefabricated.content.entities.painting.art.ModArt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * @author dags <dags@dags.me>
 */
public class EntityPainting extends HangingEntity {

    private static final EntityDataAccessor<String> PAINTING_TYPE =
            SynchedEntityData.defineId(EntityPainting.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> PAINTING_ART =
            SynchedEntityData.defineId(EntityPainting.class, EntityDataSerializers.STRING);

    public EntityPainting(EntityType<EntityPainting> type, Level world) {
        super(type, world);
    }

    public EntityPainting(Level world, BlockPos pos, Direction facing, ModPainting type, ArtType art) {
        super(PaintingEntityType.PAINTING, world, pos);
        //this.direction = facing;
        this.entityData.set(PAINTING_TYPE, type.getName());
        this.entityData.set(PAINTING_ART, art.shapeId);
        this.setDirection(facing);
    }

    /// This method initializes the datatracker so we know which additional new fields we'll be tracking. Note that we're not setting the values here.
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PAINTING_TYPE, "");
        builder.define(PAINTING_ART, ArtType.A1x1_0.shapeId);
    }

    /// Once our datatracker is initialized and our fields are set in the constructor, we update.
    /// Not doing so will result in a "flicker" or "shift" when referencing something like the art type when calculating the bounding box/position
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (PAINTING_ART.equals(data) || PAINTING_TYPE.equals(data)) {
            this.recalculateBoundingBox();
        }
    }

    public ModPainting getPaintingType() {
        return ModPainting.fromId(entityData.get(PAINTING_TYPE));
    }

    public ArtType getArt() {
        return ArtType.fromName(entityData.get(PAINTING_ART));
    }

    public void setType(ModPainting type) {
        entityData.set(PAINTING_TYPE, type.getName());
    }

    public void setArt(ArtType art) {
        entityData.set(PAINTING_ART, art.shapeId);
        if (getDirection() != null) {
            setDirection(getDirection());
        }
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return getPaintingType().createStack(ModArt.of(getArt()));
    }

    @Override
    public void dropItem(ServerLevel level, @Nullable Entity causedBy) {
        if (level.getGameRules().get(GameRules.ENTITY_DROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);

            if (causedBy instanceof Player player && player.getAbilities().instabuild) {
                return;
            }

            ItemStack drop = getPaintingType().createStack(ModArt.of(getArt()));
            if (drop != ItemStack.EMPTY) {
                this.spawnAtLocation(level, drop, 0.0F);
            }
        }
    }

    /// This controls the hitbox, the visual (and actual?) position. It can also control the model -- if the bounding box is off then the painting can be invisible.
    @Override
    protected AABB calculateBoundingBox(BlockPos pos, Direction side) {
        ArtType artType = getArt();

        float f = 0.46875F;
        Vec3 vec3d = Vec3.atCenterOf(pos).relative(side, (double)-0.46875F);
        double d = this.getOffset(artType.sizeX / 16);
        double e = this.getOffset((artType.sizeY  / 16));
        Direction direction = side.getCounterClockWise();
        Vec3 vec3d2 = vec3d.relative(direction, d).relative(Direction.UP, e);
        Direction.Axis axis = side.getAxis();
        double g = axis == Direction.Axis.X ? (double)0.0625F : (artType.sizeX  / 16.0);
        double h = (artType.sizeY  / 16.0);
        double i = axis == Direction.Axis.Z ? (double)0.0625F : (artType.sizeX  / 16.0);
        return AABB.ofSize(vec3d2, g, h, i);

        /*
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        double wallOffset = 0.5 - (1.0 / 32.0); // push back into the wall
        double artTypeOffsetWidth = (artType.sizeX / 32.0) - 0.5; // push back into the wall
        double artTypeOffsetHeight = (artType.sizeY / 32.0) - 0.5; // push back into the wall

        int offsetX = side.getOffsetX();
        int offsetZ = side.getOffsetZ();
        //x -= side.getOffsetX() * wallOffset;
        //z -= side.getOffsetZ() * wallOffset;

        switch (side) {
            case SOUTH:
                x += artTypeOffsetWidth;
                z -= wallOffset;
                break;
            case NORTH:
                x -= artTypeOffsetWidth;
                z += wallOffset;
                break;
            case WEST:
                x += wallOffset;
                z += artTypeOffsetWidth;
                break;
            case EAST:
                x -= wallOffset;
                z -= artTypeOffsetWidth;
                break;
        }

        double halfW = (artType.sizeX  / 32.0);
        double halfH = (artType.sizeY / 32.0);
        double halfD = 1.0 / 32.0;



        double hw = (side.getAxis() == Direction.Axis.Z) ? halfW : halfD;
        double hd = (side.getAxis() == Direction.Axis.X) ? halfW : halfD;

        Box box = new Box(x - hw, y - halfH + artTypeOffsetHeight, z - hd, x + hw, y + halfH + artTypeOffsetHeight, z + hd);

        return box;*/
    }

    /// If the width or height is uneven we return 0 offset.
    private double getOffset(int length) {
        return length % 2 == 0 ? (double)0.5F : (double)0.0F;
    }

    public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        this.setPos(x, y, z);
    }

    @Override
    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entityTrackerEntry) {
        return new ClientboundAddEntityPacket(this, this.getDirection().get3DDataValue(), this.getPos());
    }

    /// We need to set the facing here, otherwise the rotation/facing won't be placed correctly.
    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
        //this.setPosition(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public boolean survives() {
        return true;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput compound) {
        compound.putByte("Facing", (byte) this.getDirection().get2DDataValue());
        super.addAdditionalSaveData(compound);
        compound.putString(Art.TYPE_TAG, entityData.get(PAINTING_TYPE));
        compound.putInt(Art.ART_TAG, getArt().index());
    }

    @Override
    public void readAdditionalSaveData(ValueInput compound) {
        String type = compound.getString(Art.TYPE_TAG).orElse("");
        int id = compound.getInt(Art.ART_TAG).orElse(0);
        entityData.set(PAINTING_TYPE, type);
        entityData.set(PAINTING_ART, ArtType.fromId(id).shapeId);
        this.setDirectionRaw(Direction.from2DDataValue(compound.getByteOr("Facing", (byte) 0)));
        super.readAdditionalSaveData(compound);
        this.setDirection(this.getDirection());
    }

    @Override
    public void snapTo(double x, double y, double z, float yaw, float pitch) {
        this.setPos(x, y, z);
    }
}