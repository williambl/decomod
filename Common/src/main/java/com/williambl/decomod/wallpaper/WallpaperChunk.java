package com.williambl.decomod.wallpaper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface WallpaperChunk {
    WallpaperType addWallpaper(BlockPos pos, Direction dir, WallpaperType data);
    WallpaperType removeWallpaper(BlockPos pos, Direction dir);
    void removeWallpaper(BlockPos pos);
}
