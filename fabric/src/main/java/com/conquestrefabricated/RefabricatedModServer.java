package com.conquestrefabricated;

import com.conquestrefabricated.content.blocks.init.BlockFamilyInit;
import com.conquestrefabricated.core.group.fabric.FamilyGroup;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;

public class RefabricatedModServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        BlockFamilyInit.init();
//        FamilyGroup.setAddRootItems();
//        FamilyGroup.FAMILY_GROUPS.forEach(familyGroup -> {
//            if (familyGroup.cached.isEmpty()) {
//                NonNullList<ItemStack> list = NonNullList.create();
//                familyGroup.populate(list);
//                familyGroup.sorter.apply(list);
//                familyGroup.sorter.sort(list);
//                familyGroup.cached = new ArrayList<>(list);
//            }
//        });

    }
}
