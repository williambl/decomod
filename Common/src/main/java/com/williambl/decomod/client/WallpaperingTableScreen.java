package com.williambl.decomod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;

import com.williambl.decomod.wallpaper.WallpaperingRecipe;
import com.williambl.decomod.wallpaper.WallpaperingTableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemDisplayContext;

import static com.williambl.decomod.DecoMod.id;

public class WallpaperingTableScreen extends AbstractContainerScreen<WallpaperingTableMenu> {
    private static final ResourceLocation BG_LOCATION = id("textures/gui/container/wallpapering_table.png");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int RECIPES_COLUMNS = 4;
    private static final int RECIPES_ROWS = 3;
    private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
    private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
    private static final int SCROLLER_FULL_HEIGHT = 54;
    private static final int RECIPES_X = 52;
    private static final int RECIPES_Y = 14;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public WallpaperingTableScreen(WallpaperingTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        menu.registerUpdateListener(this::containerChanged);
        this.titleLabelY--;
    }

    @Override
    public void render(GuiGraphics graphics, int $$1, int $$2, float $$3) {
        super.render(graphics, $$1, $$2, $$3);
        this.renderTooltip(graphics, $$1, $$2);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float $$1, int $$2, int $$3) {
        this.renderBackground(graphics);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        graphics.blit(BG_LOCATION, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        int $$6 = (int)(41.0F * this.scrollOffs);
        graphics.blit(BG_LOCATION, $$4 + 119, $$5 + SCROLLER_HEIGHT + $$6, 176 + (this.isScrollBarActive() ? 0 : SCROLLER_WIDTH), 0, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        int $$7 = this.leftPos + RECIPES_X;
        int $$8 = this.topPos + RECIPES_Y;
        int $$9 = this.startIndex + SCROLLER_WIDTH;
        this.renderButtons(graphics, $$2, $$3, $$7, $$8, $$9);
        this.renderRecipes(graphics, $$7, $$8, $$9);
    }

    @Override
    protected void renderTooltip(GuiGraphics $$0, int $$1, int $$2) {
        super.renderTooltip($$0, $$1, $$2);
        if (this.displayRecipes) {
            int $$3 = this.leftPos + RECIPES_X;
            int $$4 = this.topPos + RECIPES_Y;
            int $$5 = this.startIndex + SCROLLER_WIDTH;
            List<WallpaperingRecipe> $$6 = this.menu.getRecipes();

            for(int $$7 = this.startIndex; $$7 < $$5 && $$7 < this.menu.getNumRecipes(); ++$$7) {
                int $$8 = $$7 - this.startIndex;
                int $$9 = $$3 + $$8 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
                int $$10 = $$4 + $$8 / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
                if ($$1 >= $$9 && $$1 < $$9 + RECIPES_IMAGE_SIZE_WIDTH && $$2 >= $$10 && $$2 < $$10 + RECIPES_IMAGE_SIZE_HEIGHT) {
                    $$0.renderTooltip(this.font, $$6.get($$7).getResultItem(this.minecraft.level.registryAccess()), $$1, $$2);
                }
            }
        }

    }

    private void renderButtons(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        for(int $$6 = this.startIndex; $$6 < $$5 && $$6 < this.menu.getNumRecipes(); ++$$6) {
            int $$7 = $$6 - this.startIndex;
            int $$8 = $$3 + $$7 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int $$9 = $$7 / RECIPES_COLUMNS;
            int $$10 = $$4 + $$9 * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            int $$11 = this.imageHeight;
            if ($$6 == this.menu.getSelectedRecipeIndex()) {
                $$11 += RECIPES_IMAGE_SIZE_HEIGHT;
            } else if ($$1 >= $$8 && $$2 >= $$10 && $$1 < $$8 + RECIPES_IMAGE_SIZE_WIDTH && $$2 < $$10 + RECIPES_IMAGE_SIZE_HEIGHT) {
                $$11 += 36;
            }

            $$0.blit(BG_LOCATION, $$8, $$10 - 1, 0, $$11, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT);
        }

    }

    private void renderRecipes(GuiGraphics graphics, int $$0, int $$1, int $$2) {
        List<WallpaperingRecipe> $$3 = this.menu.getRecipes();

        for(int $$4 = this.startIndex; $$4 < $$2 && $$4 < this.menu.getNumRecipes(); ++$$4) {
            int $$5 = $$4 - this.startIndex;
            int $$6 = $$0 + $$5 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int $$7 = $$5 / RECIPES_COLUMNS;
            int $$8 = $$1 + $$7 * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            graphics.renderItem($$3.get($$4).getResultItem(this.minecraft.level.registryAccess()), $$6, $$8);
        }

    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int $$3 = this.leftPos + RECIPES_X;
            int $$4 = this.topPos + RECIPES_Y;
            int $$5 = this.startIndex + SCROLLER_WIDTH;

            for(int $$6 = this.startIndex; $$6 < $$5; ++$$6) {
                int $$7 = $$6 - this.startIndex;
                double $$8 = $$0 - (double)($$3 + $$7 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH);
                double $$9 = $$1 - (double)($$4 + $$7 / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT);
                if ($$8 >= 0.0D && $$9 >= 0.0D && $$8 < RECIPES_IMAGE_SIZE_WIDTH && $$9 < RECIPES_IMAGE_SIZE_HEIGHT && this.menu.clickMenuButton(this.minecraft.player, $$6)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, $$6);
                    return true;
                }
            }

            $$3 = this.leftPos + 119;
            $$4 = this.topPos + 9;
            if ($$0 >= (double)$$3 && $$0 < (double)($$3 + SCROLLER_WIDTH) && $$1 >= (double)$$4 && $$1 < (double)($$4 + SCROLLER_FULL_HEIGHT)) {
                this.scrolling = true;
            }
        }

        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.scrolling && this.isScrollBarActive()) {
            int $$5 = this.topPos + RECIPES_Y;
            int $$6 = $$5 + SCROLLER_FULL_HEIGHT;
            this.scrollOffs = ((float)$$1 - (float)$$5 - 7.5F) / ((float)($$6 - $$5) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5D) * RECIPES_COLUMNS;
            return true;
        } else {
            return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
        }
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        if (this.isScrollBarActive()) {
            int $$3 = this.getOffscreenRows();
            float $$4 = (float)$$2 / (float)$$3;
            this.scrollOffs = Mth.clamp(this.scrollOffs - $$4, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)$$3) + 0.5D) * RECIPES_COLUMNS;
        }

        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && this.menu.getNumRecipes() > SCROLLER_WIDTH;
    }

    protected int getOffscreenRows() {
        return (this.menu.getNumRecipes() + RECIPES_COLUMNS - 1) / RECIPES_COLUMNS - RECIPES_ROWS;
    }

    private void containerChanged() {
        this.displayRecipes = this.menu.hasInputItem();
        if (!this.displayRecipes) {
            this.scrollOffs = 0.0F;
            this.startIndex = 0;
        }
    }
}