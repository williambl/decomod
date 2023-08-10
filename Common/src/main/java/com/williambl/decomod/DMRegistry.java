package com.williambl.decomod;

import com.mojang.datafixers.util.Pair;
import com.sun.jna.platform.unix.solaris.LibKstat;
import com.williambl.decomod.platform.Services;
import com.williambl.decomod.wallpaper.*;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.williambl.decomod.DecoMod.id;

public class DMRegistry {

    public static final Supplier<CreativeModeTab> TAB = Services.REGISTRATION_HELPER.registerCreativeModeTab("decomod", () -> DMRegistry.WALLPAPERING_TABLE_BLOCK.get().asItem().getDefaultInstance(), (itemDisplayParameters, output) -> {output.acceptAll(BuiltInRegistries.ITEM.entrySet().stream().filter(kv -> kv.getKey().location().getNamespace().equals(Constants.MOD_ID)).map(Map.Entry::getValue).map(Item::getDefaultInstance).toList());});
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> ACACIA_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("acacia_door", BlockSetType.ACACIA, BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> BIRCH_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("birch_door", BlockSetType.BIRCH, BlockBehaviour.Properties.copy(Blocks.BIRCH_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> CRIMSON_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("crimson_door", BlockSetType.CRIMSON, BlockBehaviour.Properties.copy(Blocks.CRIMSON_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> DARK_OAK_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("dark_oak_door", BlockSetType.DARK_OAK, BlockBehaviour.Properties.copy(Blocks.DARK_OAK_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> JUNGLE_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("jungle_door", BlockSetType.JUNGLE, BlockBehaviour.Properties.copy(Blocks.JUNGLE_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> MANGROVE_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("mangrove_door", BlockSetType.MANGROVE, BlockBehaviour.Properties.copy(Blocks.MANGROVE_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> OAK_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("oak_door", BlockSetType.OAK, BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> SPRUCE_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("spruce_door", BlockSetType.SPRUCE, BlockBehaviour.Properties.copy(Blocks.SPRUCE_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> WARPED_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("warped_door", BlockSetType.WARPED, BlockBehaviour.Properties.copy(Blocks.WARPED_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> CHERRY_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("cherry_door", BlockSetType.CHERRY, BlockBehaviour.Properties.copy(Blocks.CHERRY_DOOR), new Item.Properties());
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> IRON_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("iron_door", BlockSetType.IRON, BlockBehaviour.Properties.copy(Blocks.IRON_DOOR), new Item.Properties());
    public static final Supplier<IronBarsBlock> IRON_FENCE =
            Services.REGISTRATION_HELPER.registerBlock("iron_fence", () -> new IronFenceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)) {}, new Item.Properties());

    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> CHAIN_LINK_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("chain_link_door", () -> new ChainLinkDoorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_DOOR), BlockSetType.IRON), new Item.Properties());

    public static final Supplier<Registry<WallpaperType>> WALLPAPER_REGISTRY =
            Services.REGISTRATION_HELPER.registerRegistry("wallpaper_type", WallpaperType.class);

    public static final Supplier<WallpaperScraperItem> WALLPAPER_SCRAPER =
            Services.REGISTRATION_HELPER.registerItem("wallpaper_scraper", () -> new WallpaperScraperItem(new Item.Properties().stacksTo(1)));

    public static final Function<WallpaperType, Supplier<WallpaperApplierItem>> WALLPAPER_ITEMS = Util.make(() -> {
        Map<WallpaperType, Supplier<WallpaperApplierItem>> map = new HashMap<>();
        Services.REGISTRATION_HELPER.forAllRegistered(WALLPAPER_REGISTRY.get(), (wallpaperType, wallpaperId) -> {
            ResourceLocation id;
            if (wallpaperType instanceof DoubleWallpaperType) {
                if (wallpaperId.getPath().endsWith("_left")) {
                    id = id("wallpaper/"+wallpaperId.getNamespace()+"/"+wallpaperId.getPath().substring(0, wallpaperId.getPath().length() - "_left".length()));
                } else {
                    return;
                }
            } else {
                id = id("wallpaper/" + wallpaperId.getNamespace() + "/" + wallpaperId.getPath());
            }
            var item = Services.REGISTRATION_HELPER.registerItem(id, () -> new WallpaperApplierItem(new Item.Properties(), wallpaperType));
            map.put(wallpaperType, item);
            if (wallpaperType instanceof DoubleWallpaperType doubleType) {
                map.put(doubleType.right.get(), item);
            }
        });
        return map::get;
    });

    public static final Supplier<WallpaperingTableBlock> WALLPAPERING_TABLE_BLOCK =
            Services.REGISTRATION_HELPER.registerBlock("wallpapering_table", () -> new WallpaperingTableBlock(BlockBehaviour.Properties.copy(Blocks.CARTOGRAPHY_TABLE)), new Item.Properties());

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
    public static final Supplier<WallpaperType> CHERRY_TRIM =
            Services.REGISTRATION_HELPER.registerWallpaperType("cherry_trim", WallpaperType::new);
    public static final Supplier<Pair<WallpaperType, WallpaperType>> BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("bricks_quoin");

    public static final Supplier<Pair<WallpaperType, WallpaperType>> DEEPSLATE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("deepslate_bricks_quoin");

    public static final Supplier<Pair<WallpaperType, WallpaperType>> END_STONE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("end_stone_bricks_quoin");

    public static final Supplier<Pair<WallpaperType, WallpaperType>> MUD_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("mud_bricks_quoin");

    public static final Supplier<Pair<WallpaperType, WallpaperType>> POLISHED_BLACKSTONE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("polished_blackstone_bricks_quoin");

    public static final Supplier<Pair<WallpaperType, WallpaperType>> QUARTZ_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("quartz_bricks_quoin");

    public static final Supplier<Pair<WallpaperType, WallpaperType>> STONE_BRICKS_QUOIN =
            Services.REGISTRATION_HELPER.registerWallpaperTypeLeftAndRight("stone_bricks_quoin");
}
