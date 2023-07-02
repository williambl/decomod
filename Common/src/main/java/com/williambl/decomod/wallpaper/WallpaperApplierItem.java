package com.williambl.decomod.wallpaper;

import com.williambl.decomod.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class WallpaperApplierItem extends Item {
    private final WallpaperType type;
    public WallpaperApplierItem(Properties properties, WallpaperType type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var wallpaperChunk = Services.WALLPAPERS.getWallpaperChunk(ctx.getLevel().getChunk(ctx.getClickedPos()));
        if (wallpaperChunk != null) {
            wallpaperChunk.addWallpaper(ctx.getClickedPos().immutable(), ctx.getClickedFace(), this.type);

            ctx.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        return this.type.getName();
    }
}
