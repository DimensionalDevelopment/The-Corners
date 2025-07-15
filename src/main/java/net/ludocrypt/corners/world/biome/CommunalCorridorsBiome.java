package net.ludocrypt.corners.world.biome;

import net.ludocrypt.corners.init.CornerSoundEvents;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class CommunalCorridorsBiome {

	public static Biome create(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
		Biome.BiomeBuilder biome = new Biome.BiomeBuilder();

		MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
		BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(features, carvers);

		BiomeSpecialEffects.Builder biomeEffects = new BiomeSpecialEffects.Builder();
		biomeEffects.skyColor(13548960);
		biomeEffects.waterColor(13548960);
		biomeEffects.waterFogColor(13548960);
		biomeEffects.fogColor(13548960);
		biomeEffects.grassColorOverride(13818488);
		biomeEffects.ambientLoopSound(CornerSoundEvents.BIOME_LOOP_COMMUNAL_CORRIDORS);

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
