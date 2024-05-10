package org.altadoon.gt6x.features.electronics;

import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregtech.tileentity.energy.generators.MultiTileEntitySolarPanelElectric;
import net.minecraft.block.Block;

import static gregapi.data.CS.SIDES_HORIZONTAL;
import static gregapi.data.CS.SIDES_TOP;

public class MultiTileEntitySolarPanelX extends MultiTileEntitySolarPanelElectric {
    public static final int polySiOutput = 6;
    public static final int monoSiOutput = 10;
    public static final int GaAsOutput = 32;
    public static final int numTiers = 3;
    public static final int numPasses = 2;
    public static final int numSides = 5;

    protected int getTier() {
        switch ((int)mOutput) {
            case polySiOutput -> { return 0; }
            case monoSiOutput -> { return 1; }
            case GaAsOutput -> { return 2; }
            default -> { return -1; }
        }
    }

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (!aShouldSideBeRendered[aSide]) return null;
        int index = SIDES_TOP[aSide]?4:aSide==mFacing?SIDES_HORIZONTAL[aSide]?0:2:SIDES_HORIZONTAL[aSide]?1:3;
        int tier = getTier();
        return BlockTextureMulti.get(BlockTextureDefault.get(icons[tier][0][index], mRGBa), BlockTextureDefault.get(icons[tier][1][index]));
    }

    public static IIconContainer[][][] icons = new IIconContainer[numTiers][numPasses][numSides];
    public static String[] tier_names = { "solarpanel_poly_si", "solarpanel_mono_si", "solarpanel_gaas" };
    public static String[] pass_names = { "colored", "overlay" };
    public static String[] side_names = { "side_facing", "side", "bottom_facing", "bottom", "top" };

    static {
        for (int tier = 0; tier < numTiers; tier++) {
            for (int pass = 0; pass < numPasses; pass++) {
                for (int side = 0; side < numSides; side++) {
                    icons[tier][pass][side] = new Textures.BlockIcons.CustomIcon(
                        String.join("/", new String[] {
                            "machines",
                            "solarpanels",
                            tier_names[tier],
                            pass_names[pass],
                            side_names[side]
                        })
                    );
                }
            }
        }
    }

    @Override public String getTileEntityName() {
        return "gt6x.multitileentity." + tier_names[getTier()];
    }
}
