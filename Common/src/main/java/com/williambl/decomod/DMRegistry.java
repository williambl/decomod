package com.williambl.decomod;

import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.util.Pair;
import com.williambl.decomod.platform.Services;
import com.williambl.decomod.wallpaper.WallpaperApplierItem;
import com.williambl.decomod.wallpaper.WallpaperScraperItem;
import com.williambl.decomod.wallpaper.WallpaperType;
import net.minecraft.core.Registry;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class DMRegistry {
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> CUSTOM_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("custom_door", BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR), new Item.Properties());
    public static final Supplier<IronBarsBlock> IRON_FENCE =
            Services.REGISTRATION_HELPER.registerBlock("iron_fence", () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)) {}, new Item.Properties());

    public static final Supplier<Registry<WallpaperType>> WALLPAPER_REGISTRY =
            Services.REGISTRATION_HELPER.registerRegistry("wallpaper_type");

    public static final Supplier<WallpaperScraperItem> WALLPAPER_SCRAPER =
            Services.REGISTRATION_HELPER.registerItem("wallpaper_scraper", () -> new WallpaperScraperItem(new Item.Properties().stacksTo(1)));

    public static final Function<WallpaperType, WallpaperApplierItem> WALLPAPER_ITEMS =
            Services.WALLPAPERS.registerItemsForWallpapers();
}
