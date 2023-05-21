package com.williambl.decomod.fabric;

import com.williambl.decomod.Constants;
import com.williambl.decomod.DecoModClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class DecoModClientInitialiser implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("Hello Fabric Client world!");
        DecoModClient.init();
    }
}
