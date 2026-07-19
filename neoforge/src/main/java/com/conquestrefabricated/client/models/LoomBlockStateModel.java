package com.conquestrefabricated.client.models;

import com.conquestrefabricated.content.blocks.block.decor.Loom;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.tileentity.loom.LoomBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoomBlockStateModel implements BlockStateModel {

    private final BlockStateModel wrapped;
    private final TextureAtlasSprite[] sprites;

    public LoomBlockStateModel(BlockStateModel wrapped, TextureAtlasSprite[] sprites) {
        this.wrapped = wrapped;
        this.sprites = sprites;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        wrapped.collectParts(random, parts);
//        BlockStateModelPart extra = Minecraft.getInstance().getModelManager().getStandaloneModel(LoomStandaloneModels.LOOM_EXTRA);
//        if (extra != null) {
//            parts.add(extra);
//        }
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        List<BlockStateModelPart> baseParts = new ArrayList<>();
        wrapped.collectParts(level, pos, state, random, baseParts);

        if (state.getValue(Loom.HAS_THREAD)) {
            TextureAtlasSprite sprite = sprites[0];
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && blockEntity.getType() == TileEntityTypes.LOOM) {
                String productData = ((LoomBlockEntity) blockEntity).getProduct();
                sprite = sprites[getTextureVariant(productData)];
            }
            TextureAtlasSprite originalSprite = sprites[getTextureVariant("conquest:white_canvas")];
            TextureAtlasSprite finalSprite = sprite;

            for (BlockStateModelPart part : baseParts) {
                parts.add(new RetexturedPart(part, originalSprite, finalSprite));
            }

            String loomType = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
            int size = state.getValue(Loom.SIZE);
            boolean rotated = state.getValue(Loom.POSITION) == 2;
            String lookupKey = LoomStandaloneModels.lookupKey(loomType, size, rotated);
            StandaloneModelKey<BlockStateModelPart> key = LoomStandaloneModels.KEYS.get(lookupKey);

            if (key != null) {
                BlockStateModelPart extra = Minecraft.getInstance().getModelManager().getStandaloneModel(key);
                if (extra != null) {
                    Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    parts.add(new RetexturedPart(new RotatedPart(extra, facing), originalSprite, finalSprite));
                }
            }
        } else {
            parts.addAll(baseParts);
        }
    }

    @Override
    public Material.Baked particleMaterial() {
        return wrapped.particleMaterial();
    }

    @Override
    public int materialFlags() {
        return wrapped.materialFlags();
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

    private static class RetexturedPart implements BlockStateModelPart {
        private final BlockStateModelPart original;
        private final TextureAtlasSprite originalSprite;
        private final TextureAtlasSprite finalSprite;

        RetexturedPart(BlockStateModelPart original, TextureAtlasSprite originalSprite, TextureAtlasSprite finalSprite) {
            this.original = original;
            this.originalSprite = originalSprite;
            this.finalSprite = finalSprite;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction cullFace) {
            List<BakedQuad> quads = original.getQuads(cullFace);
            List<BakedQuad> result = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) {
                if (quad.materialInfo().sprite().contents().name().equals(originalSprite.contents().name())) {
                    result.add(retexture(quad, originalSprite, finalSprite));
                } else {
                    result.add(quad);
                }
            }
            return result;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return original.useAmbientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return original.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return original.materialFlags();
        }

        private static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite originalSprite, TextureAtlasSprite finalSprite) {
            float spriteWidth = originalSprite.getU1() - originalSprite.getU0();
            float spriteHeight = originalSprite.getV1() - originalSprite.getV0();

            long[] srcUv = {quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3()};
            long[] newUv = new long[4];
            for (int i = 0; i < 4; i++) {
                float u = UVPair.unpackU(srcUv[i]);
                float v = UVPair.unpackV(srcUv[i]);
                float nu = (u - originalSprite.getU0()) / spriteWidth;
                float nv = (v - originalSprite.getV0()) / spriteHeight;
                float newU = finalSprite.getU0() + nu * (finalSprite.getU1() - finalSprite.getU0());
                float newV = finalSprite.getV0() + nv * (finalSprite.getV1() - finalSprite.getV0());
                newUv[i] = UVPair.pack(newU, newV);
            }

            BakedQuad.MaterialInfo oldInfo = quad.materialInfo();
            BakedQuad.MaterialInfo newInfo = new BakedQuad.MaterialInfo(
                    finalSprite, oldInfo.layer(), oldInfo.itemRenderType(),
                    oldInfo.tintIndex(), oldInfo.shade(), oldInfo.lightEmission(), oldInfo.ambientOcclusion()
            );

            return new BakedQuad(
                    quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                    newUv[0], newUv[1], newUv[2], newUv[3],
                    quad.direction(),
                    newInfo
            );
        }
    }
}