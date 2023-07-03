package com.williambl.decomod.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.williambl.decomod.platform.Services;
import com.williambl.decomod.wallpaper.WallpaperChunk;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class WallpaperRenderer {
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
        stack.pushPose();
        for (var block : wallpaperChunk) {
            var pos = block.getKey();
            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = level.getBrightness(LightLayer.SKY, pos);
            int packedLight = LightTexture.pack(blockLight, skyLight);
            stack.translate(pos.getX(), pos.getY(), pos.getZ());

            for (var dir : Direction.values()) {
                var wallpaper = block.getValue().get(dir);
                if (wallpaper == null) {
                    continue;
                }

                int tint = wallpaper.getTint();
                RenderType renderType = RenderType.cutoutMipped();
                var vertexConsumer = buffers.getBuffer(renderType);

                modelBlockRenderer.renderModel(
                        stack.last(),
                        vertexConsumer,
                        null,
                        modelManager.getModelFromResLoc(wallpaper.getModelLocation(dir)),
                        FastColor.ARGB32.red(tint),
                        FastColor.ARGB32.green(tint),
                        FastColor.ARGB32.blue(tint),
                        packedLight,
                        OverlayTexture.NO_OVERLAY
                );
            }
        }
        stack.popPose();
    }
}
