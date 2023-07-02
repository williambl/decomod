package com.williambl.decomod.platform.services;

import com.williambl.decomod.wallpaper.WallpaperChunk;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

public interface IWallpaperHelper {
    WallpaperChunk getWallpaperChunk(ChunkPos chunkPos);
    WallpaperChunk getWallpaperChunk(ChunkAccess chunk);
}
