package com.williambl.decomod.client;

import com.ibm.icu.impl.CacheValue;
import com.williambl.decomod.Constants;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WallpaperModelCache {
    private ResourceKey<Level> currentLevel = null;
    private final Long2ObjectMap<CacheValue[]> cachedChunks = new Long2ObjectOpenHashMap<>(256);

    public void setLevel(ResourceKey<Level> newLevel) {
        if (!Objects.equals(this.currentLevel, newLevel)) {
            Constants.LOGGER.debug("Level changed to {}, clearing wallpaper caches.", newLevel.location());
            this.cachedChunks.clear();
        }

        this.currentLevel = newLevel;
    }

    public CacheValue[] getOrCalculate(ChunkPos pos, Supplier<Stream<CacheValue>> calculator) {
        return this.cachedChunks.computeIfAbsent(pos.toLong(), $ -> calculator.get().toArray(CacheValue[]::new));
    }

    public boolean markDirty(ChunkPos pos) {
        return this.cachedChunks.remove(pos.toLong()) != null;
    }

    record CacheValue(BlockPos pos, int tint, BakedQuad quad, float[] brightness, int[] lightmap) {}
}
