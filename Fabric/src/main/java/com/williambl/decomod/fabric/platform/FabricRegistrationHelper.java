package com.williambl.decomod.fabric.platform;

import com.williambl.decomod.platform.services.IRegistrationHelper;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.williambl.decomod.DecoMod.id;

public class FabricRegistrationHelper implements IRegistrationHelper {
    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> sup) {
        final var res = Registry.register(Registry.ITEM, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> sup) {
        final var res = Registry.register(Registry.BLOCK, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBEType(String name, BiFunction<BlockPos, BlockState, T> factory, Supplier<Set<Block>> blocksSup) {
        final var res = Registry.register(Registry.BLOCK_ENTITY_TYPE, id(name), FabricBlockEntityTypeBuilder.<T>create(factory::apply, blocksSup.get().toArray(Block[]::new)).build());
        return () -> res;
    }

    @Override
    public <T extends RecipeType<?>> Supplier<T> registerRecipeType(String name, Supplier<T> sup) {
        final var res = Registry.register(Registry.RECIPE_TYPE, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends RecipeSerializer<?>> Supplier<T> registerRecipeSerializer(String name, Supplier<T> sup) {
        final var res = Registry.register(Registry.RECIPE_SERIALIZER, id(name), sup.get());
        return () -> res;
    }

    @Override
    public <T extends AbstractMinecart> Supplier<EntityType<T>> registerMinecartType(String name, EntityType.EntityFactory<T> factory) {
        final var res = Registry.register(Registry.ENTITY_TYPE, id(name), FabricEntityTypeBuilder.create(MobCategory.MISC, factory).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build());
        return () -> res;
    }

    @Override
    public <T> Supplier<Registry<T>> registerRegistry(String name, Class<T> clazz) {
        final var res = FabricRegistryBuilder.createSimple(clazz, id(name)).buildAndRegister();
        return () -> res;
    }

    @Override
    public <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, BiFunction<Integer, Inventory, T> factory) {
        final var res = Registry.register(Registry.MENU, id(name), new MenuType<>(factory::apply));
        return () -> res;
    }
}
