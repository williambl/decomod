package com.williambl.decomod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.williambl.decomod.platform.Services;
import com.williambl.decomod.wallpaper.WallpaperChunk;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class WallpaperRenderer {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static void renderWallpapers(
            PoseStack poseStack,
            MultiBufferSource buffers,
            ModelBlockRenderer modelRenderer,
            Level level,
            ExtendedModelManager modelManager,
            Camera camera,
            int chunkViewDistance
    ) {
        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x(), -camera.getPosition().y(), -camera.getPosition().z());
        getChunkPositions(camera, chunkViewDistance).forEach(chunkPos -> {
            var wallpaperChunk = Services.WALLPAPERS.getWallpaperChunk(level, chunkPos);
            if (wallpaperChunk != null) {
                WallpaperRenderer.renderWallpapersForChunk(
                        poseStack,
                        buffers,
                        modelRenderer,
                        level,
                        modelManager,
                        wallpaperChunk
                );
            }
        });
        poseStack.popPose();
    }

    private static Stream<ChunkPos> getChunkPositions(Camera camera, int chunkViewDistance) {
        int minChunkX = (camera.getBlockPosition().getX() >> 4) - chunkViewDistance;
        int minChunkZ = (camera.getBlockPosition().getZ() >> 4) - chunkViewDistance;
        return IntStream.rangeClosed(minChunkX, minChunkX+1+(2*chunkViewDistance))
                .mapToObj(x -> IntStream.rangeClosed(minChunkZ, minChunkZ+1+(2*chunkViewDistance)).mapToObj(z -> new ChunkPos(x, z)))
                .flatMap(Function.identity());
    }


    private static void renderWallpapersForChunk(PoseStack stack,
                                          MultiBufferSource buffers,
                                          ModelBlockRenderer modelBlockRenderer,
                                          BlockAndTintGetter level,
                                          ExtendedModelManager modelManager,
                                          WallpaperChunk wallpaperChunk) {
        for (var block : wallpaperChunk) {
            stack.pushPose();
            var pos = block.getKey();
            stack.translate(pos.getX(), pos.getY(), pos.getZ());

            for (var dir : Direction.values()) {
                var wallpaper = block.getValue().get(dir);
                if (wallpaper == null) {
                    continue;
                }

                int tint = wallpaper.getTint(); //TODO
                RenderType renderType = RenderType.cutoutMipped();
                var vertexConsumer = buffers.getBuffer(renderType);
                var state = level.getBlockState(pos);

                modelBlockRenderer.tesselateWithAO(level, modelManager.getModelFromResLoc(wallpaper.getModelLocation(dir)), state, pos, stack, vertexConsumer, false, RandomSource.create(), state.getSeed(pos), OverlayTexture.NO_OVERLAY);
            }
            stack.popPose();
        }
    }
}
