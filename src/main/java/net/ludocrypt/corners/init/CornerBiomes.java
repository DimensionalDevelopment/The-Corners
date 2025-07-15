package net.ludocrypt.corners.init;

import com.mojang.serialization.Codec;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.corners.world.chunk.HoaryCrossroadsChunkGenerator;
import net.ludocrypt.corners.world.chunk.YearningCanalChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

public class CornerBiomes {

	public static final ResourceKey<Biome> YEARNING_CANAL_BIOME = ResourceKey
		.create(Registries.BIOME, TheCorners.id(CornerWorlds.YEARNING_CANAL));
	public static final ResourceKey<Biome> COMMUNAL_CORRIDORS_BIOME = ResourceKey
		.create(Registries.BIOME, TheCorners.id(CornerWorlds.COMMUNAL_CORRIDORS));
	public static final ResourceKey<Biome> HOARY_CROSSROADS_BIOME = ResourceKey
		.create(Registries.BIOME, TheCorners.id(CornerWorlds.HOARY_CROSSROADS));
	public static final ResourceKey<Feature<?>> GAIA_TREE_FEATURE = ResourceKey
		.create(Registries.FEATURE, TheCorners.id("gaia_tree"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_GAIA_TREE_FEATURE = ResourceKey
		.create(Registries.CONFIGURED_FEATURE, TheCorners.id("gaia_tree"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_SAPLING_GAIA_TREE_FEATURE = ResourceKey
		.create(Registries.CONFIGURED_FEATURE, TheCorners.id("gaia_sapling"));

	public static void init() {
		get("yearning_canal_chunk_generator", YearningCanalChunkGenerator.CODEC);
		get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);
		get("hoary_crossroads_chunk_generator", HoaryCrossroadsChunkGenerator.CODEC);
	}

	public static <C extends ChunkGenerator, D extends Codec<C>> D get(String id, D chunkGeneratorCodec) {
		return Registry.register(BuiltInRegistries.CHUNK_GENERATOR, TheCorners.id(id), chunkGeneratorCodec);
	}

}
