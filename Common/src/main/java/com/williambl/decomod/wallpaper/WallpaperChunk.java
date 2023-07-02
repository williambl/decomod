package com.williambl.decomod.wallpaper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

public interface WallpaperChunk extends Iterable<Map.Entry<BlockPos, EnumMap<Direction, WallpaperType>>> {
    WallpaperType addWallpaper(BlockPos pos, Direction dir, WallpaperType data);
    WallpaperType removeWallpaper(BlockPos pos, Direction dir);
    void removeWallpaper(BlockPos pos);
}
