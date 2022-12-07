package org.altadoon.gt6x.features;

import org.altadoon.gt6x.common.Config;

public abstract class GT6XFeature {
    public abstract String name();
    public abstract void configure(Config config);
    public void beforePreInit() {}
    public abstract void preInit();
    public void afterPreInit() {}
    public abstract void init();
    public abstract void postInit();
    public void afterPostInit() {}
}
