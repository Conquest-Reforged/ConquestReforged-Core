package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.block.Trapdoor;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**These static classes exists almost solely to indicate what should be climbable to data-gen (climbable tag). There are so few
 * climbable blocks that making a special property doesn't seem worthwhile, while a class still permits a bit of automation.
 */
@Render(RenderLayer.CUTOUT)
public class Ladder extends LadderBlock {

    public Ladder(Props properties) {
        super(properties.tag(BlockTags.CLIMBABLE).toSettings());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return true;
    }

    public boolean isLadder(BlockState state, LevelReader reader, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Render(RenderLayer.CUTOUT)
    public static class LadderPane extends IronBarsBlock {

        public LadderPane(Props props) {
            super(props.tag(BlockTags.CLIMBABLE).toSettings());
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
            return Shapes.empty();
        }
    }

    @Render(RenderLayer.CUTOUT)
    public static class LadderTrapdoor extends Trapdoor {

        public LadderTrapdoor(Props properties, BlockSetType type) {super(properties.tag(BlockTags.CLIMBABLE), type);}

        @Override
        public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
            return Interactions.onUseToggleItem(player, level, blockPos, state, OPEN);
        }
    }
}
