package com.williambl.decomod.fabric.platform.client;

import com.williambl.decomod.client.ExtendedModelManager;
import com.williambl.decomod.client.WallpaperRenderer;
import com.williambl.decomod.fabric.DecoModRuntimeResourcePack;
import com.williambl.decomod.platform.services.client.IClientHelper;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricClientHelper implements IClientHelper {
    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererSup) {
        EntityRendererRegistry.register(type, rendererSup);
    }

    @Override
    public void registerBlockRenderType(Block block, RenderType type) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, type);
    }

    @Override
    public void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> createModel) {
        EntityModelLayerRegistry.registerModelLayer(location, createModel::get);
    }

    @Override
    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(MenuType<T> menuType, MenuScreens.ScreenConstructor<T, U> screenConstructor) {
        MenuScreens.register(menuType, screenConstructor);
    }

    @Override
    public void registerWallpaperRenderer() {
        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            var poseStack = ctx.matrixStack();
            var buffers = ctx.consumers();
            var modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
            var level = ctx.world();
            var modelManager = ((ExtendedModelManager)Minecraft.getInstance().getModelManager());
            var camera = ctx.camera();
            int viewDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();
            WallpaperRenderer.renderWallpapers(
                    poseStack,
                    buffers,
                    modelRenderer,
                    level,
                    modelManager,
                    camera,
                    viewDistance
            );
        });
    }

    @Override
    public void addModelToRuntimeResourcePack(ResourceLocation name, ResourceLocation parent, Map<String, ResourceLocation> textures) {
        DecoModRuntimeResourcePack.addModel(name, parent, textures);
    }

    @Override
    public void forceLoadModels(Consumer<Consumer<ResourceLocation>> modelConsumer) {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            modelConsumer.accept(out);
        });
    }
}
