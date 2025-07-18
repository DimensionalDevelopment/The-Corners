package net.ludocrypt.corners.world.chunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.block.RadioBlock;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.corners.world.maze.GrandMazeGenerator;
import net.ludocrypt.corners.world.maze.StraightDepthFirstMaze;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.*;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class CommunalCorridorsChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<CommunalCorridorsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), NbtGroup.CODEC.fieldOf("group").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.nbtGroup;
		}), Codec.INT.fieldOf("maze_width").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeWidth;
		}), Codec.INT.fieldOf("maze_height").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeHeight;
		}), Codec.INT.fieldOf("maze_dilation").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeDilation;
		}), Codec.LONG.fieldOf("seed_modifier").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.mazeSeedModifier;
		})).apply(instance, instance.stable(CommunalCorridorsChunkGenerator::new));
	});
	private GrandMazeGenerator grandMazeGenerator;
//	private RectangularMazeGenerator<MazeComponent> level2mazeGenerator;
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDilation;
	private long mazeSeedModifier;

	public CommunalCorridorsChunkGenerator(BiomeSource biomeSource, NbtGroup group, int mazeWidth, int mazeHeight,
										   int mazeDilation, long mazeSeedModifier) {
		super(biomeSource, group);
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeDilation = mazeDilation;
		this.mazeSeedModifier = mazeSeedModifier;
		this.grandMazeGenerator = new GrandMazeGenerator(this.mazeWidth, this.mazeHeight, this.mazeDilation,
			this.mazeSeedModifier);
//		this.level2mazeGenerator = new RectangularMazeGenerator<MazeComponent>(this.mazeWidth * this.mazeDilation,
//			this.mazeHeight * this.mazeDilation, this.mazeDilation, true, this.mazeSeedModifier) {
//		};
	}

	public static NbtGroup createGroup() {
		NbtGroup.Builder builder = NbtGroup.Builder
			.create(TheCorners.id(CornerWorlds.COMMUNAL_CORRIDORS))
			.with("communal_corridors", 1, 14)
			.with("communal_corridors_decorated", 1, 22)
			.with("communal_corridors_decorated_big", 1, 3)
//			.with("communal_corridors_level_transition")
//			.with("communal_corridors_level2", 1, 12)
//			.with("communal_corridors_level2_decorated", 0, 11)
//			.with("communal_corridors_level2_decorated_big", 0, 3)
//			.with("communal_corridors_level2_decorated_big_dip")
//			.with("communal_corridors_level2_decorated_huge", 0, 1)
//			.with("communal_corridors_level2_tall", 1, 12)
			.with("communal_corridors_two_stories", 1, 5);

		for (int i = 0; i < 15; i++) {
			String dir = "nesw";
			boolean north = ((i & 8) != 0);
			boolean east = ((i & 4) != 0);
			boolean south = ((i & 2) != 0);
			boolean west = ((i & 1) != 0);

			if (north) {
				dir = dir.replace("n", "");
			}

			if (east) {
				dir = dir.replace("e", "");
			}

			if (south) {
				dir = dir.replace("s", "");
			}

			if (west) {
				dir = dir.replace("w", "");
			}

			builder
				.with("communal_corridors_maze/communal_corridors_" + dir, "communal_corridors_" + dir, 0, 9)
				.with("communal_corridors_maze/communal_corridors_" + dir + "_decorated",
					"communal_corridors_" + dir + "_decorated", 0, 9);
		}

		return builder.build();
	}

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	/**
	 * Create a new solved maze, with the starting and ending points based on a
	 * bigger maze called grandMaze.
	 * 
	 * @param mazePos the position of the maze
	 * @param width   width of the maze
	 * @param height  height of the maze
	 * @param random  generator
	 * @return MazeComponent
	 */
	public MazeComponent newGrandMaze(WorldGenRegion region, Vec2i mazePos, int width, int height, RandomSource random) {

		// Find the position of the grandMaze that contains the current maze
		BlockPos grandMazePos = new BlockPos(mazePos.getX() - Math
			.floorMod(mazePos.getX(), (grandMazeGenerator.width * grandMazeGenerator.width * grandMazeGenerator.thicknessX)),
			0,
			mazePos.getY() - Math
				.floorMod(mazePos.getY(),
					(grandMazeGenerator.height * grandMazeGenerator.height * grandMazeGenerator.thicknessY)));
		// Check if the grandMaze was already generated, if not generate it
		MazeComponent grandMaze;

		if (grandMazeGenerator.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = grandMazeGenerator.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(grandMazeGenerator.width / grandMazeGenerator.dilation,
				grandMazeGenerator.height / grandMazeGenerator.dilation,
				RandomSource
					.create(
						LimlibHelper.blockSeed(grandMazePos.getX(), grandMazeGenerator.seedModifier, grandMazePos.getZ())));
			grandMaze.generateMaze();
			grandMazeGenerator.grandMazeMap.put(grandMazePos, grandMaze);
		}

		// Get the cell of the grandMaze that corresponds to the current maze
		CellState originCell = grandMaze
			.cellState(
				(((mazePos.getX() - grandMazePos
					.getX()) / grandMazeGenerator.thicknessX) / grandMazeGenerator.width) / grandMazeGenerator.dilation,
				(((mazePos.getY() - grandMazePos
					.getZ()) / grandMazeGenerator.thicknessY) / height) / grandMazeGenerator.dilation);
		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		// Check if the origin cell has an opening in the south or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.goesDown() || originCell.getPosition().getX() == 0) {

			if (start == null) {
				start = new Vec2i(0, (grandMazeGenerator.height / grandMazeGenerator.dilation) / 2);
			}

		}

		// Check if the origin cell has an opening in the west or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.goesLeft() || originCell.getPosition().getY() == 0) {

			if (start == null) {
				start = new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2, 0);
			} else {
				endings.add(new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2, 0));
			}

		}

		// Check if the origin cell has an opening in the north or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell
			.goesUp() || originCell.getPosition().getX() == (grandMazeGenerator.width / grandMazeGenerator.dilation) - 1) {

			if (start == null) {
				start = new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) - 1,
					(grandMazeGenerator.height / grandMazeGenerator.dilation) / 2);
			} else {
				endings
					.add(new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) - 1,
						(grandMazeGenerator.height / grandMazeGenerator.dilation) / 2));
			}

		}

		// Check if the origin cell has an opening in the east or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell.goesRight() || originCell
			.getPosition()
			.getY() == (grandMazeGenerator.height / grandMazeGenerator.dilation) - 1) {

			if (start == null) {
				start = new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2,
					(grandMazeGenerator.height / grandMazeGenerator.dilation) - 1);
			} else {
				endings
					.add(new Vec2i((grandMazeGenerator.width / grandMazeGenerator.dilation) / 2,
						(grandMazeGenerator.height / grandMazeGenerator.dilation) - 1));
			}

		}

		// If the origin cell is a dead end, add a random ending point in the middle of
		// the maze. This ensures there is always somewhere to go in a dead end.
		if (endings.isEmpty()) {
			endings
				.add(new Vec2i(random.nextInt((grandMazeGenerator.width / grandMazeGenerator.dilation) - 2) + 1,
					random.nextInt((grandMazeGenerator.height / grandMazeGenerator.dilation) - 2) + 1));
		}

		// Create a new maze.
		MazeComponent mazeToSolve = new StraightDepthFirstMaze(grandMazeGenerator.width / grandMazeGenerator.dilation,
			grandMazeGenerator.height / grandMazeGenerator.dilation, random, 0.45D);
		mazeToSolve.generateMaze();
		// Create a maze solver and solve the maze using the starting point and ending
		// points.
		MazeComponent solvedMaze = new DepthFirstMazeSolver(mazeToSolve, random, start, endings.toArray(new Vec2i[0]));
		solvedMaze.generateMaze();
		// Create a scaled maze using the dilation.
		MazeComponent dilatedMaze = new DilateMaze(solvedMaze, grandMazeGenerator.dilation);
		dilatedMaze.generateMaze();
		Vec2i starting = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1,
			random.nextInt((dilatedMaze.height / 2) - 2) + 1);
		Vec2i ending = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1,
			random.nextInt((dilatedMaze.height / 2) - 2) + 1);
		// Make a new maze
		MazeComponent overlayMaze = new StraightDepthFirstMaze(dilatedMaze.width / 2, dilatedMaze.height / 2, random, 0.7D);
		overlayMaze.generateMaze();
		// Find a path along two random points
		MazeComponent solvedOverlay = new DepthFirstMazeSolver(overlayMaze, random, starting, ending);
		solvedOverlay.generateMaze();
		// Make it bigger
		MazeComponent dilatedOverlay = new DilateMaze(solvedOverlay, 2);
		dilatedOverlay.generateMaze();
		// Combine the two
		CombineMaze combinedMaze = new CombineMaze(dilatedMaze, dilatedOverlay);
		combinedMaze.generateMaze();
		return combinedMaze;
	}

	public MazeComponent newMaze(BlockPos mazePos, int width, int height, RandomSource random) {
		MazeComponent maze = new DepthFirstMaze(width, height, random);
		maze.generateMaze();
		return maze;
	}

	@Override
	public CompletableFuture<ChunkAccess> populateNoise(WorldGenRegion region, ChunkStatus targetStatus, Executor executor,
			ServerLevel world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager,
			ThreadedLevelLightEngine lightingProvider,
			Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> fullChunkConverter, List<ChunkAccess> chunks,
			ChunkAccess chunk) {
		this.grandMazeGenerator
			.generateMaze(new Vec2i(chunk.getPos().getWorldPosition()), region, this::newGrandMaze, this::decorateGrandCell);
//		this.level2mazeGenerator
//			.generateMaze(startPos, region.getSeed(), this::newMaze, (pos, mazePos, maze, cellState,
//					thickness) -> decorateCell(pos, mazePos, maze, cellState, thickness, region));
		return CompletableFuture.completedFuture(chunk);
	}

//	public void decorateCell(BlockPos pos, BlockPos mazePos, MazeComponent maze, CellState state, int thickness,
//			ChunkRegion region) {
//		RandomGenerator random = RandomGenerator
//			.createLegacy(LimlibHelper
//				.blockSeed(pos.getX(), LimlibHelper.blockSeed(mazePos.getZ(), region.getSeed(), mazePos.getX()),
//					pos.getZ()));
////		RandomGenerator chunkRandom = RandomGenerator
////				.createLegacy(region.getSeed() + LimlibHelper.blockSeed(pos.getX() - Math.floorMod(pos.getX(), 16), pos.getZ() - Math.floorMod(pos.getZ(), 16), -1337));
////		RandomGenerator bigRandom = RandomGenerator
////				.createLegacy(region.getSeed() + LimlibHelper.blockSeed(pos.getX() - Math.floorMod(pos.getX(), 32), pos.getZ() - Math.floorMod(pos.getZ(), 32), 69420));
////		RandomGenerator hugeRandom = RandomGenerator
////				.createLegacy(region.getSeed() + LimlibHelper.blockSeed(pos.getX() - Math.floorMod(pos.getX(), 64), pos.getZ() - Math.floorMod(pos.getZ(), 64), 1337));
////
////		if (hugeRandom.nextDouble() < 0.04522689D) {
////			boolean flip = hugeRandom.nextBoolean();
////			int xOffset = MathHelper.floor(((double) Math.floorMod(pos.getX(), 64)) / 16.0D) * 16;
////			int zOffset = MathHelper.floor(((double) Math.floorMod(pos.getZ(), 64)) / 16.0D) * 16;
////
////			if (Math.floorMod(pos.getX(), 16) == 0 && Math.floorMod(pos.getZ(), 16) == 0) {
////				generateNbt(region, new BlockPos(xOffset, 0, zOffset), pos.up(17), pos.up(17).add(16, 64, 16), nbtGroup.pick("communal_corridors_level2_decorated_huge", hugeRandom),
////						flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE, flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
////			}
////
////			return;
////		} else if (bigRandom.nextDouble() < 0.09888567D) {
////			boolean flip = bigRandom.nextBoolean();
////			int xOffset = MathHelper.floor(((double) Math.floorMod(pos.getX(), 32)) / 16.0D) * 16;
////			int zOffset = MathHelper.floor(((double) Math.floorMod(pos.getZ(), 32)) / 16.0D) * 16;
////
////			if (Math.floorMod(pos.getX(), 16) == 0 && Math.floorMod(pos.getZ(), 16) == 0) {
////				String group = nbtGroup.chooseGroup(bigRandom, "communal_corridors_level2_decorated_big", "communal_corridors_level2_decorated_big_dip");
////				BlockPos basePos = pos.up(17);
////
////				if (group.equals("communal_corridors_level2_decorated_big_dip")) {
////					basePos = pos.up(15);
////				}
////
////				generateNbt(region, new BlockPos(xOffset, 0, zOffset), basePos, basePos.add(16, 64, 16), nbtGroup.pick(group, bigRandom), flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE,
////						flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
////			}
////
////			return;
////		} else {
////
////			if (chunkRandom.nextDouble() < 0.17624375D) {
////				BlockPos chunkPos = new BlockPos(pos.getX() - Math.floorMod(pos.getX(), 16), pos.getY(), pos.getZ() - Math.floorMod(pos.getZ(), 16));
////
////				if (region.getBlockState(chunkPos).isAir()) {
////					region.setBlockState(chunkPos, Blocks.OAK_PLANKS.getDefaultState(), Block.FORCE_STATE);
////					return;
////				}
////
////				boolean flip = chunkRandom.nextBoolean();
////
////				if (Math.floorMod(pos.getX(), 16) == 0 && Math.floorMod(pos.getZ(), 16) == 0) {
////
////					if (chunkRandom.nextDouble() < 0.7624375D) {
////						generateNbt(region, pos.up(17), nbtGroup.pick("communal_corridors_level2_decorated", chunkRandom), flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE,
////								flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
////					} else {
////
////						if ((chunkRandom.nextDouble() < 0.31275D && chunkRandom.nextInt(8) == 0)) {
////							generateNbt(region, pos.up(17), nbtGroup.pick("communal_corridors_level2_tall", chunkRandom), flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE,
////									flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
////						} else {
////							generateNbt(region, pos.up(17), nbtGroup.pick("communal_corridors_level2", chunkRandom), flip ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE,
////									flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
////						}
////
////					}
////
////				}
////
////				return;
////			}
////
////		}
//		String dir = "nesw";
//
//		if (!state.isWest()) {
//			dir = dir.replace("n", "");
//		}
//
//		if (!state.isNorth()) {
//			dir = dir.replace("e", "");
//		}
//
//		if (!state.isEast()) {
//			dir = dir.replace("s", "");
//		}
//
//		if (!state.isSouth()) {
//			dir = dir.replace("w", "");
//		}
//
//		if (dir != "") {
//
//			if (random.nextDouble() > 0.67289445D) {
//				this
//					.generateNbt(region, pos.up(17),
//						nbtGroup.pick("communal_corridors_maze/communal_corridors_" + dir, random));
//			} else {
//				this
//					.generateNbt(region, pos.up(17),
//						nbtGroup.pick("communal_corridors_maze/communal_corridors_" + dir + "_decorated", random));
//			}
//
//		}
//
//	}

	public void decorateGrandCell(WorldGenRegion region, Vec2i cellPos2, Vec2i mazePos2, MazeComponent maze, CellState state,
			Vec2i thickness, RandomSource mazeRandom) {

		BlockPos pos = cellPos2.toBlock();
		BlockPos mazePos = mazePos2.toBlock();

		for (int x = 0; x < thickness.getX(); x++) {

			for (int z = 0; z < thickness.getY(); z++) {
				region.setBlock(pos.offset(x, 0, z), Blocks.OAK_PLANKS.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE, 0);
//				region.setBlockState(pos.add(x, 16, z), Blocks.OAK_PLANKS.getDefaultState(), Block.FORCE_STATE, 0);
			}

		}

		RandomSource random = RandomSource
			.create(LimlibHelper
				.blockSeed(pos.getX(), LimlibHelper.blockSeed(mazePos.getZ(), region.getSeed(), mazePos.getX()),
					pos.getZ()));
		String dir = "nesw";

		if (!state.goesLeft()) {
			dir = dir.replace("n", "");
		}

		if (!state.goesUp()) {
			dir = dir.replace("e", "");
		}

		if (!state.goesRight()) {
			dir = dir.replace("s", "");
		}

		if (!state.goesDown()) {
			dir = dir.replace("w", "");
		}

		if (dir != "") {

			if (random.nextDouble() > 0.67289445D) {
				this
					.generateNbt(region, pos.above(1),
						nbtGroup.pick("communal_corridors_maze/communal_corridors_" + dir, random));
			} else {
				this
					.generateNbt(region, pos.above(1),
						nbtGroup.pick("communal_corridors_maze/communal_corridors_" + dir + "_decorated", random));
			}

		} else {
			RandomSource fullChunkRandom = RandomSource
				.create(region.getSeed() + LimlibHelper
					.blockSeed(pos.getX() - Math.floorMod(pos.getX(), 16), pos.getZ() - Math.floorMod(pos.getZ(), 16),
						-69420));
			Manipulation manipulation = random.nextBoolean() ? Manipulation.NONE : Manipulation.TOP_LEFT_BOTTOM_RIGHT;
			Vec2i fullChunkPos = new Vec2i(state.getPosition().getX() - Math.floorMod(state.getPosition().getX(), 2),
				state.getPosition().getY() - Math.floorMod(state.getPosition().getY(), 2));
			boolean skipSecondFloor = false;
			boolean skipSmallRooms = false;
//			if (RandomGenerator.createLegacy(region.getSeed() + LimlibHelper.blockSeed(pos.getX() - Math.floorMod(pos.getX(), 64), pos.getZ() - Math.floorMod(pos.getZ(), 64), 1337))
//					.nextDouble() < 0.04522689D) {} else if (RandomGenerator
//							.createLegacy(region.getSeed() + LimlibHelper.blockSeed(pos.getX() - Math.floorMod(pos.getX(), 32), pos.getZ() - Math.floorMod(pos.getZ(), 32), 69420))
//							.nextDouble() < 0.09888567D) {} else {
//				RandomGenerator upperChunkRandom = RandomGenerator
//						.createLegacy(region.getSeed() + LimlibHelper.blockSeed(pos.getX() - Math.floorMod(pos.getX(), 16), pos.getZ() - Math.floorMod(pos.getZ(), 16), -1337));
//
//				if (upperChunkRandom.nextDouble() < 0.17624375D) {
//
//					if (!(maze.cellState(fullChunkPos).isWest() || maze.cellState(fullChunkPos).isNorth() || maze.cellState(fullChunkPos).isEast() || maze.cellState(fullChunkPos).isSouth())) {
//
//						if (upperChunkRandom.nextDouble() < 0.2) {
//							BlockPos chunkPos = new BlockPos(pos.getX() - Math.floorMod(pos.getX(), 16), pos.getY(), pos.getZ() - Math.floorMod(pos.getZ(), 16));
//							region.setBlockState(chunkPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
//
//							if (Math.floorMod(state.getPosition().getX(), 2) == 0 && Math.floorMod(state.getPosition().getY(), 2) == 0) {
//								generateNbt(region, pos.up(6), nbtGroup.pick("communal_corridors_level_transition", random), flip ? BlockRotation.COUNTERCLOCKWISE_90 : BlockRotation.NONE,
//										flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
//							}
//
//							skipSecondFloor = true;
//						}
//
//					}
//
//				}
//
//			}

			if (!skipSecondFloor) {

				for (int x = 0; x < thickness.getX(); x++) {

					for (int z = 0; z < thickness.getY(); z++) {

						for (int y = 0; y < 3; y++) {
							region
								.setBlock(pos.offset(x, 11 + y, z), CornerBlocks.DRYWALL.defaultBlockState(),
									Block.UPDATE_KNOWN_SHAPE, 0);
						}

					}

				}

			}

			if ((fullChunkRandom.nextDouble() < 0.31275D && fullChunkRandom.nextInt(8) == 0)) {

				if (!maze.cellState(fullChunkPos).goes()) {

					if (Math.floorMod(state.getPosition().getX(), 2) == 0 && Math
						.floorMod(state.getPosition().getY(), 2) == 0) {
						generateNbt(region, pos.above(), nbtGroup.pick("communal_corridors_decorated_big", random),
							manipulation);
					}

					skipSmallRooms = true;
				}

			}

			if (!skipSmallRooms) {

				if (random.nextDouble() < 0.2375625D) {
					generateNbt(region, pos.above(), nbtGroup.pick("communal_corridors", random), manipulation);
				} else {

					if (skipSecondFloor) {
						generateNbt(region, pos.above(), nbtGroup.pick("communal_corridors_decorated", random), manipulation);
						return;
					} else {
						String group = nbtGroup.chooseGroup(random, "communal_corridors_decorated", "communal_corridors_two_stories");
						generateNbt(region, pos.above(), nbtGroup.pick(group, random), manipulation);

						if (group.equals("communal_corridors_two_stories")) {
							return;
						}

					}

				}

			}

			if (!skipSecondFloor) {
				generateNbt(region, pos.above(6), nbtGroup.pick("communal_corridors_decorated", random), manipulation);
			}

		}

	}

	@Override
	public int getPlacementRadius() {
		return 2;
	}

	@Override
	protected ResourceLocation getContainerLootTable(RandomizableContainerBlockEntity container) {
		return container.getBlockState().is(Blocks.CHEST) ? BuiltInLootTables.WOODLAND_MANSION
				: BuiltInLootTables.SPAWN_BONUS_CHEST;
	}

	@Override
	protected void modifyStructure(WorldGenRegion region, BlockPos pos, BlockState state, Optional<CompoundTag> nbt) {
		super.modifyStructure(region, pos, state, nbt);

		if (state.is(CornerBlocks.WOODEN_RADIO)) {
			int i = RandomSource.create(region.getSeed() + LimlibHelper.blockSeed(pos)).nextInt(3);

			switch (i) {
				case 1:
					region
						.setBlock(pos,
							CornerBlocks.TUNED_RADIO.defaultBlockState().setValue(RadioBlock.FACING, state.getValue(RadioBlock.FACING)),
							Block.UPDATE_ALL, 1);
					break;
				case 2:
					region
						.setBlock(pos,
							CornerBlocks.BROKEN_RADIO
								.defaultBlockState()
								.setValue(RadioBlock.FACING, state.getValue(RadioBlock.FACING)),
							Block.UPDATE_ALL, 1);
					break;
				case 0:
				default:
					break;
			}

		} else if (state.is(Blocks.RED_STAINED_GLASS)) {
			RandomSource random = RandomSource.create(region.getSeed() + LimlibHelper.blockSeed(pos));

			if (random.nextDouble() < 0.3765568D) {
				region.setBlock(pos, Blocks.COBWEB.defaultBlockState(), Block.UPDATE_ALL, 1);
			} else {
				region.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL, 1);
			}

		} else if (state.is(Blocks.CHISELED_BOOKSHELF)) {

			double scale = 10.0D;

			NormalNoise noise = NormalNoise
				.create(RandomSource.create(region.getSeed() + 5), 1, 0.2, 0.6, 0.7);

			if (noise.getValue((pos.getX()) / scale, (pos.getZ()) / scale, (pos.getY()) / scale) > 0) {
				BlockState deepState = RadioBlock.of(state, CornerBlocks.DEEP_BOOKSHELF);
				region.setBlock(pos, deepState, Block.UPDATE_ALL, 0);

				if (nbt.isPresent()) {
					BlockEntity blockEntity = region.getBlockEntity(pos);

					if (blockEntity != null) {
						blockEntity.setBlockState(deepState);
					}

				}

			}

		}

	}

	@Override
	public int getGenDepth() {
		return 128;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {
	}

}
