package com.williambl.decomod.fabric;

import com.williambl.decomod.Constants;
import com.williambl.decomod.DecoMod;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class DecoModInitialiser implements ModInitializer, EntityComponentInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");
        DecoMod.init();
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    }
}
