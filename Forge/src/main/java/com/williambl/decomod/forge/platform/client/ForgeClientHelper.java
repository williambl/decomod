package com.williambl.decomod.forge.platform.client;

import com.williambl.decomod.Constants;
import com.williambl.decomod.platform.services.client.IClientHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid= Constants.MOD_ID)
public class ForgeClientHelper implements IClientHelper {
    private static final List<RendererProviderEntry<?>> rendererProviders = new ArrayList<>();

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererSup) {
        rendererProviders.add(new RendererProviderEntry<>(type, rendererSup));
    }

    @Override
    public void registerBlockRenderType(Block block, RenderType type) {
        //no-op
    }

    @Override
    public void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> createModel) {
        //todo
    }

    @Override
    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(MenuType<T> menuType, MenuScreens.ScreenConstructor<T, U> screenConstructor) {

    }

    @Override
    public void registerWallpaperRenderer() {

    }

    @Override
    public void addModelToRuntimeResourcePack(ResourceLocation name, ResourceLocation parent, Map<String, ResourceLocation> textures) {

    }

    @Override
    public void forceLoadModels(Consumer<Consumer<ResourceLocation>> modelConsumer) {

    }

    @SubscribeEvent
    public static void onRendererRegistration(EntityRenderersEvent.RegisterRenderers event) {
        for (var entry : rendererProviders) {
            entry.register(event);
        }
    }

    private record RendererProviderEntry<T extends Entity>(EntityType<? extends T> type, EntityRendererProvider<T> rendererProvider) {
        void register(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(this.type, this.rendererProvider);
        }
    }
}
