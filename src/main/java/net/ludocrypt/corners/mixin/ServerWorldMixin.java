package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.corners.init.CornerWorlds;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ServerLevelData;

@Mixin(ServerLevel.class)
public class ServerWorldMixin {

	@Shadow
	@Final
	private ServerLevelData serverLevelData;

	@Inject(method = "advanceWeatherCycle", at = @At("HEAD"))
	private void corners$tickWeather(CallbackInfo ci) {

		ServerLevel world = ((ServerLevel) (Object) this);

		if (world.dimension().equals(CornerWorlds.HOARY_CROSSROADS_KEY)) {

			this.serverLevelData.setRainTime(0);
			this.serverLevelData.setRaining(true);
			this.serverLevelData.setThunderTime(0);
			this.serverLevelData.setThundering(false);

			world.setRainLevel(2.0F);

			if (!world.isRaining()) {
				world
					.getServer()
					.getPlayerList()
					.broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 2.0F),
						world.dimension());
				world
					.getServer()
					.getPlayerList()
					.broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F),
						world.dimension());
			}

		} else if (world.dimension().location().getNamespace().equals("corners")) {

			this.serverLevelData.setRainTime(0);
			this.serverLevelData.setRaining(false);
			this.serverLevelData.setThunderTime(0);
			this.serverLevelData.setThundering(false);

			world.setRainLevel(0.0F);

			if (world.isRaining()) {
				world
					.getServer()
					.getPlayerList()
					.broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 0.0F),
						((ServerLevel) (Object) this).dimension());
				world
					.getServer()
					.getPlayerList()
					.broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F),
						world.dimension());
			}

		}

	}

}
