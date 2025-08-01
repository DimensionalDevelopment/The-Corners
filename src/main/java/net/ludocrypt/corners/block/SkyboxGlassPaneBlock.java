package net.ludocrypt.corners.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkyboxGlassPaneBlock extends CornerPaneBlock {

	public SkyboxGlassPaneBlock(Properties settings) {
		super(settings);
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return super.getShape(state, world, pos, context);
	}

}
