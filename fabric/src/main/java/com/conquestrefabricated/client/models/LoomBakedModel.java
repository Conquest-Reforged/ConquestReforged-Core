package com.conquestrefabricated.client.models;

import com.conquestrefabricated.content.blocks.block.decor.Loom;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.tileentity.loom.LoomBlockEntity;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class LoomBakedModel extends WrapperBlockStateModel {

    private final BlockStateModel extraModel;
    private final TextureAtlasSprite[] sprites;

    public LoomBakedModel(BlockStateModel baseModel, BlockStateModel extraModel, TextureAtlasSprite[] sprites) {
        super(baseModel);
        this.extraModel = extraModel;
        this.sprites = sprites;
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state,
                          RandomSource random, Predicate<@Nullable Direction> cullTest) {
        BlockEntity blockEntity = blockView.getBlockEntity(pos);

        TextureAtlasSprite sprite = sprites[0];

        if (state.getValue(Loom.HAS_THREAD)) {
            if (blockEntity != null && blockEntity.getType() == TileEntityTypes.LOOM) {
                String productData = ((LoomBlockEntity) blockEntity).getProduct();
                sprite = sprites[getTextureVariant(productData)];
            }
            TextureAtlasSprite originalSprite = sprites[getTextureVariant("conquest:white_canvas")];
            TextureAtlasSprite finalSprite = sprite;

            emitter.pushTransform(quad -> {
                float u0 = quad.u(0), u1 = quad.u(1), u2 = quad.u(2), u3 = quad.u(3);
                float v0 = quad.v(0), v1 = quad.v(1), v2 = quad.v(2), v3 = quad.v(3);

                float spriteWidth = originalSprite.getU1() - originalSprite.getU0();
                float spriteHeight = originalSprite.getV1() - originalSprite.getV0();

                float nu0 = (u0 - originalSprite.getU0()) / spriteWidth;
                float nu1 = (u1 - originalSprite.getU0()) / spriteWidth;
                float nu2 = (u2 - originalSprite.getU0()) / spriteWidth;
                float nu3 = (u3 - originalSprite.getU0()) / spriteWidth;

                float nv0 = (v0 - originalSprite.getV0()) / spriteHeight;
                float nv1 = (v1 - originalSprite.getV0()) / spriteHeight;
                float nv2 = (v2 - originalSprite.getV0()) / spriteHeight;
                float nv3 = (v3 - originalSprite.getV0()) / spriteHeight;

                quad.materialBake(new Material.Baked(finalSprite, false), 0);

                quad.uv(0, finalSprite.getU0() + nu0 * (finalSprite.getU1() - finalSprite.getU0()),
                        finalSprite.getV0() + nv0 * (finalSprite.getV1() - finalSprite.getV0()));
                quad.uv(1, finalSprite.getU0() + nu1 * (finalSprite.getU1() - finalSprite.getU0()),
                        finalSprite.getV0() + nv1 * (finalSprite.getV1() - finalSprite.getV0()));
                quad.uv(2, finalSprite.getU0() + nu2 * (finalSprite.getU1() - finalSprite.getU0()),
                        finalSprite.getV0() + nv2 * (finalSprite.getV1() - finalSprite.getV0()));
                quad.uv(3, finalSprite.getU0() + nu3 * (finalSprite.getU1() - finalSprite.getU0()),
                        finalSprite.getV0() + nv3 * (finalSprite.getV1() - finalSprite.getV0()));

                return true;
            });
            wrapped.emitQuads(emitter, blockView, pos, state, random, cullTest);
            emitter.popTransform();
        }

        emitter.pushTransform(quad -> true);
        extraModel.emitQuads(emitter, blockView, pos, state, random, cullTest);
        emitter.popTransform();
    }

    private int getTextureVariant(String productData) {
        return switch (productData) {
            case "conquest:red_canvas" -> 1;
            case "conquest:black_canvas" -> 2;
            case "conquest:gray_canvas" -> 3;
            case "conquest:light_gray_canvas" -> 4;
            case "conquest:white_canvas" -> 5;
            case "conquest:brown_canvas" -> 6;
            case "conquest:yellow_canvas" -> 7;
            case "conquest:orange_canvas" -> 8;
            case "conquest:pink_canvas" -> 9;
            case "conquest:magenta_canvas" -> 10;
            case "conquest:purple_canvas" -> 11;
            case "conquest:blue_canvas" -> 12;
            case "conquest:light_blue_canvas" -> 13;
            case "conquest:cyan_canvas" -> 14;
            case "conquest:green_canvas" -> 15;
            case "conquest:lime_canvas" -> 16;
            case "conquest:baotuo_rug" -> 17;
            case "conquest:berber_rug" -> 18;
            case "conquest:black_persian_rug" -> 19;
            case "conquest:blue_nain_rug" -> 20;
            case "conquest:brown_oriental_carpet" -> 21;
            case "conquest:celtic_knot_rug" -> 22;
            case "conquest:kashmiri_carpet" -> 23;
            case "conquest:kazakh_rug" -> 24;
            case "conquest:kilim_rug" -> 25;
            case "conquest:nahavand_rug" -> 26;
            case "conquest:red_and_blue_sarouk_rug" -> 27;
            case "conquest:red_oriental_carpet" -> 28;
            case "conquest:red_pazyryk_rug" -> 29;
            case "conquest:shirishabad_rug" -> 30;
            case "conquest:william_morris_rug" -> 31;
            case "conquest:yellow_red_persian_rug" -> 32;
            default -> 0;
        };
    }
}