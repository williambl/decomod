package com.williambl.decomod.wallpaper;

import com.google.common.base.Suppliers;
import com.williambl.decomod.DMRegistry;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WallpaperType {
	private final Supplier<ResourceLocation> keySupplier = Suppliers.memoize(() -> DMRegistry.WALLPAPER_REGISTRY.get().getKey(this));
	private final Supplier<String> translationKeySupplier = Suppliers.memoize(() -> Util.makeDescriptionId("wallpaper", this.keySupplier.get()));
	private final EnumMap<Direction, Supplier<ResourceLocation>> modelLocationSuppliers =
			new EnumMap<>(Arrays.stream(Direction.values()).collect(Collectors.toMap(
					Function.identity(),
					dir -> Suppliers.memoize(() -> new ResourceLocation(this.keySupplier.get().getNamespace(), "wallpaper/"+this.keySupplier.get().getPath()+"/"+dir.getName())))));


	public Component getName() {
		return Component.translatable(this.translationKeySupplier.get());
	}

	public ResourceLocation getModelLocation(Direction dir) {
		return this.modelLocationSuppliers.get(dir).get();
	}

	public int getTint() {
		return 0;
	}

	public WallpaperType getVariant(UseOnContext ctx) {
		return this;
	}
}