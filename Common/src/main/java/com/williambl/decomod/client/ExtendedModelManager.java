package com.williambl.decomod.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public interface ExtendedModelManager {
	BakedModel getModelFromResLoc(ResourceLocation location);
}