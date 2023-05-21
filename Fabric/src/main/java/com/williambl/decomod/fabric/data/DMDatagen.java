package com.williambl.decomod.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

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
        public DMModels(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
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
