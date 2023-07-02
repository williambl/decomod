package com.williambl.decomod;

import com.williambl.decomod.client.WallpaperingTableScreen;
import com.williambl.decomod.platform.ClientServices;
import com.williambl.decomod.wallpaper.WallpaperingTableMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DecoModClient {
    public static void init() {
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.CUSTOM_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.IRON_FENCE.get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerMenuScreen(DMRegistry.WALLPAPERING_TABLE_MENU.get(), WallpaperingTableScreen::new);
    }
}
