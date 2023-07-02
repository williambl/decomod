package com.williambl.decomod.platform;

import com.williambl.decomod.Constants;
import com.williambl.decomod.platform.services.IIntegrationHelper;
import com.williambl.decomod.platform.services.IPlatformHelper;
import com.williambl.decomod.platform.services.IRegistrationHelper;
import com.williambl.decomod.platform.services.IWallpaperHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegistrationHelper REGISTRATION_HELPER = load(IRegistrationHelper.class);
    public static final IIntegrationHelper INTEGRATIONS = load(IIntegrationHelper.class);
    public static final IWallpaperHelper WALLPAPERS = load(IWallpaperHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
