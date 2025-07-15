package net.ludocrypt.corners.world.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class YearningCanalBiome {

	public static Biome create(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
		Biome.BiomeBuilder biome = new Biome.BiomeBuilder();

		MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
		BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(features, carvers);

		BiomeSpecialEffects.Builder biomeEffects = new BiomeSpecialEffects.Builder();
		biomeEffects.skyColor(16777215);
		biomeEffects.waterColor(16777215);
		biomeEffects.waterFogColor(16777215);
		biomeEffects.fogColor(16777215);
		biomeEffects.grassColorOverride(13818488);

		BiomeSpecialEffects effects = biomeEffects.build();

		biome.mobSpawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.specialEffects(effects);
		biome.hasPrecipitation(false);
		biome.temperature(0.8F);
		biome.downfall(0.0F);

		return biome.build();
	}

}
