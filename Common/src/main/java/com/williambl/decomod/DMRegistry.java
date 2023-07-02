package com.williambl.decomod;

import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.util.Pair;
import com.williambl.decomod.platform.Services;
import com.williambl.decomod.wallpaper.*;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.awt.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.williambl.decomod.DecoMod.id;

public class DMRegistry {
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> CUSTOM_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("custom_door", BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR), new Item.Properties());
    public static final Supplier<IronBarsBlock> IRON_FENCE =
            Services.REGISTRATION_HELPER.registerBlock("iron_fence", () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)) {}, new Item.Properties());

    public static final Supplier<Registry<WallpaperType>> WALLPAPER_REGISTRY =
            Services.REGISTRATION_HELPER.registerRegistry("wallpaper_type", WallpaperType.class);

    public static final Supplier<WallpaperScraperItem> WALLPAPER_SCRAPER =
            Services.REGISTRATION_HELPER.registerItem("wallpaper_scraper", () -> new WallpaperScraperItem(new Item.Properties().stacksTo(1)));

    public static final Function<WallpaperType, WallpaperApplierItem> WALLPAPER_ITEMS =
            Services.WALLPAPERS.registerItemsForWallpapers();

    public static final Supplier<WallpaperingTableBlock> WALLPAPERING_TABLE_BLOCK =
            Services.REGISTRATION_HELPER.registerBlock("wallpapering_table", () -> new WallpaperingTableBlock(BlockBehaviour.Properties.copy(Blocks.CARTOGRAPHY_TABLE)));

    public static final Supplier<RecipeType<WallpaperingRecipe>> WALLPAPERING_RECIPE_TYPE =
            Services.REGISTRATION_HELPER.registerRecipeType("wallpapering", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return id("wallpapering").toString();
                }
            });

    public static final Supplier<RecipeSerializer<WallpaperingRecipe>> WALLPAPERING_RECIPE_SERIALIZER =
            Services.REGISTRATION_HELPER.registerRecipeSerializer("wallpapering", WallpaperingRecipe.Serializer::new);

    public static final Supplier<MenuType<WallpaperingTableMenu>> WALLPAPERING_TABLE_MENU =
            Services.REGISTRATION_HELPER.registerMenuType("wallpapering_table", WallpaperingTableMenu::new);
}
