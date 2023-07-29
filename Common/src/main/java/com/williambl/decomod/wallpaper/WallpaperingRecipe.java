package com.williambl.decomod.wallpaper;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.williambl.decomod.DMRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
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
    public ItemStack assemble(Container container, RegistryAccess registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return this.result;
    }

    public boolean isAdditionIngredient(ItemStack $$0) {
        return this.ingredientB.test($$0);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(DMRegistry.WALLPAPERING_TABLE_BLOCK.get());
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DMRegistry.WALLPAPERING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return DMRegistry.WALLPAPERING_RECIPE_TYPE.get();
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.ingredientA, this.ingredientB).anyMatch(($$0) -> $$0.getItems().length == 0);
    }

    public static class Serializer implements RecipeSerializer<WallpaperingRecipe> {

        private static Ingredient getPossiblyEmptyIngredient(JsonObject json, String key) {
            var jsonElement = json.get(key);
            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().isEmpty()) {
                return Ingredient.of();
            }
            return Ingredient.fromJson(jsonElement);
        }

        @Override
        public WallpaperingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredientA = getPossiblyEmptyIngredient(json, "ingredient_a");
            Ingredient ingredientB = getPossiblyEmptyIngredient(json, "ingredient_b");
            ItemStack result = ItemStack.CODEC.decode(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "result")).map(Pair::getFirst).result().orElseThrow();
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

    public static class Builder {
        private final Ingredient ingredientA;
        private final Ingredient ingredientB;
        private final ItemStack result;
        private final Advancement.Builder advancement = Advancement.Builder.advancement();
        private final RecipeSerializer<?> type;

        public Builder(RecipeSerializer<?> recipeSerializer, Ingredient ingredientA, Ingredient ingredient2, ItemStack item) {
            this.type = recipeSerializer;
            this.ingredientA = ingredientA;
            this.ingredientB = ingredient2;
            this.result = item;
        }

        public static Builder wallpapering(Ingredient ingredient, Ingredient ingredient2, Item item) {
            return new Builder(DMRegistry.WALLPAPERING_RECIPE_SERIALIZER.get(), ingredient, ingredient2, item.getDefaultInstance());
        }

        public static Builder wallpapering(Ingredient ingredient, Ingredient ingredient2, ItemStack item) {
            return new Builder(DMRegistry.WALLPAPERING_RECIPE_SERIALIZER.get(), ingredient, ingredient2, item);
        }

        public Builder unlocks(String string, CriterionTriggerInstance criterionTriggerInstance) {
            this.advancement.addCriterion(string, criterionTriggerInstance);
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer, String string) {
            this.save(consumer, new ResourceLocation(string));
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
            this.ensureValid(resourceLocation);
            this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
            String namespace = resourceLocation.getNamespace();
            consumer.accept(new Builder.Result(resourceLocation, this.type, this.ingredientA, this.ingredientB, this.result, this.advancement, new ResourceLocation(namespace, "recipes/" + resourceLocation.getPath())));
        }

        private void ensureValid(ResourceLocation resourceLocation) {
            if (this.advancement.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
            }
        }

        public static class Result implements FinishedRecipe {
            private final ResourceLocation id;
            private final Ingredient base;
            private final Ingredient addition;
            private final ItemStack result;
            private final Advancement.Builder advancement;
            private final ResourceLocation advancementId;
            private final RecipeSerializer<?> type;

            public Result(ResourceLocation resourceLocation, RecipeSerializer<?> recipeSerializer, Ingredient ingredient, Ingredient ingredient2, ItemStack item, Advancement.Builder builder, ResourceLocation resourceLocation2) {
                this.id = resourceLocation;
                this.type = recipeSerializer;
                this.base = ingredient;
                this.addition = ingredient2;
                this.result = item;
                this.advancement = builder;
                this.advancementId = resourceLocation2;
            }

            @Override
            public void serializeRecipeData(JsonObject jsonObject) {
                jsonObject.add("ingredient_a", this.base.toJson());
                jsonObject.add("ingredient_b", this.addition.toJson());
                var serialisedStack = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, this.result).result().orElseThrow();
                jsonObject.add("result", serialisedStack);
            }

            @Override
            public ResourceLocation getId() {
                return this.id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return this.type;
            }

            @Override
            @Nullable
            public JsonObject serializeAdvancement() {
                return this.advancement.serializeToJson();
            }

            @Override
            @Nullable
            public ResourceLocation getAdvancementId() {
                return this.advancementId;
            }
        }
    }
}