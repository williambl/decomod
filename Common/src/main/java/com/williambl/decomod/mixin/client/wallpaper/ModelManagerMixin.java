package com.williambl.decomod.mixin.client.wallpaper;

import com.williambl.decomod.client.ExtendedModelManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin implements ExtendedModelManager {
    @Shadow
    private Map<ResourceLocation, BakedModel> bakedRegistry;

    @Shadow
    private BakedModel missingModel;

    @Override
    public BakedModel getModelFromResLoc(ResourceLocation location) {
        return this.bakedRegistry.getOrDefault(location, this.missingModel);
    }
}
