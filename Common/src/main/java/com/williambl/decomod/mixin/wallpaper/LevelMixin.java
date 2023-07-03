package com.williambl.decomod.mixin.wallpaper;

import com.williambl.decomod.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Level.class)
public class LevelMixin {
	@Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void decomod$clearWallpaper(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir, LevelChunk levelChunk) {
		if (!blockState.isAir()) {
			return;
		}
		var wallpaperChunk = Services.WALLPAPERS.getWallpaperChunk(levelChunk);
		if (wallpaperChunk == null) {
			return;
		}

		wallpaperChunk.removeWallpaper(blockPos);
	}
}