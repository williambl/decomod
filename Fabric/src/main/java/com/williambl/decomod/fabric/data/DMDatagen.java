package com.williambl.decomod.fabric.data;

import com.williambl.decomod.DMRegistry;
import com.williambl.decomod.wallpaper.WallpaperingRecipe;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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

        private static final TextureSlot PANEL_TEXTURE = TextureSlot.create("panel");
        private static final TextureSlot POST_TEXTURE = TextureSlot.create("post");
        private static final ModelTemplate FENCE_PANEL = createModelTemplate("fence_panel", "_panel", PANEL_TEXTURE);
        private static final ModelTemplate FENCE_CORNER = createModelTemplate("fence_corner", "_corner", PANEL_TEXTURE);
        private static final ModelTemplate FENCE_END = createModelTemplate("fence_end", "_end", PANEL_TEXTURE, POST_TEXTURE);
        private static final ModelTemplate FENCE_POST = createModelTemplate("fence_post", "_post", POST_TEXTURE);


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

        private void createIronFence(Block block, BlockModelGenerators generators) {
            TextureMapping textureMapping = new TextureMapping().put(PANEL_TEXTURE, TextureMapping.getBlockTexture(block, "_panel")).put(POST_TEXTURE, TextureMapping.getBlockTexture(block, "_post"));
            ResourceLocation panel = FENCE_PANEL.create(block, textureMapping, generators.modelOutput);
            ResourceLocation corner = FENCE_CORNER.create(block, textureMapping, generators.modelOutput);
            ResourceLocation end = FENCE_END.create(block, textureMapping, generators.modelOutput);
            ResourceLocation post = FENCE_POST.create(block, textureMapping, generators.modelOutput);
            generators.createSimpleFlatItemModel(block.asItem());
            generators.blockStateOutput.accept(MultiPartGenerator.multiPart(block)
                    .with(  // panel e-w
                            Condition.condition().term(BlockStateProperties.EAST, true).term(BlockStateProperties.WEST, true),
                            Variant.variant().with(VariantProperties.MODEL, panel))
                    .with(  // panel n-s
                            Condition.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.SOUTH, true),
                            Variant.variant().with(VariantProperties.MODEL, panel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .with(  // only east connected
                            Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.WEST, false),
                            Variant.variant().with(VariantProperties.MODEL, end))
                    .with(  // only south connected
                            Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.WEST, false),
                            Variant.variant().with(VariantProperties.MODEL, end).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .with(  // only west connected
                            Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.WEST, true),
                            Variant.variant().with(VariantProperties.MODEL, end).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                    .with(  // only north connected
                            Condition.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.WEST, false),
                            Variant.variant().with(VariantProperties.MODEL, end).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                    .with(  // north-west corner
                            Condition.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.WEST, true),
                            Variant.variant().with(VariantProperties.MODEL, corner))
                    .with(  // north-east corner
                            Condition.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, true),
                            Variant.variant().with(VariantProperties.MODEL, corner).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                    .with(  // east-south corner
                            Condition.condition().term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.EAST, true),
                            Variant.variant().with(VariantProperties.MODEL, corner).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                    .with(  // south-west corner
                            Condition.condition().term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, true),
                            Variant.variant().with(VariantProperties.MODEL, corner).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                    .with(  // no connections
                            Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.WEST, false),
                            Variant.variant().with(VariantProperties.MODEL, post))
            );
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            createDoor(DMRegistry.CUSTOM_DOOR.getFirst().get(), blockStateModelGenerator);
            createIronFence(DMRegistry.IRON_FENCE.get(), blockStateModelGenerator);
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
            WallpaperingRecipe.Builder.wallpapering(Ingredient.of(Items.IRON_INGOT), Ingredient.of(Items.IRON_INGOT), DMRegistry.WALLPAPER_ITEMS.apply(DMRegistry.IRON_BAND.get()).get())
                    .unlocks("has_iron", has(Items.IRON_INGOT))
                    .save(exporter, id("iron_band"));
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
