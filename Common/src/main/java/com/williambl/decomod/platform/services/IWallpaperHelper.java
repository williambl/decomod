package com.williambl.decomod.platform.services;

import com.williambl.decomod.wallpaper.WallpaperApplierItem;
import com.williambl.decomod.wallpaper.WallpaperChunk;
import com.williambl.decomod.wallpaper.WallpaperType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.function.Function;

public interface IWallpaperHelper {
    WallpaperChunk getWallpaperChunk(ChunkPos chunkPos);
    WallpaperChunk getWallpaperChunk(ChunkAccess chunk);

    Function<WallpaperType, WallpaperApplierItem> registerItemsForWallpapers();
}
