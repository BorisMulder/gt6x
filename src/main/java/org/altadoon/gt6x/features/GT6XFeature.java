package org.altadoon.gt6x.features;

import gregapi.data.CS;
import org.altadoon.gt6x.common.Config;

public abstract class GT6XFeature {
    public abstract String name();
    public abstract void preInit();
    public abstract void init();
    public abstract void postInit();
    public abstract void postPostInit();

    public abstract void configure(Config config);
}
