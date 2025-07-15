package net.ludocrypt.corners.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Lists;

import net.ludocrypt.corners.access.MusicTrackerAccess;
import net.ludocrypt.corners.config.CornerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

@Mixin(MusicManager.class)
public class MusicTrackerMixin implements MusicTrackerAccess {

	@Shadow
	@Final
	private Minecraft client;
	@Unique
	private List<BlockPos> radioPositions = Lists.newArrayList();

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1))
	private int corners$tick$preventMusic(int time, int max) {
		int in = Math.min(time, max);

		if (CornerConfig.get().delayMusicWithRadio && !this.getRadioPositions().isEmpty() && !this
			.getRadioPositions()
			.stream()
			.filter((pos) -> client.player != null && client.player.distanceToSqr(Vec3.atCenterOf(pos)) < Math
				.pow(24.0D, 2.0D))
			.toList()
			.isEmpty()) {

			if (client.level == null) {
				this.getRadioPositions().clear();
			}

			return in + 1;
		}

		return in;
	}

	@Override
	public List<BlockPos> getRadioPositions() {
		return radioPositions;
	}

}
