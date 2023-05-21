package com.williambl.decomod.platform.services.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface IClientHelper {
    <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererSup);
    void registerBlockRenderType(Block block, RenderType type);
    void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> createModel);
}
