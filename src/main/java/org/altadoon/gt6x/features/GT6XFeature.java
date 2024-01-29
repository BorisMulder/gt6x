package org.altadoon.gt6x.features;

import org.altadoon.gt6x.common.Config;

public abstract class GT6XFeature {
    public String name() {return this.getClass().getName();}
    public abstract void configure(Config config);
    public void beforePreInit() {}
    public abstract void preInit();
    public void afterPreInit() {}
    public abstract void init();
    public void beforePostInit() {}
    public abstract void postInit();
    public void afterPostInit() {}
}
