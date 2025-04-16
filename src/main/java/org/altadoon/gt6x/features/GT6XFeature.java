package org.altadoon.gt6x.features;

import org.altadoon.gt6x.common.Config;

public abstract class GT6XFeature {
    public abstract String name();
    public void configure(Config config){}
    public void beforeGt6PreInit() {}
    public abstract void preInit();
    public void afterGt6PreInit() {}
    public void beforeGt6Init() {}
    public abstract void init();
    public void afterGt6Init() {}
    public void beforeGt6PostInit() {}
    public abstract void postInit();
    public void afterGt6PostInit() {}
}
