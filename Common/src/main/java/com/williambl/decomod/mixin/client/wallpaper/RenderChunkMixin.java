package com.williambl.decomod.mixin.client.wallpaper;

import com.williambl.decomod.client.WallpaperRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderDispatcher.RenderChunk.class)
public class RenderChunkMixin {
    @Shadow @Final BlockPos.MutableBlockPos origin;

    @Inject(method = "setDirty", at = @At("TAIL"))
    private void decomod$setWallpapersDirty(boolean $$0, CallbackInfo ci) {
        WallpaperRenderer.CACHE.markDirty(new ChunkPos(this.origin));
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void decomod$setWallpapersDirtyWhenReset(CallbackInfo ci) {
        WallpaperRenderer.CACHE.markDirty(new ChunkPos(this.origin));
    }
}
