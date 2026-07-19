package com.conquestrefabricated.client.models;

import com.conquestrefabricated.content.blocks.block.decor.Loom;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.mojang.math.OctahedralGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.renderer.v1.model.MeshQuadCollection; // remove if unused
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ConquestModelLoadingPlugin implements ModelLoadingPlugin {

    @Override
    public void initialize(Context pluginContext) {
        pluginContext.modifyBlockModelOnLoad().register((original, context) -> {
            if (original == null) return original;

            BlockState state = context.state();
            if (state == null) return original;

            Block block = state.getBlock();
            if (!(block instanceof Loom)) return original;
            if (!state.getValue(Loom.HAS_THREAD)) return original;

            String loomType = BuiltInRegistries.BLOCK.getKey(block).getPath();
            return handleLoomModel(state, loomType);
        });
    }

    private BlockStateModel.UnbakedRoot handleLoomModel(BlockState state, String loomType) {
        int size = state.getValue(Loom.SIZE);
        int position = state.getValue(Loom.POSITION);
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockModelRotation rotation = rotationFor(facing);

        String sizeName = switch (size) {
            case 1 -> "small";
            case 2 -> "medium";
            default -> "large";
        };
        String suffix45 = (position == 2) ? "_45" : "";

        Identifier baseModelId = Identifier.fromNamespaceAndPath("conquest",
                "block/looms/" + loomType + "_weave" + sizeSegment(size) + suffix45);
        Identifier extraModelId = Identifier.fromNamespaceAndPath("conquest",
                "block/looms/" + loomType + "_threaded" + sizeSegment(size) + suffix45);

        Vector3f translation = translationFor(size, position);

        return translation != null
                ? new LoomUnbakedModel(baseModelId, extraModelId, sizeName, rotation, translation)
                : new LoomUnbakedModel(baseModelId, extraModelId, sizeName, rotation);
    }

    private static String sizeSegment(int size) {
        return switch (size) {
            case 1 -> "_small";
            case 3 -> "_large";
            default -> "";
        };
    }

    private static BlockModelRotation rotationFor(Direction facing) {
        return switch (facing) {
            case EAST -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_Y_90);
            case SOUTH -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_Y_180);
            case WEST -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_Y_270);
            default -> BlockModelRotation.IDENTITY;
        };
    }

    private static Vector3f translationFor(int size, int position) {
        if (position == 3) {
            float mag = size == 3 ? 0.9f : 0.5f;
            return new Vector3f(mag, 0.0f, 0.0f);
        } else if (position == 4) {
            float mag = size == 3 ? -0.9f : -0.5f;
            return new Vector3f(mag, 0.0f, 0.0f);
        }
        return null;
    }
}