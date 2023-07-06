package com.williambl.decomod.platform.services;

import com.mojang.datafixers.util.Pair;
import com.williambl.decomod.wallpaper.WallpaperType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.function.*;

public interface IRegistrationHelper {
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> sup);
    public <T extends Item> Supplier<T> registerItem(ResourceLocation name, Supplier<T> sup);
    public <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> sup);
    default  <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> sup, Item.Properties itemProps) {
        var block = this.registerBlock(name, sup);
        this.registerItem(name, () -> new BlockItem(block.get(), itemProps));
        return block;
    }
    default <T extends DoorBlock> Pair<Supplier<T>, Supplier<DoubleHighBlockItem>> registerDoor(String name, Supplier<T> doorSup, Item.Properties itemProps) {
        var block = this.registerBlock(name, doorSup);
        var item = this.registerItem(name, () -> new DoubleHighBlockItem(block.get(), itemProps));
        return Pair.of(block, item);
    }
    default Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> registerDoor(String name, BlockBehaviour.Properties blockProps, Item.Properties itemProps) {
        return this.registerDoor(name, () -> new DoorBlock(blockProps) {}, itemProps);
    }
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBEType(String name, BiFunction<BlockPos, BlockState, T> factory, Supplier<Set<Block>> blocksSup);
    public <T extends RecipeType<?>> Supplier<T> registerRecipeType(String name, Supplier<T> sup);
    public <T extends RecipeSerializer<?>> Supplier<T> registerRecipeSerializer(String name, Supplier<T> sup);
    public <T extends AbstractMinecart> Supplier<EntityType<T>> registerMinecartType(String name, EntityType.EntityFactory<T> factory);
    public <T> Supplier<Registry<T>> registerRegistry(String name, Class<T> clazz);
    public <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, BiFunction<Integer, Inventory, T> factory);
    public <T extends WallpaperType> Supplier<T> registerWallpaperType(String name, Supplier<T> sup);
    default <T extends WallpaperType> Supplier<Pair<T, T>> registerWallpaperTypeLeftAndRight(String name, Supplier<T> sup) {
        var left = this.registerWallpaperType(name+"_left", sup);
        var right = this.registerWallpaperType(name+"_right", sup);
        return () -> Pair.of(left.get(), right.get());
    }
    public <T> void forAllRegistered(Registry<T> registry, BiConsumer<T, ResourceLocation> consumer);
}
