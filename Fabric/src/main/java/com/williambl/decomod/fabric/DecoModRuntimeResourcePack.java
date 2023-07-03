package com.williambl.decomod.fabric;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static net.devtech.arrp.api.RuntimeResourcePack.id;

public class DecoModRuntimeResourcePack {
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(id("wallpaper"));

    public static void addModel(ResourceLocation name, ResourceLocation parent, Map<String, ResourceLocation> textures) {
        var model = JModel.model(parent);
        var modelTextures = JModel.textures();
        for (var entry : textures.entrySet()) {
            modelTextures.var(entry.getKey(), entry.getValue().toString());
        }
        RESOURCE_PACK.addModel(model.textures(modelTextures), name);
    }

    public static void registerResourcePack() {
        RRPCallback.AFTER_VANILLA.register(a -> a.add(RESOURCE_PACK));
    }
}
