package com.williambl.decomod;

import com.williambl.decomod.client.WallpaperingTableScreen;
import com.williambl.decomod.platform.ClientServices;
import com.williambl.decomod.platform.Services;
import com.williambl.decomod.wallpaper.DoubleWallpaperType;
import com.williambl.decomod.wallpaper.WallpaperingTableMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.security.Provider;
import java.util.Map;

import static com.williambl.decomod.DecoMod.id;

public class DecoModClient {
    public static void init() {
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.ACACIA_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.BIRCH_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.CRIMSON_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.DARK_OAK_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.JUNGLE_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.MANGROVE_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.OAK_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.SPRUCE_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.WARPED_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.CHERRY_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.IRON_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.IRON_FENCE.get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.CHAIN_LINK_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerMenuScreen(DMRegistry.WALLPAPERING_TABLE_MENU.get(), WallpaperingTableScreen::new);
        ClientServices.CLIENT.registerWallpaperRenderer();
        ClientServices.CLIENT.forceLoadModels(out -> {
            Services.REGISTRATION_HELPER.forAllRegistered(DMRegistry.WALLPAPER_REGISTRY.get(), (wallpaperType, wallpaperId) -> {
                for (var dir : Direction.values()) {
                    out.accept(new ResourceLocation(wallpaperId.getNamespace(), "wallpaper/" + wallpaperId.getPath() + "/" + dir.getName()));
                }
            });
        });
        Services.REGISTRATION_HELPER.forAllRegistered(DMRegistry.WALLPAPER_REGISTRY.get(), (wallpaperType, wallpaperId) -> {
            var texture = new ResourceLocation(wallpaperId.getNamespace(), "wallpaper/"+wallpaperId.getPath());
            ClientServices.CLIENT.addModelToRuntimeResourcePack(id("item/wallpaper/"+wallpaperId.getNamespace()+"/"+wallpaperId.getPath()), new ResourceLocation("item/generated"), Map.of("layer0", texture));
            if (wallpaperType instanceof DoubleWallpaperType && wallpaperId.getPath().endsWith("_left")) {
                ClientServices.CLIENT.addModelToRuntimeResourcePack(id("item/wallpaper/" + wallpaperId.getNamespace() + "/" + wallpaperId.getPath().substring(0, wallpaperId.getPath().length()-"_left".length())), new ResourceLocation("item/generated"), Map.of("layer0", texture));
            }
            for (var dir : Direction.values()) {
                ClientServices.CLIENT.addModelToRuntimeResourcePack(
                        new ResourceLocation(wallpaperId.getNamespace(), "wallpaper/" + wallpaperId.getPath() + "/" + dir.getName()),
                        id("wallpaper_template/"+dir.getName()),
                        Map.of("wallpaper", texture));
            }
        });
    }
}
