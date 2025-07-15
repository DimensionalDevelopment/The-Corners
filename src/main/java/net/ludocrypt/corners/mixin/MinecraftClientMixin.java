package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.platform.Window;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.init.CornerWorlds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

	@Shadow
	public LocalPlayer player;
	@Shadow
	public ClientLevel world;
	@Final
	@Shadow
	private Window window;

	@Inject(method = "getMusic", at = @At("HEAD"), cancellable = true)
	private void corners$getMusic(CallbackInfoReturnable<Music> ci) {

		if (this.player != null) {

			if (world.dimension().equals(CornerWorlds.COMMUNAL_CORRIDORS_KEY)) {

				if (CornerConfig.get().christmas.isChristmas()) {
					ci
						.setReturnValue(
							new Music(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS_CHRISTMAS, 3000, 8000, true));
				} else {
					ci.setReturnValue(new Music(CornerSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true));
				}

			}

		}

	}

}
