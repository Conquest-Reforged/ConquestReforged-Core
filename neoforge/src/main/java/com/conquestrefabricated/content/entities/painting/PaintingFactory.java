package com.conquestrefabricated.content.entities.painting;

import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.content.entities.painting.art.ArtType;
import com.conquestrefabricated.content.entities.painting.art.ModArt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.Level;

public interface PaintingFactory<T extends HangingEntity> {

    T create(Level world, BlockPos pos, Direction side, String typeName, String artName);

    PaintingFactory<EntityPainting> MOD = (world, pos, side, typeName, artName) -> {
        ModPainting type = ModPainting.fromName(typeName);
        Art<ArtType> art = ModArt.fromName(artName);
        if (type == null || art == null) {
            return null;
        }
        return new EntityPainting(world, pos, side, type, art.getReference());
    };

//    PaintingFactory<PaintingEntity> VANILLA = (world, pos, side, typeName, artName) -> {
//        Art<PaintingVariant> art = VanillaArt.fromName(artName);
//        if (art == null) {
//            return new PaintingEntity(world, pos, side, Registries.PAINTING_VARIANT.createEntry(art.getReference()));
//        } else {
//            PaintingEntity painting = new PaintingEntity(world, pos, side, Registries.PAINTING_VARIANT.getEntry(Registries.PAINTING_VARIANT.getRawId(art.getReference())).get());
//            // can only set the art after creation on the server side
//            //painting.motive = art.getReference();
//            // setPosition triggers a recalculation of the entity bounding box so should allow it to hang correctly
//            painting.setPosition(pos.getX(), pos.getY(), pos.getZ());
//            return painting;
//        }
//    };
}
