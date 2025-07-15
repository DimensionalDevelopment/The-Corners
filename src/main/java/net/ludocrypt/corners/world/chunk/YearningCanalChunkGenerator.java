package net.ludocrypt.corners.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerWorlds;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class YearningCanalChunkGenerator extends AbstractNbtChunkGenerator {

	public static final Codec<YearningCanalChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), NbtGroup.CODEC.fieldOf("group").stable().forGetter((chunkGenerator) -> {
			return chunkGenerator.nbtGroup;
		})).apply(instance, instance.stable(YearningCanalChunkGenerator::new));
	});

	public YearningCanalChunkGenerator(BiomeSource biomeSource, NbtGroup group) {
		super(biomeSource, group);
	}

	public static NbtGroup createGroup() {
		return NbtGroup.Builder
			.create(TheCorners.id(CornerWorlds.YEARNING_CANAL))
			.with("yearning_canal", 1, 14)
			.with("yearning_canal_bottom")
			.with("yearning_canal_hallway")
			.with("yearning_canal_hallway_connected")
			.with("yearning_canal_hallway_decorated", 1, 12)
			.with("yearning_canal_top")
			.with("yearning_canal_with_hallway")
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
		int max = Math.floorDiv(chunk.getMaxBuildHeight(), 54);

		for (int xi = 0; xi < 16; xi++) {

			for (int zi = 0; zi < 16; zi++) {
				BlockPos pos = chunk.getPos().getWorldPosition().offset(xi, 0, zi);

				for (int yi = 0; yi < max; yi++) {
					BlockPos towerPos = pos.offset(0, yi * 54, 0);
					RandomSource pieceRandom = RandomSource
						.create(region.getSeed() + LimlibHelper.blockSeed(yi * 2, yi * 3, yi));
					boolean hallwaySpawnsAtHeight = (pieceRandom.nextDouble() < 0.875D && pieceRandom
						.nextBoolean()) && (yi != 0 && yi != max - 1);
					Direction dir = Direction.from2DDataValue(pieceRandom.nextInt(4));
					Rotation rotation = dir.equals(Direction.NORTH) ? Rotation.COUNTERCLOCKWISE_90
							: dir.equals(Direction.EAST) ? Rotation.NONE
									: dir.equals(Direction.SOUTH) ? Rotation.CLOCKWISE_90 : Rotation.CLOCKWISE_180;
					BlockPos offsetPos = towerPos
						.offset(
							(dir.equals(Direction.NORTH) ? 6
									: dir.equals(Direction.EAST) ? 12 : dir.equals(Direction.SOUTH) ? 6 : -10),
							(dir.equals(Direction.NORTH) ? 13
									: dir.equals(Direction.EAST) ? 23 : dir.equals(Direction.SOUTH) ? 22 : 15),
							(dir.equals(Direction.NORTH) ? -10
									: dir.equals(Direction.EAST) ? 6 : dir.equals(Direction.SOUTH) ? 12 : 6));

					if (pos.getX() % 19 == 0 && pos.getZ() % 19 == 0) {
						RandomSource chunkRandom = RandomSource
							.create(region.getSeed() + LimlibHelper.blockSeed(pos.getX(), pos.getZ(), 50));
						boolean tower = chunkRandom.nextDouble() < 0.01 && chunkRandom.nextDouble() < 0.4;

						if ((tower && (towerPos.getX() != 0 && towerPos.getZ() != 0)) || (towerPos.getX() == 0 && towerPos
							.getZ() == 0)) {

							if (yi == 0) {
								generateNbt(region, towerPos, nbtGroup.pick("yearning_canal_bottom", pieceRandom));
								continue;
							} else if (yi == max - 1) {
								generateNbt(region, towerPos, nbtGroup.pick("yearning_canal_top", pieceRandom));
								continue;
							} else {

								if (hallwaySpawnsAtHeight && !tower) {
									generateNbt(region, towerPos, nbtGroup.pick("yearning_canal_with_hallway", pieceRandom));
									generateNbt(region, offsetPos,
										nbtGroup.pick("yearning_canal_hallway_connected", pieceRandom),
										Manipulation.of(rotation));
								} else {
									generateNbt(region, towerPos, nbtGroup.pick("yearning_canal", pieceRandom));
								}

							}

						}

					}

					if (pos.getX() % 16 == 0 && pos.getZ() % 16 == 0) {

						if (hallwaySpawnsAtHeight) {

							if ((dir.equals(Direction.NORTH) && towerPos.getX() == 0 && towerPos.getZ() <= 0) || (dir
								.equals(Direction.WEST) && towerPos.getZ() == 0 && towerPos.getX() <= 0) || (dir
									.equals(Direction.SOUTH) && towerPos.getX() == 0 && towerPos.getZ() >= 1) || (dir
										.equals(Direction.EAST) && towerPos.getZ() == 0 && towerPos.getX() >= 1)) {
								RandomSource hallRandom = RandomSource
									.create(region.getSeed() + LimlibHelper
										.blockSeed(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ()));

								if (hallRandom.nextInt(27) == 0) {
									generateNbt(region, offsetPos.offset(0, 4, 0),
										nbtGroup.pick("yearning_canal_hallway", hallRandom), Manipulation.of(rotation));
								} else {
									generateNbt(region, offsetPos.offset(0, 4, 0),
										nbtGroup.pick("yearning_canal_hallway_decorated", hallRandom),
										Manipulation.of(rotation));
								}

							}

						}

					}

				}

			}

		}

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getPlacementRadius() {
		return 2;
	}

	@Override
	public int getGenDepth() {
		return 2032;
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
