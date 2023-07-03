package com.williambl.decomod;

import com.williambl.decomod.client.WallpaperingTableScreen;
import com.williambl.decomod.platform.ClientServices;
import com.williambl.decomod.platform.Services;
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
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.CUSTOM_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.IRON_FENCE.get(), RenderType.cutoutMipped());
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
            ClientServices.CLIENT.addModelToRuntimeResourcePack(new ResourceLocation(wallpaperId.getNamespace(), "item/"+wallpaperId.getPath()), new ResourceLocation("item/generated"), Map.of("layer0", texture));
            for (var dir : Direction.values()) {
                ClientServices.CLIENT.addModelToRuntimeResourcePack(
                        new ResourceLocation(wallpaperId.getNamespace(), "wallpaper/" + wallpaperId.getPath() + "/" + dir.getName()),
                        id("wallpaper_template/"+dir.getName()),
                        Map.of("wallpaper", texture));
            }
        });
    }
}
