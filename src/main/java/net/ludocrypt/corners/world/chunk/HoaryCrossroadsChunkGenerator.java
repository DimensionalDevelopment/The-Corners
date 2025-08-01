package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.corners.world.maze.GrandMazeGenerator;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.CombineMaze;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMaze;
import net.ludocrypt.limlib.api.world.maze.DepthFirstMazeSolver;
import net.ludocrypt.limlib.api.world.maze.DilateMaze;
import net.ludocrypt.limlib.api.world.maze.MazeComponent;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.MazePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class HoaryCrossroadsChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<HoaryCrossroadsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
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
		})).apply(instance, instance.stable(HoaryCrossroadsChunkGenerator::new));
	});
	private GrandMazeGenerator mazeGenerator;
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDilation;
	private long mazeSeedModifier;

	public HoaryCrossroadsChunkGenerator(BiomeSource biomeSource, NbtGroup group, int mazeWidth, int mazeHeight,
			int mazeDilation, long mazeSeedModifier) {
		super(biomeSource, group);
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		this.mazeDilation = mazeDilation;
		this.mazeSeedModifier = mazeSeedModifier;
		this.mazeGenerator = new GrandMazeGenerator(this.mazeWidth, this.mazeHeight, this.mazeDilation,
			this.mazeSeedModifier);
	}

	public static NbtGroup createGroup() {
		return NbtGroup.Builder
			.create(TheCorners.id(CornerWorlds.HOARY_CROSSROADS))
			.with("hoary_crossroads_f_decorated", 1, 7)
			.with("hoary_crossroads_f_bottom")
			.with("hoary_crossroads_f_rare")
			.with("hoary_crossroads_f")
			.with("hoary_crossroads_i_decorated", 1, 9)
			.with("hoary_crossroads_i_bottom")
			.with("hoary_crossroads_i_rare")
			.with("hoary_crossroads_i")
			.with("hoary_crossroads_l_decorated", 1, 9)
			.with("hoary_crossroads_l_bottom")
			.with("hoary_crossroads_l_rare")
			.with("hoary_crossroads_l")
			.with("hoary_crossroads_nub_decorated", 1, 6)
			.with("hoary_crossroads_nub_bottom")
			.with("hoary_crossroads_nub_rare")
			.with("hoary_crossroads_nub")
			.with("hoary_crossroads_t_decorated", 1, 7)
			.with("hoary_crossroads_t_bottom")
			.with("hoary_crossroads_t_rare", 1, 1)
			.with("hoary_crossroads_t")
			.with("hoary_crossroads_obelisk", 1, 4)
			.build();
	}

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<ChunkAccess> populateNoise(WorldGenRegion region, ChunkStatus targetStatus, Executor executor,
			ServerLevel world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager,
			ThreadedLevelLightEngine lightingProvider,
			Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> fullChunkConverter, List<ChunkAccess> chunks,
			ChunkAccess chunk) {
		BlockPos startPos = chunk.getPos().getWorldPosition();
		this.mazeGenerator
			.generateMaze(new Vec2i(startPos.getX(), startPos.getZ()), region, this::newMaze, this::decorateCell);
		return CompletableFuture.completedFuture(chunk);
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
	public MazeComponent newMaze(WorldGenRegion region, Vec2i mazePos, int width, int height, RandomSource random) {
		// Find the position of the grandMaze that contains the current maze
		BlockPos grandMazePos = new BlockPos(
			mazePos.getX() - Math
				.floorMod(mazePos.getX(), (mazeGenerator.width * mazeGenerator.width * mazeGenerator.thicknessX)),
			0, mazePos.getY() - Math
				.floorMod(mazePos.getY(), (mazeGenerator.height * mazeGenerator.height * mazeGenerator.thicknessY)));
		// Check if the grandMaze was already generated, if not generate it
		MazeComponent grandMaze;

		if (mazeGenerator.grandMazeMap.containsKey(grandMazePos)) {
			grandMaze = mazeGenerator.grandMazeMap.get(grandMazePos);
		} else {
			grandMaze = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation,
				mazeGenerator.height / mazeGenerator.dilation,
				RandomSource
					.create(
						LimlibHelper.blockSeed(grandMazePos.getX(), mazeGenerator.seedModifier, grandMazePos.getZ())));
			grandMaze.generateMaze();
			mazeGenerator.grandMazeMap.put(grandMazePos, grandMaze);
		}

		// Get the cell of the grandMaze that corresponds to the current maze
		CellState originCell = grandMaze
			.cellState(
				(((mazePos.getX() - grandMazePos
					.getX()) / mazeGenerator.thicknessX) / mazeGenerator.width) / mazeGenerator.dilation,
				(((mazePos.getY() - grandMazePos.getZ()) / mazeGenerator.thicknessY) / height) / mazeGenerator.dilation);
		Vec2i start = null;
		List<Vec2i> endings = Lists.newArrayList();

		// Check if the origin cell has an opening in the south or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.goesDown() || originCell.getPosition().getX() == 0) {

			if (start == null) {
				start = new Vec2i(0, (mazeGenerator.height / mazeGenerator.dilation) / 2);
			}

		}

		// Check if the origin cell has an opening in the west or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set.
		if (originCell.goesLeft() || originCell.getPosition().getY() == 0) {

			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0);
			} else {
				endings.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2, 0));
			}

		}

		// Check if the origin cell has an opening in the north or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell.goesUp() || originCell.getPosition().getX() == (mazeGenerator.width / mazeGenerator.dilation) - 1) {

			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1,
					(mazeGenerator.height / mazeGenerator.dilation) / 2);
			} else {
				endings
					.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) - 1,
						(mazeGenerator.height / mazeGenerator.dilation) / 2));
			}

		}

		// Check if the origin cell has an opening in the east or it's on the edge of
		// the grandMaze, if so set the starting point to the middle of that side, if it
		// has not been set. Else add an ending point to the middle of that side.
		if (originCell
			.goesRight() || originCell.getPosition().getY() == (mazeGenerator.height / mazeGenerator.dilation) - 1) {

			if (start == null) {
				start = new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2,
					(mazeGenerator.height / mazeGenerator.dilation) - 1);
			} else {
				endings
					.add(new Vec2i((mazeGenerator.width / mazeGenerator.dilation) / 2,
						(mazeGenerator.height / mazeGenerator.dilation) - 1));
			}

		}

		// If the origin cell is a dead end, add a random ending point in the middle of
		// the maze. This ensures there is always somewhere to go in a dead end.
		if (endings.isEmpty()) {
			endings
				.add(new Vec2i(random.nextInt((mazeGenerator.width / mazeGenerator.dilation) - 2) + 1,
					random.nextInt((mazeGenerator.height / mazeGenerator.dilation) - 2) + 1));
		}

		// Create a new maze.
		MazeComponent mazeToSolve = new DepthFirstMaze(mazeGenerator.width / mazeGenerator.dilation,
			mazeGenerator.height / mazeGenerator.dilation, random);
		mazeToSolve.generateMaze();
		// Create a maze solver and solve the maze using the starting point and ending
		// points.
		MazeComponent solvedMaze = new DepthFirstMazeSolver(mazeToSolve, random, start, endings.toArray(new Vec2i[0]));
		solvedMaze.generateMaze();
		// Create a scaled maze using the dilation.
		MazeComponent dilatedMaze = new DilateMaze(solvedMaze, mazeGenerator.dilation);
		dilatedMaze.generateMaze();
		Vec2i starting = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1,
			random.nextInt((dilatedMaze.height / 2) - 2) + 1);
		Vec2i ending = new Vec2i(random.nextInt((dilatedMaze.width / 2) - 2) + 1,
			random.nextInt((dilatedMaze.height / 2) - 2) + 1);
		// Make a new maze
		MazeComponent overlayMaze = new DepthFirstMaze(dilatedMaze.width / 2, dilatedMaze.height / 2, random);
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

	public void decorateCell(WorldGenRegion region, Vec2i cellPos, Vec2i mazePos, MazeComponent maze, CellState state,
			Vec2i thickness, RandomSource cellRandom) {
		BlockPos pos = cellPos.toBlock();
		RandomSource random = RandomSource
			.create(LimlibHelper
				.blockSeed(pos.getX(), LimlibHelper.blockSeed(mazePos.getY(), region.getSeed(), mazePos.getX()),
					pos.getZ()));
		Pair<MazePiece, Manipulation> mazeSegment = MazePiece.getFromCell(state, random);

		if (mazeSegment.getFirst() != MazePiece.E) {
			placeNbt(getPiece(mazeSegment.getFirst(), random), getPieceAsBottom(mazeSegment.getFirst(), random), region, pos,
				mazeSegment.getSecond());
		} else if (random.nextInt(67) == 0) {
			BlockPos offset = pos.offset(random.nextInt(7), 0, random.nextInt(7));
			this
				.generateNbt(region, offset.offset(0, 264, 0), nbtGroup.pick("hoary_crossroads_obelisk", random),
					Manipulation.random(random));

			for (int i = 0; i < 264; i++) {
				region.setBlock(offset.offset(0, i, 0), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);
				region.setBlock(offset.offset(1, i, 0), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);
				region.setBlock(offset.offset(1, i, 1), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);
				region.setBlock(offset.offset(0, i, 1), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);
			}

		}

	}

	private void placeNbt(ResourceLocation nbt, ResourceLocation bottomNbt, WorldGenRegion region, BlockPos basePos,
			Manipulation manipulation) {
		this.generateNbt(region, basePos.above(256), nbt, manipulation);

		for (int i = 0; i < 256; i++) {
			this.generateNbt(region, basePos.above(i), bottomNbt, manipulation);
		}

	}

	public ResourceLocation getPiece(MazePiece piece, RandomSource random) {
		String group = switch (piece) {
			case F -> "hoary_crossroads_f";
			case I -> "hoary_crossroads_i";
			case L -> "hoary_crossroads_l";
			case T -> "hoary_crossroads_t";
			default -> "hoary_crossroads_nub";
		};

		if ((random.nextInt(8) == 0)) {

			if ((random.nextInt(13) == 0)) {
				group += "_rare";
			} else {
				group += "_decorated";
			}

		}

		return nbtGroup.pick(group, random);
	}

	public ResourceLocation getPieceAsBottom(MazePiece piece, RandomSource random) {

		switch (piece) {
			case F:
				return nbtGroup.pick("hoary_crossroads_f_bottom", random);
			case I:
				return nbtGroup.pick("hoary_crossroads_i_bottom", random);
			case L:
				return nbtGroup.pick("hoary_crossroads_l_bottom", random);
			case N:
				return nbtGroup.pick("hoary_crossroads_nub_bottom", random);
			case T:
				return nbtGroup.pick("hoary_crossroads_t_bottom", random);
			default:
				return nbtGroup.pick("hoary_crossroads_nub_bottom", random);
		}

	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

	@Override
	protected ResourceLocation getContainerLootTable(RandomizableContainerBlockEntity container) {
		return BuiltInLootTables.SHIPWRECK_SUPPLY;
	}

	@Override
	public int getGenDepth() {
		return 512;
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
