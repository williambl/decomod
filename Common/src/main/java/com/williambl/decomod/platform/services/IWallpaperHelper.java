package com.williambl.decomod.platform.services;

import com.williambl.decomod.wallpaper.WallpaperApplierItem;
import com.williambl.decomod.wallpaper.WallpaperChunk;
import com.williambl.decomod.wallpaper.WallpaperType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IWallpaperHelper {
    @Nullable WallpaperChunk getWallpaperChunk(Level level, ChunkPos chunkPos);
    @Nullable WallpaperChunk getWallpaperChunk(ChunkAccess chunk);

    Function<WallpaperType, WallpaperApplierItem> registerItemsForWallpapers();
}
