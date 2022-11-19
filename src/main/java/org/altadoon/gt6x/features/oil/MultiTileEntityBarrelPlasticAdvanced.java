package org.altadoon.gt6x.features.oil;

import gregapi.data.TD;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.tank.TileEntityBase08Barrel;
import net.minecraft.block.Block;

import static gregapi.data.CS.FACES_TBS;

public class MultiTileEntityBarrelPlasticAdvanced extends TileEntityBase08Barrel {
    @Override public boolean allowCovers(byte aSide) {return true;}

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        return aShouldSideBeRendered[aSide] ? BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[FACES_TBS[aSide]], mRGBa, mMaterial.contains(TD.Properties.GLOWING)), BlockTextureDefault.get(sOverlays[FACES_TBS[aSide]])) : null;
    }

    public static IIconContainer[] sColoreds = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/tanks/plasticcan/colored/bottom"),
            new Textures.BlockIcons.CustomIcon("machines/tanks/plasticcan/colored/top"),
            new Textures.BlockIcons.CustomIcon("machines/tanks/plasticcan/colored/side"),
    }, sOverlays = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/tanks/plasticcan/overlay/bottom"),
            new Textures.BlockIcons.CustomIcon("machines/tanks/plasticcan/overlay/top"),
            new Textures.BlockIcons.CustomIcon("machines/tanks/plasticcan/overlay/side"),
    };

    @Override public String getTileEntityName() {return "gt6x.multitileentity.tank.barrel.plastic.advanced";}
}