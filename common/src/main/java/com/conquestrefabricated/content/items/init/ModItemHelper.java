package com.conquestrefabricated.content.items.init;

import com.conquestrefabricated.content.items.item.ArmorItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class ModItemHelper {
    private final String namespace;

    public ModItemHelper(String namespace) {
        this.namespace = namespace;
    }

    public Item.Properties props(String name) {
        return new Item.Properties().setId(
                ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(this.namespace, name)));
    }

    public <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
        T item = factory.apply(props(name));

        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(this.namespace, name), item);
    }

    public ArmorItem armor(String name, ArmorMaterial material, ArmorType type) {
        return register(name, properties -> new ArmorItem(material, type, properties.stacksTo(1)));
    }

    private static Item.Properties base(Item.Properties props, ToolMaterial material, float attackDuration) {
        return props.durability(material.durability())
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue())
                .delayedHolderComponent(DataComponents.DAMAGE_TYPE, DamageTypes.SPEAR)
                .component(DataComponents.PIERCING_WEAPON, new PiercingWeapon(true, false,
                        Optional.of(SoundEvents.SPEAR_ATTACK), Optional.of(SoundEvents.SPEAR_HIT)))
                .component(DataComponents.SWING_ANIMATION, new SwingAnimation(SwingAnimationType.STAB, (int)(attackDuration * 20.0F)))
                .component(DataComponents.USE_EFFECTS, new UseEffects(true, false, 1.0F))
                .component(DataComponents.WEAPON, new Weapon(1));
    }

    public static Item.Properties pike(Item.Properties props, ToolMaterial material, float attackDuration,
                                       float minReach, float maxReach, float hitboxMargin) {
        return base(props, material, attackDuration)
                .component(DataComponents.ATTACK_RANGE, new AttackRange(minReach, maxReach, minReach, maxReach, hitboxMargin, hitboxMargin))
                .attributes(meleeAttributes(material, attackDuration));
    }

    public static Item.Properties lance(Item.Properties props, ToolMaterial material, float attackDuration,
                                        float damageMultiplier, float delay, float dismountTime, float dismountThreshold,
                                        float knockbackTime, float knockbackThreshold, float damageTime, float damageThreshold) {
        return base(props, material, attackDuration)
                .component(DataComponents.KINETIC_WEAPON, new KineticWeapon(10, (int)(delay * 20.0F),
                        KineticWeapon.Condition.ofAttackerSpeed((int)(dismountTime * 20.0F), dismountThreshold),
                        KineticWeapon.Condition.ofAttackerSpeed((int)(knockbackTime * 20.0F), knockbackThreshold),
                        KineticWeapon.Condition.ofRelativeSpeed((int)(damageTime * 20.0F), damageThreshold),
                        0.38F, damageMultiplier, Optional.of(SoundEvents.SPEAR_USE), Optional.of(SoundEvents.SPEAR_HIT)))
                .component(DataComponents.ATTACK_RANGE, new AttackRange(2.0F, 4.5F, 2.0F, 6.5F, 0.125F, 0.5F))
                .component(DataComponents.MINIMUM_ATTACK_CHARGE, 1.0F)
                .attributes(meleeAttributes(material, attackDuration));
    }

    private static ItemAttributeModifiers meleeAttributes(ToolMaterial material, float attackDuration) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                        material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                        (1.0F / attackDuration) - 4.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static Item.Properties shield(Item.Properties props, int durability) {
        return props.durability(durability)
                .repairable(ItemTags.WOODEN_TOOL_MATERIALS)
                .equippableUnswappable(EquipmentSlot.OFFHAND)
                .delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(
                        0.25F, 1.0F,
                        List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                        new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                        Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                        Optional.of(SoundEvents.SHIELD_BLOCK),
                        Optional.of(SoundEvents.SHIELD_BREAK)))
                .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK);
    }

    public static Item.Properties sword(Item.Properties props, ToolMaterial material,
                                        float attackDamageBaseline, float attackSpeedBaseline, float minReach, float maxReach, float hitboxMargin) {
        return props.sword(material, attackDamageBaseline, attackSpeedBaseline)
                .component(DataComponents.ATTACK_RANGE, new AttackRange(minReach, maxReach, minReach, maxReach, hitboxMargin, hitboxMargin));
    }

    public static Item.Properties axe(Item.Properties props, ToolMaterial material,
                                      float attackDamageBaseline, float attackSpeedBaseline, float minReach, float maxReach, float hitboxMargin) {
        return props.durability(material.durability())
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue())
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                                attackDamageBaseline + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                attackSpeedBaseline, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .build())
                .component(DataComponents.ATTACK_RANGE, new AttackRange(minReach, maxReach, minReach, maxReach, hitboxMargin, hitboxMargin));
    }

    public static Item.Properties bow(Item.Properties props, int durability, int enchantability) {
        return props.durability(durability).enchantable(enchantability);
    }

    public static Item.Properties crossbow(Item.Properties props, int durability, int enchantability) {
        return props.stacksTo(1)
                .durability(durability)
                .component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                .enchantable(enchantability);
    }

    public final class MeleeReach {
        public static final float VANILLA_MIN = 0.0f;
        public static final float VANILLA_MAX = 3.0f;
        public static final float VANILLA_MARGIN = 0.0f;

        // Variance examples — short/long blades and hafted weapons
        public static final float SHORT_MAX = 2.5f;   // gladius, hand axe
        public static final float LONG_MAX = 3.5f;    // zweihander, poleaxe
        public static final float REACH_MARGIN = 0.1f;

        private MeleeReach() {}
    }
}