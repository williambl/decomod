package com.williambl.decomod;

import net.minecraft.resources.ResourceLocation;

public class DecoMod {

    public static void init() {
        new DMRegistry();
        registerDispenserBehaviours();
    }

    public static void registerDispenserBehaviours() {
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(Constants.MOD_ID, path);
    }
}