package net.ludocrypt.corners.block;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class OrientableBlock extends Block {

	public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

	public OrientableBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction direction;
		Direction direction2;

		if (ctx.getClickedFace().getAxis() == Direction.Axis.Y) {
			direction = ctx.getHorizontalDirection().getOpposite();
			direction2 = Direction.UP;
		} else {
			direction = Direction.UP;
			direction2 = ctx.getHorizontalDirection().getOpposite();
		}

		FrontAndTop ore = FrontAndTop.fromFrontAndTop(direction, direction2);
		return this.defaultBlockState().setValue(ORIENTATION, ore);
	}

	public static Direction getFacing(BlockState state) {
		return ((FrontAndTop) state.getValue(ORIENTATION)).front();
	}

	public static Direction getRotation(BlockState state) {
		return ((FrontAndTop) state.getValue(ORIENTATION)).top();
	}

}
