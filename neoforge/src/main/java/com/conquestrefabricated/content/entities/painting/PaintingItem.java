package com.conquestrefabricated.content.entities.painting;

import com.conquestrefabricated.api.painting.Painting;
import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.content.entities.painting.art.ArtType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author dags <dags@dags.me>
 */
public class PaintingItem extends Item implements com.conquestrefabricated.api.painting.PaintingHolder {

    private final String name;
    private final Function<String, Art<?>> art;
    private final Function<String, Painting> type;
    private final PaintingFactory<? extends HangingEntity> factory;

    public PaintingItem(String name, Function<String, Painting> type, Function<String, Art<?>> art, PaintingFactory<? extends HangingEntity> factory) {
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("conquest", name))));
        this.name = name;
        this.art = art;
        this.type = type;
        this.factory = factory;
    }

    @Nullable
    private static CompoundTag getCustomNbt(ItemStack stack) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);
        return component != null ? component.copyTag() : null;
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag painting = getCustomNbt(stack);
        if (painting == null) {
            return super.getName(stack);
        }

        CompoundTag data = painting.getCompound(Art.DATA_TAG).orElse(new CompoundTag());
        String typeName = data.getString(Art.TYPE_TAG).orElse("");
        String artName = data.getString(Art.ART_TAG).orElse("");

        ModPainting type = ModPainting.fromId(typeName);
        String displayName = "";

        if (type.isPresent()) {
            displayName = type.getDisplayName();
            ArtType art = ArtType.fromName(artName);
            if (art != null) {
                displayName = displayName + " " + art.getDisplayName(type.getTranslationKey());
            }
        } else if (!artName.isEmpty()) {
            displayName = artName;
        }

        return Component.literal(displayName);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        return super.use(world, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        Level world = context.getLevel();
        InteractionHand hand = context.getHand();
        Direction side = context.getClickedFace();
        if (player.getPose() == Pose.CROUCHING) {
            use(world, player, hand);
            return InteractionResult.FAIL;
        }

        ItemStack stack = player.getItemInHand(hand);
        CompoundTag data = getCustomNbt(stack);
        if (data == null) {
            return InteractionResult.FAIL;
        }

        CompoundTag paint = data.getCompound(Art.DATA_TAG).orElse(new CompoundTag());
        String paintType = paint.getString(Art.TYPE_TAG).orElse("");
        String paintArt = paint.getString(Art.ART_TAG).orElse("");
        if (paintType.isEmpty() || paintArt.isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (side != Direction.DOWN && side != Direction.UP) {
            BlockPos pos = context.getClickedPos().relative(side);
            HangingEntity painting = factory.create(world, pos, side, paintType, paintArt);
            if (painting == null) {
                return InteractionResult.FAIL;
            }

            if (!world.isClientSide()) {
                world.addFreshEntity(painting);
                painting.playPlacementSound();
            }

            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public Art<?> getArt(ItemStack stack) {
        CompoundTag nbt = getCustomNbt(stack);
        if (nbt != null) {
            String artName = nbt.getCompound(Art.DATA_TAG).orElse(new CompoundTag()).getString(Art.ART_TAG).orElse("");
            return art.apply(artName);
        }
        return null;
    }

    @Override
    public Painting getType(ItemStack stack) {
        CompoundTag nbt = getCustomNbt(stack);
        if (nbt != null) {
            String typeName = nbt.getCompound(Art.DATA_TAG).orElse(new CompoundTag()).getString(Art.TYPE_TAG).orElse("");
            return type.apply(typeName);
        }
        return null;
    }
}
