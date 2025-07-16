package net.ludocrypt.corners.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RailingBlock extends FenceBlock {

	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 0, 8);
	protected static final VoxelShape[] LAYERS_TO_OUTLINE = new VoxelShape[] { Shapes.empty(),
		Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
		Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };
	protected static final VoxelShape[] LAYERS_TO_COLLISION = new VoxelShape[] { Shapes.empty(), Shapes.empty(),
		Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };

	public RailingBlock(Properties settings) {
		super(settings);
		this.shapeByIndex = this.makeShapes(2.0F, 2.0F, 12.0F, 0.0f, 12.0F);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LAYERS);
	}

	@Override
	protected boolean isSameFence(BlockState state) {
		return state.getBlock() instanceof RailingBlock;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes
			.or(this.shapeByIndex[this.getAABBIndex(state)],
				Block.box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D),
				Block.box(6.0D, 12.0D, 6.0D, 10.0D, 13.0D, 10.0D),
				Block.box(7.0D, 13.0D, 7.0D, 9.0D, 14.0D, 9.0D), LAYERS_TO_OUTLINE[state.getValue(LAYERS)]);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes
			.or(this.shapeByIndex[this.getAABBIndex(state)],
				Block.box(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D), LAYERS_TO_COLLISION[state.getValue(LAYERS)]);
	}

	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (itemStack.getItem() == Items.SNOW) {

			if (state.getValue(LAYERS) < 8 && Blocks.SNOW.canSurvive(Blocks.SNOW.defaultBlockState(), world, pos)) {
				world.setBlockAndUpdate(pos, state.cycle(LAYERS));
				world.playSound(null, pos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}

		}

		return super.use(state, world, pos, player, hand, hit);
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		super.destroy(world, pos, state);

		if (state.getValue(LAYERS) > 0) {
			world
				.setBlock(pos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, state.getValue(LAYERS)),
					Block.UPDATE_ALL);
		}

	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState placementState = super.getStateForPlacement(ctx);
		BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());

		if (state.is(Blocks.SNOW)) {
			placementState = placementState.setValue(LAYERS, state.getValue(SnowLayerBlock.LAYERS));
		}

		return placementState;
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		BlockState defaultState = super.updateShape(state, direction, neighborState, world, pos, neighborPos);

		if (state.getValue(LAYERS) > 0 && !Blocks.SNOW.canSurvive(Blocks.SNOW.defaultBlockState(), world, pos)) {
			defaultState = defaultState.setValue(LAYERS, 0);
		}

		return defaultState;
	}

}
