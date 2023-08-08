package com.williambl.decomod.fabric.data;

import com.mojang.datafixers.util.Pair;
import com.williambl.decomod.DMRegistry;
import com.williambl.decomod.wallpaper.WallpaperType;
import com.williambl.decomod.wallpaper.WallpaperingRecipe;
import com.williambl.decomod.wallpaper.WallpaperingTableBlock;
import com.williambl.decomod.wallpaper.WallpaperingTableMenu;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.williambl.decomod.DecoMod.id;

public class DMDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(DMModels::new);
        pack.addProvider(DMBlockTags::new);
        pack.addProvider(DMItemTags::new);
        pack.addProvider(DMRecipes::new);
        pack.addProvider(DMLang::new);
        pack.addProvider(DMLootTables::new);
        pack.addProvider(DMAdvancements::new);
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
        private static final ModelTemplate FENCE_CORNER = createModelTemplate("fence_corner", "_corner", PANEL_TEXTURE, POST_TEXTURE);
        private static final ModelTemplate FENCE_END = createModelTemplate("fence_end", "_end", PANEL_TEXTURE, POST_TEXTURE);
        private static final ModelTemplate FENCE_POST = createModelTemplate("fence_post", "_post", POST_TEXTURE);


        public DMModels(FabricDataOutput dataGenerator) {
            super(dataGenerator);
        }

        private void createDoor(Block block, BlockModelGenerators generators, Item itemToStealTextureFrom) {
            TextureMapping texturemapping = TextureMapping.door(block);
            ResourceLocation bottomLeft = DOOR_BOTTOM_LEFT.create(block, texturemapping, generators.modelOutput);
            ResourceLocation bottomRight = DOOR_BOTTOM_RIGHT.create(block, texturemapping, generators.modelOutput);
            ResourceLocation topLeft = DOOR_TOP_LEFT.create(block, texturemapping, generators.modelOutput);
            ResourceLocation topRight = DOOR_TOP_RIGHT.create(block, texturemapping, generators.modelOutput);
            generators.delegateItemModel(block.asItem(), ModelLocationUtils.getModelLocation(itemToStealTextureFrom));
            generators.blockStateOutput.accept(BlockModelGenerators.createDoor(block, bottomLeft, bottomRight, bottomRight, bottomLeft, topLeft, topRight, topRight, topLeft));
        }


        private void createPremadeDoor(Block block, BlockModelGenerators generators) {
            TextureMapping texturemapping = TextureMapping.door(block);
            ResourceLocation bottomLeft = ModelLocationUtils.getModelLocation(block, "_bottom_left");
            ResourceLocation bottomLeftOpen = ModelLocationUtils.getModelLocation(block, "_bottom_left_open");
            ResourceLocation bottomRight = ModelLocationUtils.getModelLocation(block, "_bottom_right");
            ResourceLocation bottomRightOpen = ModelLocationUtils.getModelLocation(block, "_bottom_right_open");
            ResourceLocation topLeft = ModelLocationUtils.getModelLocation(block, "_top_left");
            ResourceLocation topLeftOpen = ModelLocationUtils.getModelLocation(block, "_top_left_open");
            ResourceLocation topRight = ModelLocationUtils.getModelLocation(block, "_top_right");
            ResourceLocation topRightOpen = ModelLocationUtils.getModelLocation(block, "_top_right_open");
            generators.createSimpleFlatItemModel(block.asItem());
            generators.blockStateOutput.accept(BlockModelGenerators.createDoor(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen));
        }

        private void createIronFence(Block block, BlockModelGenerators generators) {
            TextureMapping textureMapping = new TextureMapping().put(PANEL_TEXTURE, TextureMapping.getBlockTexture(block, "_panel")).put(POST_TEXTURE, TextureMapping.getBlockTexture(block, "_post"));
            ResourceLocation panel = FENCE_PANEL.create(block, textureMapping, generators.modelOutput);
            ResourceLocation corner = FENCE_CORNER.create(block, textureMapping, generators.modelOutput);
            ResourceLocation end = FENCE_END.create(block, textureMapping, generators.modelOutput);
            ResourceLocation post = FENCE_POST.create(block, textureMapping, generators.modelOutput);
            generators.createSimpleFlatItemModel(block, "_panel");
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

        private void createWallpaperingTable(BlockModelGenerators blockModelGenerators) {
            TextureMapping textureMapping = new TextureMapping()
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), "_side3"))
                    .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.ACACIA_PLANKS))
                    .put(TextureSlot.UP, TextureMapping.getBlockTexture(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), "_top"))
                    .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), "_side4"))
                    .put(TextureSlot.EAST, TextureMapping.getBlockTexture(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), "_side3"))
                    .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), "_side1"))
                    .put(TextureSlot.WEST, TextureMapping.getBlockTexture(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), "_side2"));
            blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), ModelTemplates.CUBE.create(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), textureMapping, blockModelGenerators.modelOutput)));
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            createDoor(DMRegistry.ACACIA_DOOR.getFirst().get(), blockStateModelGenerator, Items.ACACIA_DOOR);
            createDoor(DMRegistry.BIRCH_DOOR.getFirst().get(), blockStateModelGenerator, Items.BIRCH_DOOR);
            createDoor(DMRegistry.CRIMSON_DOOR.getFirst().get(), blockStateModelGenerator, Items.CRIMSON_DOOR);
            createDoor(DMRegistry.DARK_OAK_DOOR.getFirst().get(), blockStateModelGenerator, Items.DARK_OAK_DOOR);
            createDoor(DMRegistry.JUNGLE_DOOR.getFirst().get(), blockStateModelGenerator, Items.JUNGLE_DOOR);
            createDoor(DMRegistry.MANGROVE_DOOR.getFirst().get(), blockStateModelGenerator, Items.MANGROVE_DOOR);
            createDoor(DMRegistry.OAK_DOOR.getFirst().get(), blockStateModelGenerator, Items.OAK_DOOR);
            createDoor(DMRegistry.SPRUCE_DOOR.getFirst().get(), blockStateModelGenerator, Items.SPRUCE_DOOR);
            createDoor(DMRegistry.WARPED_DOOR.getFirst().get(), blockStateModelGenerator, Items.WARPED_DOOR);
            createDoor(DMRegistry.IRON_DOOR.getFirst().get(), blockStateModelGenerator, Items.IRON_DOOR);
            createPremadeDoor(DMRegistry.CHAIN_LINK_DOOR.getFirst().get(), blockStateModelGenerator);
            createIronFence(DMRegistry.IRON_FENCE.get(), blockStateModelGenerator);
            createWallpaperingTable(blockStateModelGenerator);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            itemModelGenerator.generateFlatItem(DMRegistry.WALLPAPER_SCRAPER.get(), ModelTemplates.FLAT_ITEM); // wallpaper scraper texture temporarily from https://github.com/malcolmriley/unused-textures (cc-0)
        }
    }

    private static class DMBlockTags extends FabricTagProvider.BlockTagProvider {
        public DMBlockTags(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(dataGenerator, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            this.tag(BlockTags.DOORS).add(
                    DMRegistry.ACACIA_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.BIRCH_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.CRIMSON_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.DARK_OAK_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.JUNGLE_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.MANGROVE_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.OAK_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.SPRUCE_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.WARPED_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.IRON_DOOR.getFirst().get().builtInRegistryHolder().key(),
                    DMRegistry.CHAIN_LINK_DOOR.getFirst().get().builtInRegistryHolder().key()
            );
        }
    }

    private static class DMItemTags extends FabricTagProvider.ItemTagProvider {
        public DMItemTags(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(dataGenerator, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            this.tag(ItemTags.DOORS).add(
                    DMRegistry.ACACIA_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.BIRCH_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.CRIMSON_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.DARK_OAK_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.JUNGLE_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.MANGROVE_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.OAK_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.SPRUCE_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.WARPED_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.IRON_DOOR.getFirst().get().asItem().builtInRegistryHolder().key(),
                    DMRegistry.CHAIN_LINK_DOOR.getFirst().get().asItem().builtInRegistryHolder().key()
            );
        }
    }

    private static class DMRecipes extends FabricRecipeProvider {
        public DMRecipes(FabricDataOutput dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void buildRecipes(Consumer<FinishedRecipe> exporter) {
            WallpaperingRecipe.Builder.wallpapering(Ingredient.of(Items.IRON_INGOT), Ingredient.of(Items.IRON_INGOT), DMRegistry.WALLPAPER_ITEMS.apply(DMRegistry.IRON_BAND.get()).get())
                    .unlocks("has_iron", has(Items.IRON_INGOT))
                    .save(exporter, id("iron_band"));

            exportTrim(Items.ACACIA_PLANKS, DMRegistry.ACACIA_TRIM.get(), "acacia_trim", exporter);
            exportTrim(Items.BIRCH_PLANKS, DMRegistry.BIRCH_TRIM.get(), "birch_trim", exporter);
            exportTrim(Items.CRIMSON_PLANKS, DMRegistry.CRIMSON_TRIM.get(), "crimson_trim", exporter);
            exportTrim(Items.DARK_OAK_PLANKS, DMRegistry.DARK_OAK_TRIM.get(), "dark_oak_trim", exporter);
            exportTrim(Items.JUNGLE_PLANKS, DMRegistry.JUNGLE_TRIM.get(), "jungle_trim", exporter);
            exportTrim(Items.MANGROVE_PLANKS, DMRegistry.MANGROVE_TRIM.get(), "mangrove_trim", exporter);
            exportTrim(Items.OAK_PLANKS, DMRegistry.OAK_TRIM.get(), "oak_trim", exporter);
            exportTrim(Items.SPRUCE_PLANKS, DMRegistry.SPRUCE_TRIM.get(), "spruce_trim", exporter);
            exportTrim(Items.WARPED_PLANKS, DMRegistry.WARPED_TRIM.get(), "warped_trim", exporter);
            exportQuoins(Items.BRICKS, DMRegistry.BRICKS_QUOIN.get(), "bricks_quoin", exporter);
            exportQuoins(Items.DEEPSLATE_BRICKS, DMRegistry.DEEPSLATE_BRICKS_QUOIN.get(), "deepslate_bricks_quoin", exporter);
            exportQuoins(Items.END_STONE_BRICKS, DMRegistry.END_STONE_BRICKS_QUOIN.get(), "end_stone_bricks_quoin", exporter);
            exportQuoins(Items.MUD_BRICKS, DMRegistry.MUD_BRICKS_QUOIN.get(), "mud_bricks_quoin", exporter);
            exportQuoins(Items.POLISHED_BLACKSTONE_BRICKS, DMRegistry.POLISHED_BLACKSTONE_BRICKS_QUOIN.get(), "polished_blackstone_bricks_quoin", exporter);
            exportQuoins(Items.QUARTZ_BRICKS, DMRegistry.QUARTZ_BRICKS_QUOIN.get(), "quartz_bricks_quoin", exporter);
            exportQuoins(Items.STONE_BRICKS, DMRegistry.STONE_BRICKS_QUOIN.get(), "stone_bricks_quoin", exporter);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, DMRegistry.WALLPAPER_SCRAPER.get())
                    .requires(Items.STICK)
                    .requires(Items.PAPER, 2)
                    .requires(Items.IRON_INGOT)
                    .unlockedBy("has_paper", has(Items.PAPER))
                    .save(exporter, id("wallpaper_scraper"));

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DMRegistry.WALLPAPERING_TABLE_BLOCK.get())
                    .pattern("pip")
                    .pattern("www")
                    .pattern("www")
                    .define('w', ItemTags.PLANKS)
                    .define('p', Items.PAPER)
                    .define('i', ConventionalItemTags.DYES)
                    .unlockedBy("has_paper", has(Items.PAPER))
                    .save(exporter, id("wallpapering_table"));
        }

        private static WallpaperingRecipe.Builder wallpaperFromItem(ItemLike itemLike, WallpaperType wallpaper, int count) {
            return WallpaperingRecipe.Builder.wallpapering(Ingredient.of(itemLike), Ingredient.of(), new ItemStack(DMRegistry.WALLPAPER_ITEMS.apply(wallpaper).get(), count));
        }

        private static void exportTrim(ItemLike planks, WallpaperType wallpaper, String name, Consumer<FinishedRecipe> exporter) {
            wallpaperFromItem(planks, wallpaper, 8)
                    .unlocks("has_planks", has(planks))
                    .save(exporter, id(name));
        }

        private static void exportQuoins(ItemLike bricks, Pair<WallpaperType, WallpaperType> quoins, String name, Consumer<FinishedRecipe> exporter) {
            wallpaperFromItem(bricks, quoins.getFirst(), 6)
                    .unlocks("has_bricks", has(bricks))
                    .save(exporter, id(name));
        }
    }

    private static class DMLootTables extends FabricBlockLootTableProvider {
        protected DMLootTables(FabricDataOutput dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generate() {
            this.createDoorTable(DMRegistry.ACACIA_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.BIRCH_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.CRIMSON_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.DARK_OAK_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.JUNGLE_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.MANGROVE_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.OAK_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.SPRUCE_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.WARPED_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.IRON_DOOR.getFirst().get());
            this.createDoorTable(DMRegistry.CHAIN_LINK_DOOR.getFirst().get());
        }
    }

    private static class DMLang extends FabricLanguageProvider {
        protected DMLang(FabricDataOutput dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {
            builder.add(DMRegistry.ACACIA_DOOR.getFirst().get(), "Acacia Door");
            builder.add(DMRegistry.BIRCH_DOOR.getFirst().get(), "Birch Door");
            builder.add(DMRegistry.CRIMSON_DOOR.getFirst().get(), "Crimson Door");
            builder.add(DMRegistry.DARK_OAK_DOOR.getFirst().get(), "Dark Oak Door");
            builder.add(DMRegistry.JUNGLE_DOOR.getFirst().get(), "Jungle Door");
            builder.add(DMRegistry.MANGROVE_DOOR.getFirst().get(), "Mangrove Door");
            builder.add(DMRegistry.OAK_DOOR.getFirst().get(), "Oak Door");
            builder.add(DMRegistry.SPRUCE_DOOR.getFirst().get(), "Spruce Door");
            builder.add(DMRegistry.WARPED_DOOR.getFirst().get(), "Warped Door");
            builder.add(DMRegistry.IRON_DOOR.getFirst().get(), "Iron Door");
            builder.add(DMRegistry.CHAIN_LINK_DOOR.getFirst().get(), "Chain-Link Door");
            builder.add(DMRegistry.IRON_FENCE.get(), "Iron Fence");
            builder.add(DMRegistry.WALLPAPER_SCRAPER.get(), "Wallpaper Scraper");
            builder.add(DMRegistry.WALLPAPERING_TABLE_BLOCK.get(), "Wallpapering Table");
            builder.add(WallpaperingTableBlock.CONTAINER_TITLE_KEY, "Wallpapering Table");
            builder.add(Util.makeDescriptionId("itemGroup", id("decomod")), "DecoMod");
            add(builder, DMRegistry.TEST_WALLPAPER.get(), "Test Wallpaper");
            add(builder, DMRegistry.IRON_BAND.get(), "Iron Band");
            add(builder, DMRegistry.ACACIA_TRIM.get(), "Acacia Trim");
            add(builder, DMRegistry.BIRCH_TRIM.get(), "Birch Trim");
            add(builder, DMRegistry.CRIMSON_TRIM.get(), "Crimson Trim");
            add(builder, DMRegistry.DARK_OAK_TRIM.get(), "Dark Oak Trim");
            add(builder, DMRegistry.JUNGLE_TRIM.get(), "Jungle Trim");
            add(builder, DMRegistry.MANGROVE_TRIM.get(), "Mangrove Trim");
            add(builder, DMRegistry.OAK_TRIM.get(), "Oak Trim");
            add(builder, DMRegistry.SPRUCE_TRIM.get(), "Spruce Trim");
            add(builder, DMRegistry.WARPED_TRIM.get(), "Warped Trim");
            add(builder, DMRegistry.BRICKS_QUOIN.get(), "Bricks Quoin");
            add(builder, DMRegistry.DEEPSLATE_BRICKS_QUOIN.get(), "Deepslate Bricks Quoin");
            add(builder, DMRegistry.END_STONE_BRICKS_QUOIN.get(), "End Stone Bricks Quoin");
            add(builder, DMRegistry.MUD_BRICKS_QUOIN.get(), "Mud Bricks Quoin");
            add(builder, DMRegistry.POLISHED_BLACKSTONE_BRICKS_QUOIN.get(), "Polished Blackstone Bricks Quoin");
            add(builder, DMRegistry.QUARTZ_BRICKS_QUOIN.get(), "Quartz Bricks Quoin");
            add(builder, DMRegistry.STONE_BRICKS_QUOIN.get(), "Stone Bricks Quoin");
        }

        private void add(TranslationBuilder builder, WallpaperType wallpaper, String name) {
            builder.add(Util.makeDescriptionId("wallpaper", DMRegistry.WALLPAPER_REGISTRY.get().getKey(wallpaper)), name);
        }


        private void add(TranslationBuilder builder, Pair<WallpaperType, WallpaperType> wallpapers, String name) {
            builder.add(Util.makeDescriptionId("wallpaper", DMRegistry.WALLPAPER_REGISTRY.get().getKey(wallpapers.getFirst())), name);
            builder.add(Util.makeDescriptionId("wallpaper", DMRegistry.WALLPAPER_REGISTRY.get().getKey(wallpapers.getSecond())), name);
        }
    }

    private static class DMAdvancements extends FabricAdvancementProvider {
        protected DMAdvancements(FabricDataOutput dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateAdvancement(Consumer<Advancement> consumer) {
        }
    }
}
