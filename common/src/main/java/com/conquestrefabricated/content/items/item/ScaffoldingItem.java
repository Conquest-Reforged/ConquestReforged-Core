package com.conquestrefabricated.content.items.item;

import com.conquestrefabricated.content.blocks.block.decor.Scaffolding;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ScaffoldingItem extends net.minecraft.world.item.ScaffoldingBlockItem {

    private Block blockIn;

    public ScaffoldingItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
        this.blockIn = blockIn;
    }

    @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState blockstate = world.getBlockState(blockpos);
        Block block = this.getBlock();
        if (blockstate.getBlock() != block) {
            return Scaffolding.getDistance1(world, blockpos) == 7 ? null : context;
        } else {
            Direction direction;
            if (context.isSecondaryUseActive()) {
                direction = context.isInside() ? context.getClickedFace().getOpposite() : context.getClickedFace();
            } else {
                direction = context.getClickedFace() == Direction.UP ? context.getNearestLookingDirection() : Direction.UP;
            }

            int i = 0;
            BlockPos.MutableBlockPos blockposMutable = blockpos.mutable().move(direction);

            while(i < 7) {
                if (!world.isClientSide() && !world.isInWorldBounds(blockposMutable)) {
                    Player playerentity = context.getPlayer();
                    int j = world.getHeight();
                    if (playerentity instanceof ServerPlayer && blockposMutable.getY() >= j) {
                        ClientboundSystemChatPacket schatpacket = new ClientboundSystemChatPacket((Component.translatable("build.tooHigh", j)).withStyle(ChatFormatting.RED), false);
                        ((ServerPlayer)playerentity).connection.send(schatpacket);
                    }
                    break;
                }

                blockstate = world.getBlockState(blockposMutable);
                if (blockstate.getBlock() != this.getBlock()) {
                    if (blockstate.canBeReplaced(context)) {
                        return BlockPlaceContext.at(context, blockposMutable, direction);
                    }
                    break;
                }

                blockposMutable.move(direction);
                if (direction.getAxis().isHorizontal()) {
                    ++i;
                }
            }

            return null;
        }
    }
}
