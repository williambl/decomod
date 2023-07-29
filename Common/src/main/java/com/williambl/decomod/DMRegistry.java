package com.williambl.decomod;

import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.util.Pair;
import com.williambl.decomod.platform.Services;
import com.williambl.decomod.wallpaper.*;
import net.minecraft.Util;
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
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.williambl.decomod.DecoMod.id;

public class DMRegistry {
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> CUSTOM_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("custom_door", BlockSetType.ACACIA, BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR), new Item.Properties());
    public static final Supplier<IronBarsBlock> IRON_FENCE =
            Services.REGISTRATION_HELPER.registerBlock("iron_fence", () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)) {}, new Item.Properties());

    public static final Supplier<Registry<WallpaperType>> WALLPAPER_REGISTRY =
            Services.REGISTRATION_HELPER.registerRegistry("wallpaper_type", WallpaperType.class);

    public static final Supplier<WallpaperScraperItem> WALLPAPER_SCRAPER =
            Services.REGISTRATION_HELPER.registerItem("wallpaper_scraper", () -> new WallpaperScraperItem(new Item.Properties().stacksTo(1)));

    public static final Function<WallpaperType, Supplier<WallpaperApplierItem>> WALLPAPER_ITEMS = Util.make(() -> {
        Map<WallpaperType, Supplier<WallpaperApplierItem>> map = new HashMap<>();
        Services.REGISTRATION_HELPER.forAllRegistered(WALLPAPER_REGISTRY.get(), (wallpaperType, wallpaperId) -> {
            var id = id("wallpaper/"+wallpaperId.getNamespace()+"/"+wallpaperId.getPath());
            var item = Services.REGISTRATION_HELPER.registerItem(id, () -> new WallpaperApplierItem(new Item.Properties(), wallpaperType));
            map.put(wallpaperType, item);
        });
        return map::get;
    });

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

    public static final Supplier<WallpaperType> TEST_WALLPAPER =
            Services.REGISTRATION_HELPER.registerWallpaperType("test", WallpaperType::new);

    public static final Supplier<WallpaperType> IRON_BAND =
            Services.REGISTRATION_HELPER.registerWallpaperType("iron_band", WallpaperType::new);

    public static final Supplier<WallpaperType> ACACIA_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("acacia_trim", WallpaperType::new);

    public static final Supplier<WallpaperType> BIRCH_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("birch_trim", WallpaperType::new);
    public static final Supplier<WallpaperType> CRIMSON_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("crimson_trim", WallpaperType::new);
    public static final Supplier<WallpaperType> DARK_OAK_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("dark_oak_trim", WallpaperType::new);
    public static final Supplier<WallpaperType> JUNGLE_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("jungle_trim", WallpaperType::new);
    public static final Supplier<WallpaperType> MANGROVE_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("mangrove_trim", WallpaperType::new);
    public static final Supplier<WallpaperType> OAK_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("oak_trim", WallpaperType::new);
    public static final Supplier<WallpaperType> SPRUCE_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("spruce_trim", WallpaperType::new);
    public static final Supplier<WallpaperType> WARPED_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("warped_trim", WallpaperType::new);
    public static final Supplier<Pair<WallpaperType, WallpaperType>> BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("bricks_quoin", WallpaperType::new);

    public static final Supplier<Pair<WallpaperType, WallpaperType>> DEEPSLATE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("deepslate_bricks_quoin", WallpaperType::new);

    public static final Supplier<Pair<WallpaperType, WallpaperType>> END_STONE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("end_stone_bricks_quoin", WallpaperType::new);

    public static final Supplier<Pair<WallpaperType, WallpaperType>> MUD_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("mud_bricks_quoin", WallpaperType::new);

    public static final Supplier<Pair<WallpaperType, WallpaperType>> POLISHED_BLACKSTONE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("polished_blackstone_bricks_quoin", WallpaperType::new);

    public static final Supplier<Pair<WallpaperType, WallpaperType>> QUARTZ_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("quartz_bricks_quoin", WallpaperType::new);

    public static final Supplier<Pair<WallpaperType, WallpaperType>> STONE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("stone_bricks_quoin", WallpaperType::new);
}
