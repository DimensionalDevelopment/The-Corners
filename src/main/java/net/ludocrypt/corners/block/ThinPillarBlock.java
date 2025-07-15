package net.ludocrypt.corners.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThinPillarBlock extends RotatedPillarBlock {

	protected static final VoxelShape Y_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	protected static final VoxelShape Z_SHAPE = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 16.0D);
	protected static final VoxelShape X_SHAPE = Block.box(0.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);

	public ThinPillarBlock(Properties settings) {
		super(settings);
	}

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

		switch (state.getValue(AXIS)) {
			case X:
			default:
				return X_SHAPE;
			case Z:
				return Z_SHAPE;
			case Y:
				return Y_SHAPE;
		}

	}

}
