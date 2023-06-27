package com.williambl.decomod;

import com.williambl.decomod.platform.ClientServices;
import net.minecraft.client.renderer.RenderType;

public class DecoModClient {
    public static void init() {
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.CUSTOM_DOOR.getFirst().get(), RenderType.cutoutMipped());
        ClientServices.CLIENT.registerBlockRenderType(DMRegistry.IRON_FENCE.get(), RenderType.cutoutMipped());
    }
}
