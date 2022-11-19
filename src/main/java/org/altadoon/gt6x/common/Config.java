package org.altadoon.gt6x.common;

import gregapi.data.CS;
import net.minecraftforge.common.config.Configuration;
import org.altadoon.gt6x.features.GT6XFeature;

import java.io.File;
import java.util.ArrayList;

import static org.altadoon.gt6x.common.Log.LOG;

public class Config {
    private final ArrayList<GT6XFeature> features;

    public Configuration cfg;

    public Config(Class<? extends GT6XFeature>[] allFeatures) {
        cfg = new Configuration(new File(CS.DirectoriesGT.CONFIG_GT, "gt6x.cfg"));
        cfg.load();
        features = readFeatures(allFeatures);
        cfg.save();
    }

    public ArrayList<GT6XFeature> getEnabledFeatures() { return features; }

    private ArrayList<GT6XFeature> readFeatures(Class<? extends GT6XFeature>[] allFeatures) {
        ArrayList<GT6XFeature> result = new ArrayList<>();

        for (Class<? extends GT6XFeature> featureClass : allFeatures) {
            try {
                GT6XFeature feature = featureClass.newInstance();
                String name = feature.name();

                boolean enabled = cfg.get(name, "enabled", true).getBoolean();
                if (enabled) {
                    feature.configure(this);
                    result.add(feature);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("{}: ctor invocation failed. Your GT6X feature class should have a public nullary constructor. Disabling feature...", featureClass.getCanonicalName());
            }
        }

        return result;
    }
}
