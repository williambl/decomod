package com.williambl.decomod.fabric;

import com.williambl.decomod.Constants;
import com.williambl.decomod.DecoMod;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.fabricmc.api.ModInitializer;

public class DecoModInitialiser implements ModInitializer, EntityComponentInitializer, ChunkComponentInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOGGER.info("Hello Fabric world!");
        DecoMod.init();
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    }
}
