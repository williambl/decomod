package com.williambl.decomod.wallpaper;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.stream.Stream;

public class WallpaperingRecipe implements Recipe<Container> {
    final Ingredient ingredientA;
    final Ingredient ingredientB;
    final ItemStack result;
    private final ResourceLocation id;

    public WallpaperingRecipe(ResourceLocation id, Ingredient ingredientA, Ingredient ingredientB, ItemStack result) {
        this.id = id;
        this.ingredientA = ingredientA;
        this.ingredientB = ingredientB;
        this.result = result;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.ingredientA.test(container.getItem(0)) && this.ingredientB.test(container.getItem(1));
    }

    @Override
    public ItemStack assemble(Container container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    public boolean isAdditionIngredient(ItemStack $$0) {
        return this.ingredientB.test($$0);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.ingredientA, this.ingredientB).anyMatch(($$0) -> $$0.getItems().length == 0);
    }

    public static class Serializer implements RecipeSerializer<WallpaperingRecipe> {
        @Override
        public WallpaperingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredientA = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient_a"));
            Ingredient ingredientB = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient_b"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new WallpaperingRecipe(id, ingredientA, ingredientB, result);
        }

        @Override
        public WallpaperingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ingredientA = Ingredient.fromNetwork(buf);
            Ingredient ingredientB = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new WallpaperingRecipe(id, ingredientA, ingredientB, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WallpaperingRecipe recipe) {
            recipe.ingredientA.toNetwork(buf);
            recipe.ingredientB.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }
}