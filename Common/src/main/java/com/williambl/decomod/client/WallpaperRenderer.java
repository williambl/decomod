package com.williambl.decomod.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.williambl.decomod.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;

import java.util.List;

public final class WallpaperRenderer {
    public static void renderWallpapersForChunk(PoseStack stack,
                                          MultiBufferSource buffers,
                                          ModelBlockRenderer modelBlockRenderer,
                                          BlockAndTintGetter level,
                                          ExtendedModelManager modelManager,
                                          ChunkPos chunkPos) {
        var wallpaperChunk = Services.WALLPAPERS.getWallpaperChunk(chunkPos);
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
    }
}
