package com.conquestrefabricated.content.blocks.group.neoforge;

import com.conquestrefabricated.core.group.neoforge.FamilyGroup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModGroupsEvent {

    @SubscribeEvent
    public static void registerItemGroups(RegisterEvent event) {
        FamilyGroup.FAMILY_GROUPS.forEach(familyGroup -> {
            event.register(Registries.CREATIVE_MODE_TAB, itemGroupRegisterHelper -> {
                itemGroupRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", familyGroup.label), familyGroup);
            });

        });

        //FamilyGroup.stream().forEach(familyGroup -> {
        //});
    }
}
