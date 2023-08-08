package com.williambl.decomod.fabric.platform;

import com.williambl.decomod.Constants;
import com.williambl.decomod.DMRegistry;
import com.williambl.decomod.platform.services.IRegistrationHelper;
import com.williambl.decomod.wallpaper.WallpaperType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.williambl.decomod.DecoMod.id;

public class FabricRegistrationHelper implements IRegistrationHelper {
    @Override
    public Supplier<CreativeModeTab> registerCreativeModeTab(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator generator) {
        final var res = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id(name), FabricItemGroup.builder().title(Component.translatable(Util.makeDescriptionId("itemGroup", id(name)))).icon(icon).displayItems(generator).build());
        return () -> res;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> sup) {
        final var res = Registry.register(BuiltInRegistries.ITEM, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(ResourceLocation name, Supplier<T> sup) {
        final var res = Registry.register(BuiltInRegistries.ITEM, name, sup.get());
        return () -> res;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> sup) {
        final var res = Registry.register(BuiltInRegistries.BLOCK, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBEType(String name, BiFunction<BlockPos, BlockState, T> factory, Supplier<Set<Block>> blocksSup) {
        final var res = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(name), FabricBlockEntityTypeBuilder.<T>create(factory::apply, blocksSup.get().toArray(Block[]::new)).build());
        return () -> res;
    }

    @Override
    public <T extends RecipeType<?>> Supplier<T> registerRecipeType(String name, Supplier<T> sup) {
        final var res = Registry.register(BuiltInRegistries.RECIPE_TYPE, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends RecipeSerializer<?>> Supplier<T> registerRecipeSerializer(String name, Supplier<T> sup) {
        final var res = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends AbstractMinecart> Supplier<EntityType<T>> registerMinecartType(String name, EntityType.EntityFactory<T> factory) {
        final var res = Registry.register(BuiltInRegistries.ENTITY_TYPE, id(name), FabricEntityTypeBuilder.create(MobCategory.MISC, factory).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build());
        return () -> res;
    }

    @Override
    public <T> Supplier<Registry<T>> registerRegistry(String name, Class<T> clazz) {
        final var res = FabricRegistryBuilder.createSimple(clazz, id(name)).buildAndRegister();
        return () -> res;
    }

    @Override
    public <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, BiFunction<Integer, Inventory, T> factory) {
        final var res = Registry.register(BuiltInRegistries.MENU, id(name), new MenuType<>(factory::apply, FeatureFlagSet.of()));
        return () -> res;
    }

    @Override
    public <T extends WallpaperType> Supplier<T> registerWallpaperType(String name, Supplier<T> sup) {
        final var res = Registry.register(DMRegistry.WALLPAPER_REGISTRY.get(), id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T> void forAllRegistered(Registry<T> registry, BiConsumer<T, ResourceLocation> consumer) {
        registry.holders().forEach(holder -> {
            consumer.accept(holder.value(), holder.key().location());
        });
        RegistryEntryAddedCallback.event(registry).register((rawId, id, object) -> {
            consumer.accept(object, id);
        });
    }
}
