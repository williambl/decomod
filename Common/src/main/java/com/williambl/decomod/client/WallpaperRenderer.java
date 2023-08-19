package com.williambl.decomod.client;

import com.google.common.collect.Streams;
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
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class WallpaperRenderer {
    public static final WallpaperModelCache CACHE = new WallpaperModelCache();
    private static final Direction[] DIRECTIONS = Direction.values();

    public static void renderWallpapers(
            PoseStack poseStack,
            MultiBufferSource buffers,
            ModelBlockRenderer modelRenderer,
            Level level,
            ExtendedModelManager modelManager,
            Camera camera,
            int chunkViewDistance,
            ProfilerFiller profiler
    ) {
        profiler.push("wallpaperRendering");
        RenderType renderType = RenderType.cutoutMipped();
        var vertexConsumer = buffers.getBuffer(renderType);
        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x(), -camera.getPosition().y(), -camera.getPosition().z());
        getChunkPositions(camera, chunkViewDistance)
                .map(pos -> CACHE.getOrCalculate(pos, () -> WallpaperRenderer.tesselateWallpapersForChunk(modelRenderer, level, modelManager, pos)))
                .forEach(cacheValues -> {
                    for (var value : cacheValues) {
                        poseStack.pushPose();
                        poseStack.translate(value.pos().getX(), value.pos().getY(), value.pos().getZ());
                        putQuadData(vertexConsumer, poseStack.last(), value.quad(), value.brightness(), value.lightmap(), OverlayTexture.NO_OVERLAY, value.tint());
                        poseStack.popPose();
                    }
                });
        poseStack.popPose();
        profiler.pop();
    }

    private static Stream<WallpaperModelCache.CacheValue> tesselateWallpapersForChunk(
            ModelBlockRenderer modelRenderer,
            Level level,
            ExtendedModelManager modelManager,
            ChunkPos chunkPos
    ) {
        var wallpaperChunk = Services.WALLPAPERS.getWallpaperChunk(level, chunkPos);
        if (wallpaperChunk != null) {
            return Streams.stream(wallpaperChunk)
                    .flatMap(entry -> {
                        var pos = entry.getKey();
                        var wallpapers = entry.getValue();
                        var state = level.getBlockState(pos);
                        return Arrays.stream(DIRECTIONS)
                                .flatMap(dir -> {
                                    var wallpaper = wallpapers.get(dir);
                                    return wallpaper != null ? calculateQuadsWithAO(
                                            level,
                                            modelManager.getModelFromResLoc(wallpaper.getModelLocation(dir)),
                                            wallpaper.getTint(),
                                            level.getBlockState(pos),
                                            pos,
                                            false,
                                            RandomSource.create(),
                                            state.getSeed(pos),
                                            modelRenderer
                                    ) : Stream.empty();
                                });
                    });
        }

        return Stream.empty();
    }

    private static Stream<ChunkPos> getChunkPositions(Camera camera, int chunkViewDistance) {
        int minChunkX = (camera.getBlockPosition().getX() >> 4) - chunkViewDistance;
        int minChunkZ = (camera.getBlockPosition().getZ() >> 4) - chunkViewDistance;
        return IntStream.rangeClosed(minChunkX, minChunkX + 1 + (2 * chunkViewDistance))
                .mapToObj(x -> IntStream.rangeClosed(minChunkZ, minChunkZ + 1 + (2 * chunkViewDistance)).mapToObj(z -> new ChunkPos(x, z)))
                .flatMap(Function.identity());
    }

    private static Stream<WallpaperModelCache.CacheValue> calculateAOForFaces(BlockAndTintGetter level, BlockState state, BlockPos pos, List<BakedQuad> quads, float[] dirFloats, BitSet bits, ModelBlockRenderer.AmbientOcclusionFace ao, ModelBlockRenderer renderer, int tint) {
        return quads.stream().map(quad -> {
            renderer.calculateShape(level, state, pos, quad.getVertices(), quad.getDirection(), dirFloats, bits);
            ao.calculate(level, state, pos, quad.getDirection(), dirFloats, bits, quad.isShade());
            return new WallpaperModelCache.CacheValue(pos, tint, quad, ao.brightness, ao.lightmap);
        });
    }

    public static Stream<WallpaperModelCache.CacheValue> calculateQuadsWithAO(BlockAndTintGetter level, BakedModel model, int tint, BlockState state, BlockPos pos, boolean occlude, RandomSource rand, long seed, ModelBlockRenderer renderer) {
        Stream<WallpaperModelCache.CacheValue> res = Stream.empty();
        float[] dirFloats = new float[DIRECTIONS.length * 2];
        BitSet bits = new BitSet(3);
        ModelBlockRenderer.AmbientOcclusionFace ao = new ModelBlockRenderer.AmbientOcclusionFace();
        BlockPos.MutableBlockPos mPos = pos.mutable();
        for (Direction dir : DIRECTIONS) {
            rand.setSeed(seed);
            List<BakedQuad> quads = model.getQuads(state, dir, rand);
            if (!quads.isEmpty()) {
                mPos.setWithOffset(pos, dir);
                if (!occlude || Block.shouldRenderFace(state, level, pos, dir, mPos)) {
                    res = Stream.concat(res, calculateAOForFaces(level, state, pos, quads, dirFloats, bits, ao, renderer, tint));
                }
            }
        }

        rand.setSeed(seed);
        List<BakedQuad> quads = model.getQuads(state, null, rand);
        if (!quads.isEmpty()) {
            res = Stream.concat(res, calculateAOForFaces(level, state, pos, quads, dirFloats, bits, ao, renderer, tint));
        }

        return res;
    }

    private static void putQuadData(VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, float[] brightness, int[] lightmap, int overlay, int tint) {
        float r;
        float g;
        float b;
        if (quad.isTinted()) {
            r = (float) (tint >> 16 & 255) / 255.0F;
            g = (float) (tint >> 8 & 255) / 255.0F;
            b = (float) (tint & 255) / 255.0F;
        } else {
            r = 1.0F;
            g = 1.0F;
            b = 1.0F;
        }

        consumer.putBulkData(pose, quad, brightness, r, g, b, lightmap, overlay, true);
    }
}
