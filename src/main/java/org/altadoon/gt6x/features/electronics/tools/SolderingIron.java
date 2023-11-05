package org.altadoon.gt6x.features.electronics.tools;

import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.old.Textures;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.items.IFluidFillableToolStats;
import org.altadoon.gt6x.common.items.Tools;

import java.util.HashMap;
import java.util.List;

import static gregapi.data.CS.UNCOLOURED;

public class SolderingIron implements IFluidFillableToolStats {
    public static final int ID = 0;
    public static final int ID_EMPTY = 1;

    private static final Textures.ItemIcons.CustomIcon handleTexture = new Textures.ItemIcons.CustomIcon(Tools.TEXTURES_DIR + "solderingIron_handle");
    private static final Textures.ItemIcons.CustomIcon tipTexture = new Textures.ItemIcons.CustomIcon(Tools.TEXTURES_DIR + "solderingIron_tip");

    private static final HashMap<FluidStack, Integer> usableFluids = new HashMap<>() {{
        put(MT.Sn.mLiquid, 1);
        put(MT.Pb.mLiquid, 1);
        put(MT.SolderingAlloy.mLiquid, 2);
        put(MTx.SolderingPaste.mLiquid, 2);
    }};

    @Override
    public int usesAddedPerFluidUnit(FluidStack fluidStack) {
        return usableFluids.getOrDefault(fluidStack, 0);
    }

    @Override
    public int getMaxUses() {
        return 20;
    }

    @Override
    public int getRenderPasses() {
        return 4;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        return switch (renderPass) {
            case 0 -> tipTexture.getIcon(0);
            case 1 -> tipTexture.getIcon(1);
            case 2 -> handleTexture.getIcon(0);
            case 3 -> handleTexture.getIcon(1);
            default -> null;
        };
    }

    @Override
    public short[] getRGBa(ItemStack aStack, int aRenderPass) {
        return switch (aRenderPass) {
            case 0 -> MT.StainlessSteel.mRGBaSolid;
            case 2 -> MT.WOODS.Oak.mRGBaSolid;
            default -> UNCOLOURED;
        };
    }

    static {
        LH.add("gt6x.solderingiron.usableFluids", "Usable fluids:");
        LH.add("gt6x.solderingiron.usesPerUnit", "use(s) per Unit");
    }

    @Override
    public void addAdditionalToolTips(List<String> tooltips, ItemStack stack, boolean f3_H) {
        tooltips.add(LH.Chat.CYAN + LH.get("gt6x.solderingiron.usableFluids"));
        usableFluids.forEach((fs, amount) -> {
            tooltips.add(LH.Chat.WHITE + fs.getLocalizedName() + ": " + UT.Code.makeString(amount) + " " + LH.get("gt6x.solderingiron.usesPerUnit"));
        });
    }
}
