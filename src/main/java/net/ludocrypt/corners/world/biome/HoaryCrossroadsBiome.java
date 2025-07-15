package net.ludocrypt.corners.world.biome;

import net.ludocrypt.corners.init.CornerSoundEvents;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class HoaryCrossroadsBiome {

	public static Biome create(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
		Biome.BiomeBuilder biome = new Biome.BiomeBuilder();

		MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
		BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(features, carvers);

		BiomeSpecialEffects.Builder biomeEffects = new BiomeSpecialEffects.Builder();
		biomeEffects.skyColor(16777215);
		biomeEffects.waterColor(9681663);
		biomeEffects.waterFogColor(7243242);
		biomeEffects.fogColor(13224908);
		biomeEffects.grassColorOverride(6796479);
		biomeEffects.ambientLoopSound(CornerSoundEvents.BIOME_LOOP_HOARY_CROSSROADS);

		BiomeSpecialEffects effects = biomeEffects.build();

		biome.mobSpawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.specialEffects(effects);
		biome.hasPrecipitation(true);
		biome.temperature(-1.0F);
		biome.downfall(1.0F);

		return biome.build();
	}

}
