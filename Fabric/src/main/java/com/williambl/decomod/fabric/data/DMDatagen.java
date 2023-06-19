package com.williambl.decomod.fabric.data;

import com.williambl.decomod.DMRegistry;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Consumer;

import static com.williambl.decomod.DecoMod.id;

public class DMDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(DMModels::new);
        fabricDataGenerator.addProvider(DMBlockTags::new);
        fabricDataGenerator.addProvider(DMItemTags::new);
        fabricDataGenerator.addProvider(DMRecipes::new);
        fabricDataGenerator.addProvider(DMLang::new);
        fabricDataGenerator.addProvider(DMLootTables::new);
        fabricDataGenerator.addProvider(DMAdvancements::new);
    }

    private static class DMModels extends FabricModelProvider {
        private static ModelTemplate createModelTemplate(String string, String string2, TextureSlot... textureSlots) {
            return new ModelTemplate(Optional.of(id("block/" +string)), Optional.of(string2), textureSlots);
        }

        private static final ModelTemplate DOOR_BOTTOM_LEFT = createModelTemplate("door_bottom_left", "_bottom_left", TextureSlot.BOTTOM);
        private static final ModelTemplate DOOR_BOTTOM_RIGHT = createModelTemplate("door_bottom_right", "_bottom_right", TextureSlot.BOTTOM);
        private static final ModelTemplate DOOR_TOP_LEFT = createModelTemplate("door_top_left", "_top_left", TextureSlot.TOP);
        private static final ModelTemplate DOOR_TOP_RIGHT = createModelTemplate("door_top_right", "_top_right", TextureSlot.TOP);

        public DMModels(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        private void createDoor(Block block, BlockModelGenerators generators) {
            TextureMapping texturemapping = TextureMapping.door(block);
            ResourceLocation bottomLeft = DOOR_BOTTOM_LEFT.create(block, texturemapping, generators.modelOutput);
            ResourceLocation bottomRight = DOOR_BOTTOM_RIGHT.create(block, texturemapping, generators.modelOutput);
            ResourceLocation topLeft = DOOR_TOP_LEFT.create(block, texturemapping, generators.modelOutput);
            ResourceLocation topRight = DOOR_TOP_RIGHT.create(block, texturemapping, generators.modelOutput);
            generators.createSimpleFlatItemModel(block.asItem());
            generators.blockStateOutput.accept(BlockModelGenerators.createDoor(block, bottomLeft, bottomRight, bottomRight, bottomLeft, topLeft, topRight, topRight, topLeft));
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            createDoor(DMRegistry.CUSTOM_DOOR.getFirst().get(), blockStateModelGenerator);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        }
    }

    private static class DMBlockTags extends FabricTagProvider.BlockTagProvider {
        public DMBlockTags(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
        }
    }

    private static class DMItemTags extends FabricTagProvider.ItemTagProvider {
        public DMItemTags(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
        }
    }

    private static class DMRecipes extends FabricRecipeProvider {
        public DMRecipes(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateRecipes(Consumer<FinishedRecipe> exporter) {
        }
    }

    private static class DMLootTables extends FabricBlockLootTableProvider {
        protected DMLootTables(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateBlockLootTables() {
        }
    }

    private static class DMLang extends FabricLanguageProvider {
        protected DMLang(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
        }
    }

    private static class DMAdvancements extends FabricAdvancementProvider {
        protected DMAdvancements(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateAdvancement(Consumer<Advancement> consumer) {
        }
    }
}
