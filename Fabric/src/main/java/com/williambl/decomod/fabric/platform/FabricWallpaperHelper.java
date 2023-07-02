package com.williambl.decomod.fabric.platform;

import com.williambl.decomod.fabric.wallpaper.ChunkWallpaperComponent;
import com.williambl.decomod.platform.services.IWallpaperHelper;
import com.williambl.decomod.wallpaper.WallpaperApplierItem;
import com.williambl.decomod.wallpaper.WallpaperChunk;
import com.williambl.decomod.wallpaper.WallpaperType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class FabricWallpaperHelper implements IWallpaperHelper {
    @Override
    public @Nullable WallpaperChunk getWallpaperChunk(Level level, ChunkPos chunkPos) {
        return ChunkWallpaperComponent.KEY.getNullable(level.getChunk(chunkPos.x, chunkPos.z));
    }

    @Override
    public @Nullable WallpaperChunk getWallpaperChunk(ChunkAccess chunk) {
        return ChunkWallpaperComponent.KEY.getNullable(chunk);
    }

    @Override
    public Function<WallpaperType, WallpaperApplierItem> registerItemsForWallpapers() {
        return null; //TODO
    }
}
