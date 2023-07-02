package com.williambl.decomod.platform;

import com.williambl.decomod.Constants;
import com.williambl.decomod.platform.services.client.IClientHelper;

import java.util.ServiceLoader;

public class ClientServices {

    public static final IClientHelper CLIENT = load(IClientHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
