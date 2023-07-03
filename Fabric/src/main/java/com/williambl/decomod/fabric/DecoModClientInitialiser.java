package com.williambl.decomod.fabric;

import com.williambl.decomod.Constants;
import com.williambl.decomod.DecoModClient;
import com.williambl.decomod.client.ExtendedModelManager;
import com.williambl.decomod.client.ExtendedViewArea;
import com.williambl.decomod.client.WallpaperRenderer;
import com.williambl.decomod.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class DecoModClientInitialiser implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOGGER.info("Hello Fabric Client world!");
        DecoModClient.init();
        DecoModRuntimeResourcePack.registerResourcePack();
    }
}
