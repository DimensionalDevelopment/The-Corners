package net.ludocrypt.corners.world.feature;

import net.ludocrypt.corners.init.CornerBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class GaiaSaplingGenerator extends AbstractMegaTreeGrower {

	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean bees) {
		return null;
	}

	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource random) {
		return CornerBiomes.CONFIGURED_SAPLING_GAIA_TREE_FEATURE;
	}

	public boolean generateRadio(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state,
			RandomSource random) {
		Holder<ConfiguredFeature<?, ?>> holder = world
			.registryAccess()
			.registryOrThrow(Registries.CONFIGURED_FEATURE)
			.getHolder(CornerBiomes.CONFIGURED_GAIA_TREE_FEATURE)
			.orElse(null);

		if (holder == null) {
			return false;
		} else {
			return holder.value().place(world, chunkGenerator, random, pos);
		}

	}

}
