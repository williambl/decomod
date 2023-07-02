package com.williambl.decomod.wallpaper;

import com.williambl.decomod.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class WallpaperScraperItem extends Item {
    public WallpaperScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Services.WALLPAPERS.getWallpaperChunk(ctx.getLevel().getChunk(ctx.getClickedPos()))
                .removeWallpaper(ctx.getClickedPos().immutable(), ctx.getClickedFace());

        return InteractionResult.SUCCESS;
    }
}
