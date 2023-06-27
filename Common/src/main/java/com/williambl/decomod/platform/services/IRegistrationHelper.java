package com.williambl.decomod.platform.services;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
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
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IRegistrationHelper {
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> sup);
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
}
