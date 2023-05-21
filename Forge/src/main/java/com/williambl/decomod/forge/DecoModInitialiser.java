package com.williambl.decomod.forge;

import com.williambl.decomod.Constants;
import com.williambl.decomod.DecoMod;
import com.williambl.decomod.DecoModClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(Constants.MOD_ID)
public class DecoModInitialiser {
    
    public DecoModInitialiser() {
        Constants.LOG.info("Hello Forge world!");
        DecoMod.init();
    }

    @SubscribeEvent
    public void onClientInit(FMLClientSetupEvent event) {
        Constants.LOG.info("Hello Forge Client world!");
        DecoModClient.init();
    }
    
}