package org.altadoon.gt6x.features.electronics;

import gregapi.cover.CoverData;
import gregapi.cover.ITileEntityCoverable;
import gregapi.cover.covers.AbstractCoverAttachment;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.util.WD;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class CoverSolarPanel extends AbstractCoverAttachment {
    protected final int tier;

    protected boolean skyVisible = false, checkSky = true;

    protected static final int[] EUt = new int[] {
        4, 6, 8
    };

    private static final ITexture[] textures = new ITexture[] {
        null, // maybe amorphous-Si in future
        BlockTextureDefault.get("machines/covers/solarpanels/CdTe"),
        BlockTextureDefault.get("machines/covers/solarpanels/CIGS"),
    };

    public CoverSolarPanel(int tier) {
        this.tier = tier;
    }

    @Override
    public void addToolTips(List<String> tooltips, ItemStack stack, boolean F3_H) {
        super.addToolTips(tooltips, stack, F3_H);
        tooltips.add(LH.Chat.RED + LH.get(LH.ENERGY_OUTPUT) + ": " + LH.Chat.WHITE + EUt[tier] + " " + TD.Energy.EU.getLocalisedChatNameShort() + LH.Chat.WHITE + "/t");
    }

    @Override
    public boolean interceptCoverPlacement(byte coverSide, CoverData data, Entity player) {
        if (coverSide == SIDE_UP && data.mTileEntity.canTick() && data.mTileEntity instanceof ITileEntityEnergy energy) {
            return !energy.isEnergyAcceptingFrom(TD.Energy.EU, coverSide, false);
        }
        return true;
    }

    @Override public boolean showsConnectorFront(byte aCoverSide, CoverData aData) {return false;}

    @Override public ITexture getCoverTextureSurface(byte aCoverSide, CoverData aData) {return textures[tier];}
    @Override public ITexture getCoverTextureAttachment(byte aSide, CoverData aData, byte aTextureSide) {return aSide != aTextureSide ? BACKGROUND_COVER : BlockTextureMulti.get(BACKGROUND_COVER, getCoverTextureSurface(aSide, aData));}
    @Override public ITexture getCoverTextureHolder(byte aCoverSide, CoverData aData, byte aTextureSide) {return BACKGROUND_COVER;}

    @Override
    public void onTickPre(byte side, CoverData data, long timer, boolean isServerSide, boolean receivedBlockUpdate, boolean receivedInventoryUpdate) {
        if (!isServerSide) return;

        if ((checkSky || receivedBlockUpdate || timer % 600 == 5)) {
            checkSky = F;
            skyVisible = data.mTileEntity.getSkyAtSide(SIDE_TOP);
        }

        long energy = 0;

        if (skyVisible) {
            World worldObj = data.mTileEntity.getWorld();
            if (!worldObj.isThundering() && data.mTileEntity.getLightLevelAtSide(SIDE_TOP) > 2) {
                if (WD.dimTF(worldObj)) {
                    energy = EUt[tier] / 2;
                } else if (worldObj.isDaytime()) {
                    if (worldObj.isRaining() && data.mTileEntity.getBiome().rainfall > 0) {
                        energy = EUt[tier] / 4;
                    } else {
                        energy = EUt[tier];
                    }
                }
            }
        }

        if (energy > 0 && data.mTileEntity instanceof TileEntity tileEntity) {
            ITileEntityEnergy.Util.insertEnergyInto(TD.Energy.EU, side, energy, 1, null, tileEntity);
        } else if (energy > 0) {
            LOG.warn("Solar panel error: attached block is not a tile entity: " + data.mTileEntity);
        }
    }
}
