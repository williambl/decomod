package com.williambl.decomod.client;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class WallpaperModelCache {
    private final Long2ObjectMap<CacheValue[]> cachedChunks = new Long2ObjectOpenHashMap<>(256);

    public CacheValue[] getOrCalculate(ChunkPos pos, Supplier<Stream<CacheValue>> calculator) {
        return this.cachedChunks.computeIfAbsent(pos.toLong(), $ -> calculator.get().toArray(CacheValue[]::new));
    }

    public boolean markDirty(ChunkPos pos) {
        return this.cachedChunks.remove(pos.toLong()) != null;
    }

    record CacheValue(BlockPos pos, int tint, BakedQuad quad, float[] brightness, int[] lightmap) {}
}
