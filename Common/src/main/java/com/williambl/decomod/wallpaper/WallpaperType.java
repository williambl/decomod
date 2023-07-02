package com.williambl.decomod.wallpaper;

import com.google.common.base.Suppliers;
import com.williambl.decomod.DMRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class WallpaperType {
	private final Supplier<String> translationKeySupplier = Suppliers.memoize(() -> Util.makeDescriptionId("wallpaper", DMRegistry.WALLPAPER_REGISTRY.get().getKey(this)));

	public Component getName() {
		return Component.translatable(this.translationKeySupplier.get());
	}
}