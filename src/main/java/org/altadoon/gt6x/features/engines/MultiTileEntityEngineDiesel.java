package org.altadoon.gt6x.features.engines;

import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregtech.tileentity.energy.generators.MultiTileEntityMotorLiquid;
import net.minecraft.block.Block;

import static gregapi.data.CS.*;

public class MultiTileEntityEngineDiesel extends MultiTileEntityMotorLiquid {
    @Override public float getSurfaceSizeAttachable (byte aSide) {return ALONG_AXIS[aSide][mFacing]?0.5F:0.25F;}
    @Override public boolean isSideSolid2           (byte aSide) {return ALONG_AXIS[aSide][mFacing];}
    @Override public boolean isSurfaceOpaque2       (byte aSide) {return ALONG_AXIS[aSide][mFacing];}
    @Override public boolean allowCovers            (byte aSide) {return ALONG_AXIS[aSide][mFacing];}

    @Override
    public int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {
        return 7;
    }

    @Override
    public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
        switch (aRenderPass) {
            case 0: return box(aBlock, PX_P[                          1], PX_P[                       1], PX_P[                       1], PX_N[                       1], PX_N[                       1], PX_N[                       1]);
            case 1: return box(aBlock, PX_P[                          0], PX_P[                       0], PX_P[                       0], PX_N[SIDES_AXIS_X[mFacing]?14: 0], PX_N[SIDES_AXIS_Y[mFacing]?14: 0], PX_N[SIDES_AXIS_Z[mFacing]?14: 0]);
            case 2: return box(aBlock, PX_P[SIDES_AXIS_X[mFacing]?14: 0], PX_P[SIDES_AXIS_Y[mFacing]?14: 0], PX_P[SIDES_AXIS_Z[mFacing]?14: 0], PX_N[                        0], PX_N[                       0], PX_N[                       0]);
            case 3: return box(aBlock, PX_P[SIDES_AXIS_Y[mFacing]? 0: 6], PX_P[SIDES_AXIS_Z[mFacing]? 0: 6], PX_P[SIDES_AXIS_X[mFacing]? 0: 6], PX_N[SIDES_AXIS_Y[mFacing]? 0: 6], PX_N[SIDES_AXIS_Z[mFacing]? 0: 6], PX_N[SIDES_AXIS_X[mFacing]? 0: 6]);
            case 4: return box(aBlock, PX_P[SIDES_AXIS_Z[mFacing]? 0: 6], PX_P[SIDES_AXIS_X[mFacing]? 0: 6], PX_P[SIDES_AXIS_Y[mFacing]? 0: 6], PX_N[SIDES_AXIS_Z[mFacing]? 0: 6], PX_N[SIDES_AXIS_X[mFacing]? 0: 6], PX_N[SIDES_AXIS_Y[mFacing]? 0: 6]);
            case 5: return box(aBlock, PX_P[SIDES_AXIS_X[mFacing]? 0: 4], PX_P[SIDES_AXIS_Y[mFacing]? 0: 4], PX_P[SIDES_AXIS_Z[mFacing]? 0: 4], PX_N[SIDES_AXIS_X[mFacing]? 0: 4], PX_N[SIDES_AXIS_Y[mFacing]? 0: 4], PX_N[SIDES_AXIS_Z[mFacing]? 0: 4]);
            case 6: return box(aBlock, PX_P[SIDES_AXIS_X[mFacing]? 0: 3], PX_P[SIDES_AXIS_Y[mFacing]? 0: 3], PX_P[SIDES_AXIS_Z[mFacing]? 0: 3], PX_N[SIDES_AXIS_X[mFacing]? 0: 3], PX_N[SIDES_AXIS_Y[mFacing]? 0: 3], PX_N[SIDES_AXIS_Z[mFacing]? 0: 3]);
        }
        return false;
    }

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
        if (aSide == mFacing)         return BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[0], mRGBa), BlockTextureDefault.get((mActivity.mState>0?sOverlaysActive:sOverlays)[0]));
        if (aSide == OPOS[mFacing])   return BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[1], mRGBa), BlockTextureDefault.get((mActivity.mState>0?sOverlaysActive:sOverlays)[1]));
        return BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[2], mRGBa), BlockTextureDefault.get((mActivity.mState>0?sOverlaysActive:sOverlays)[2]));
    }

    @Override
    public String getTileEntityName() {return "gt6x.multitileentity.generator.motor_diesel";}
}
