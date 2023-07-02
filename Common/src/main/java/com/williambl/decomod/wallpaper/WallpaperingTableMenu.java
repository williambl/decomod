package com.williambl.decomod.wallpaper;

import com.google.common.collect.Lists;
import java.util.List;

import com.williambl.decomod.DMRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WallpaperingTableMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_A = 0;
    public static final int INPUT_SLOT_B = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<WallpaperingRecipe> recipes = Lists.newArrayList();
    private Item inputAItem = Items.AIR;
    private Item inputBItem = Items.AIR;
    long lastSoundTime;
    final Slot inputSlotA;
    final Slot inputSlotB;
    final Slot resultSlot;
    Runnable slotUpdateListener = () -> {
    };
    public final Container container = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            WallpaperingTableMenu.this.slotsChanged(this);
            WallpaperingTableMenu.this.slotUpdateListener.run();
        }
    };
    final ResultContainer resultContainer = new ResultContainer();

    public WallpaperingTableMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, ContainerLevelAccess.NULL);
    }

    public WallpaperingTableMenu(int syncId, Inventory inventory, final ContainerLevelAccess levelAccess) {
        super(DMRegistry.WALLPAPERING_TABLE_MENU.get(), syncId);
        this.access = levelAccess;
        this.level = inventory.player.level;
        this.inputSlotA = this.addSlot(new Slot(this.container, INPUT_SLOT_A, 20, 33));
        this.inputSlotB = this.addSlot(new Slot(this.container, INPUT_SLOT_B, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player.level, player, stack.getCount());
                WallpaperingTableMenu.this.resultContainer.awardUsedRecipes(player);
                ItemStack inputA = WallpaperingTableMenu.this.inputSlotA.remove(1);
                ItemStack inputB = WallpaperingTableMenu.this.inputSlotB.remove(1);
                if (!inputA.isEmpty() || !inputB.isEmpty()) {
                    WallpaperingTableMenu.this.setupResultSlot();
                }

                levelAccess.execute((level, pos) -> {
                    long time = level.getGameTime();
                    if (WallpaperingTableMenu.this.lastSoundTime != time) {
                        level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        WallpaperingTableMenu.this.lastSoundTime = time;
                    }
                });

                super.onTake(player, stack);
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<WallpaperingRecipe> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlotA.hasItem() && !this.recipes.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Blocks.STONECUTTER);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonIndex) {
        if (this.isValidRecipeIndex(buttonIndex)) {
            this.selectedRecipeIndex.set(buttonIndex);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int i) {
        return i >= 0 && i < this.recipes.size();
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack stackA = this.inputSlotA.getItem();
        ItemStack stackB = this.inputSlotB.getItem();
        boolean shouldSetupRecipeList = false;
        if (!stackA.is(this.inputAItem)) {
            this.inputAItem = stackA.getItem();
            shouldSetupRecipeList = true;
        }
        if (!stackB.is(this.inputBItem)) {
            this.inputBItem = stackB.getItem();
            shouldSetupRecipeList = true;
        }

        if (shouldSetupRecipeList) {
            this.setupRecipeList(container, stackA, stackB);
        }
    }

    private void setupRecipeList(Container container, ItemStack stackA, ItemStack stackB) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!stackA.isEmpty() || !stackB.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(DMRegistry.WALLPAPERING_RECIPE_TYPE.get(), container, this.level);
        }
    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            WallpaperingRecipe recipe = this.recipes.get(this.selectedRecipeIndex.get());
            this.resultContainer.setRecipeUsed(recipe);
            this.resultSlot.set(recipe.assemble(this.container));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    @Override
    public MenuType<?> getType() {
        return MenuType.STONECUTTER;
    }

    public void registerUpdateListener(Runnable runnable) {
        this.slotUpdateListener = runnable;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            Item itemInSlot = stackInSlot.getItem();
            stack = stackInSlot.copy();
            if (slotIndex == RESULT_SLOT) { // if result slot is being shiftclicked, try taking from it...
                itemInSlot.onCraftedBy(stackInSlot, player.level, player);
                if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true)) { // and putting into inventory
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stackInSlot, stack);
            } else if (slotIndex == INPUT_SLOT_A || slotIndex == INPUT_SLOT_B) { // otherwise if it's an input slot...
                if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, false)) { // move back into inventory
                    return ItemStack.EMPTY;
                }
            } else if (this.moveItemStackTo(stackInSlot, INPUT_SLOT_A, RESULT_SLOT, false)) { // otherwise if it can fit into the input slots, do that
                // no-op
            } else if (slotIndex >= INV_SLOT_START && slotIndex < INV_SLOT_END) { // otherwise if it's an inventory slot...
                if (!this.moveItemStackTo(stackInSlot, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) { // move it into the hotbar
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= USE_ROW_SLOT_START && slotIndex < USE_ROW_SLOT_END && !this.moveItemStackTo(stackInSlot, INV_SLOT_START, INV_SLOT_END, false)) { // otherwise, if its in the hotbar, try moving it into the inventory.
                return ItemStack.EMPTY; // otherwise, give up
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
            this.broadcastChanges();
        }

        return stack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((level, pos) -> this.clearContainer(player, this.container));
    }
}