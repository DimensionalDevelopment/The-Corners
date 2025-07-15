package net.ludocrypt.corners.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkyboxGlassSlabBlock extends SlabBlock {

	public SkyboxGlassSlabBlock(Properties settings) {
		super(settings);
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return super.getShape(state, world, pos, context);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {

		if (stateFrom.is(this)) {

			if (state.getValue(TYPE).equals(SlabType.TOP)) {
				return stateFrom.getValue(TYPE).equals(SlabType.TOP) || stateFrom.getValue(TYPE).equals(SlabType.DOUBLE);
			} else if (state.getValue(TYPE).equals(SlabType.BOTTOM)) {
				return stateFrom.getValue(TYPE).equals(SlabType.BOTTOM) || stateFrom.getValue(TYPE).equals(SlabType.DOUBLE);
			} else if (state.getValue(TYPE).equals(SlabType.DOUBLE)) {
				return super.skipRendering(state, stateFrom, direction);
			} else {
				return false;
			}

		} else {
			return super.skipRendering(state, stateFrom, direction);
		}

	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return true;
	}

}
