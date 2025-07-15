package net.ludocrypt.corners.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class AdvancementHelper {

	public static void grantAdvancement(Player player, ResourceLocation id) {

		if (player instanceof ServerPlayer serverPlayerEntity) {
			Advancement advancement = serverPlayerEntity.server.getAdvancements().getAdvancement(id);
			AdvancementProgress progress = serverPlayerEntity.getAdvancements().getOrStartProgress(advancement);

			if (!progress.isDone()) {
				progress
					.getRemainingCriteria()
					.forEach((criteria) -> serverPlayerEntity.getAdvancements().award(advancement, criteria));
			}

		}

	}

}
