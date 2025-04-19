package org.altadoon.gt6x.common.items;

import gregapi.code.ItemStackContainer;
import gregapi.code.ItemStackSet;
import gregapi.data.CS;
import gregapi.item.multiitem.MultiItemTool;
import gregapi.item.multiitem.MultiItemToolWithCompat;
import gregapi.item.multiitem.tools.IToolStats;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class Tools {
    public static final String TEXTURES_DIR = "gt6x:tools/";

    public static MultiItemTool metaTool;
    public static MultiItemRefillableTool refillableMetaTool;

    private static final ItemStackSet<ItemStackContainer> TOOL_LIST = new ItemStackSet<>();
    private static final Map<String, ItemStackSet<ItemStackContainer>> TOOL_LISTS = new HashMap<>();

    private static ItemStackSet<ItemStackContainer> get(String aToolType) {
        ItemStackSet<ItemStackContainer> rSet = TOOL_LISTS.get(aToolType);
        if (rSet == null)
            TOOL_LISTS.put(aToolType, rSet = new ItemStackSet<>());
        return rSet;
    }

    public static boolean contains(String aToolType, ItemStack aStack) {
        return get(aToolType).contains(aStack, true);
    }

    private static boolean add(String aToolType, ItemStack aStack) {
        if (TOOL_LIST.add(aStack))
            return get(aToolType).add(aStack);
        return false;
    }

    public static void add(int id, String oreDictName, String english, String tooltip, IToolStats tool, Object... randomData) {
        add(oreDictName, metaTool.addTool(id, english, tooltip, tool, randomData));
    }

    public static void addRefillable(int id, String oreDictName, String english, String tooltip, IFluidFillableToolStats tool, Object... randomData) {
        Pair<ItemStack, ItemStack> tools = refillableMetaTool.addTool((short)id, english, tooltip, tool, randomData);
        add(oreDictName, tools.getKey());
        add(oreDictName + "_empty", tools.getValue());

        //TODO not working
        CS.ItemsGT.addNEIRedirects(refillableMetaTool.make(id), refillableMetaTool.make(id+1));
    }

    public static void init(String modId) {
        metaTool = new MultiItemToolWithCompat(modId, "metatool.normal");
        refillableMetaTool = new MultiItemRefillableTool(modId, "metatool.refillable");
    }
}
