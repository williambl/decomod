package com.williambl.decomod.wallpaper;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class DoubleWallpaperType extends WallpaperType {
    private final Supplier<WallpaperType> left;
    private final Supplier<WallpaperType> right;

    private DoubleWallpaperType(Supplier<WallpaperType> left, Supplier<WallpaperType> right) {
        this.left = left == null ? () -> this : left;
        this.right = right == null ? () -> this : right;
    }

    public static DoubleWallpaperType createLeft(Supplier<WallpaperType> right) {
        return new DoubleWallpaperType(null, right);
    }

    public static DoubleWallpaperType createRight(Supplier<WallpaperType> left) {
        return new DoubleWallpaperType(left, null);
    }

    @Override
    public WallpaperType getVariant(UseOnContext ctx) {
        var location = ctx.getClickLocation();
        var centreOfClickedFace = ctx.getClickedPos().getCenter().add(Vec3.atLowerCornerOf(ctx.getClickedFace().getNormal()).scale(0.5));
        var diff = location.with(Direction.Axis.Y, 0).subtract(centreOfClickedFace.with(Direction.Axis.Y, 0));
        boolean reverseCheck = ctx.getClickedFace() == Direction.SOUTH || ctx.getClickedFace() == Direction.EAST;
        if (reverseCheck ? (diff.x < 0 || diff.z > 0) : (diff.x > 0 || diff.z < 0)) {
            return this.left.get();
        } else {
            return this.right.get();
        }
    }
}
