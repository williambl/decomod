package com.williambl.decomod;

import com.mojang.datafixers.util.Pair;
import com.williambl.decomod.platform.Services;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class DMRegistry {
    public static final Pair<Supplier<DoorBlock>, Supplier<DoubleHighBlockItem>> CUSTOM_DOOR =
            Services.REGISTRATION_HELPER.registerDoor("custom_door", BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR), new Item.Properties());
    public static final Supplier<IronBarsBlock> IRON_FENCE =
            Services.REGISTRATION_HELPER.registerBlock("iron_fence", () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)) {}, new Item.Properties());
}
