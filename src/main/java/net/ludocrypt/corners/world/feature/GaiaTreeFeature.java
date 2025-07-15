package net.ludocrypt.corners.world.feature;

import com.mojang.serialization.Codec;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.init.CornerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GaiaTreeFeature extends Feature<NoneFeatureConfiguration> {

	public GaiaTreeFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		RandomSource random = context.random();
		WorldGenLevel world = context.level();
		BlockPos pos = context.origin().immutable();
		BlockState stump = CornerBlocks.STRIPPED_GAIA_LOG.defaultBlockState();
		BlockState leaf = CornerBlocks.GAIA_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
		trySetState(world, pos.above(), stump);
		trySetState(world, pos.below(), stump);
		trySetState(world, pos.above().above(), leaf);
		trySetState(world, pos.above().north(), leaf);
		trySetState(world, pos.above().east(), leaf);
		trySetState(world, pos.above().south(), leaf);
		trySetState(world, pos.above().west(), leaf);
		trySetState(world, pos.below().north(), stump);
		trySetState(world, pos.below().east(), stump);
		trySetState(world, pos.below().south(), stump);
		trySetState(world, pos.below().west(), stump);
		int range = random.nextInt(3) + 4;

		for (int i = -range; i <= range; i++) {

			for (int j = -range; j <= range; j++) {

				for (int k = -range; k <= range; k++) {
					BlockPos op = pos.offset(i, j, k);

					if (!op.equals(pos.relative(world.getBlockState(pos).getValue(RadioBlock.FACING)))) {

						if (world.getBlockState(op).is(Blocks.VINE) || world.isEmptyBlock(op)) {

							for (Direction dir : Direction.values()) {

								if (!dir.equals(Direction.DOWN)) {

									if (world
										.getBlockState(op.relative(dir))
										.isFaceSturdy(world, op.relative(dir), dir.getOpposite())) {

										if (random.nextDouble() > op.distSqr(pos) / (double) range / 2.0) {
											BlockState defaultState = Blocks.VINE.defaultBlockState();

											if (world.getBlockState(op).is(Blocks.VINE)) {
												defaultState = world.getBlockState(op);
											}

											world
												.setBlock(op, defaultState.setValue(VineBlock.getPropertyForFace(dir), true),
													Block.UPDATE_ALL);
										}

									}

								}

							}

						}

					}

				}

			}

		}

		return true;
	}

	public void trySetState(WorldGenLevel world, BlockPos pos, BlockState state) {
		BlockState from = world.getBlockState(pos);
		boolean wooden = from.is(TagKey.create(Registries.BLOCK, TheCorners.id("gaia_replaceable")));

		if (wooden || !from.isCollisionShapeFullBlock(world, pos) || from.isAir()) {

			if (!from.isCollisionShapeFullBlock(world, pos) && !(wooden) && !from.is(Blocks.VINE) && !from.isAir()) {
				world.destroyBlock(pos, true);
			}

			world.setBlock(pos, state, Block.UPDATE_ALL);
		}

	}

}
