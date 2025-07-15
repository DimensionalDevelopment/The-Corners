package net.ludocrypt.corners.packet;

import java.util.Comparator;
import java.util.List;

import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import net.ludocrypt.corners.access.MusicTrackerAccess;
import net.ludocrypt.corners.client.sound.LoopingPositionedSoundInstance;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerRadioRegistry;
import net.ludocrypt.corners.mixin.SoundManagerAccessor;
import net.ludocrypt.corners.util.DimensionalPaintingVariant;
import net.ludocrypt.corners.util.RadioSoundTable;
import net.ludocrypt.limlib.impl.access.SoundSystemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ServerToClientPackets {

	public static void manageServerToClientPackets() {
		ClientPlayNetworking
			.registerGlobalReceiver(ClientToServerPackets.PLAY_RADIO, (client, handler, buf, responseSender) -> {
				BlockPos pos = buf.readBlockPos();
				boolean start = buf.readBoolean();
				client.execute(() -> {
					RadioSoundTable id = CornerRadioRegistry.getCurrent(client);
					List<Painting> closestPaintings = client.level
						.getEntitiesOfClass(Painting.class, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)).inflate(16.0D),
							(entity) -> entity.getVariant().value() instanceof DimensionalPaintingVariant)
						.stream()
						.sorted(Comparator.comparing((entity) -> entity.distanceToSqr(Vec3.atLowerCornerOf(pos))))
						.toList();

					if (!closestPaintings.isEmpty()) {
						id = CornerRadioRegistry
							.getCurrent(
								((DimensionalPaintingVariant) closestPaintings.get(0).getVariant().value()).radioRedirect);
					}

					SoundSystemAccess
						.get(((SoundManagerAccessor) client.getSoundManager()).getSoundSystem())
						.stopSoundsAtPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, null,
							SoundSource.RECORDS);
					((MusicTrackerAccess) (client.getMusicManager())).getRadioPositions().remove(pos);

					if (start) {
						((MusicTrackerAccess) (client.getMusicManager())).getRadioPositions().add(pos);
						SoundEvent soundEvent = id.getStaticSound().value();

						if (client.level.getBlockState(pos).is(CornerBlocks.WOODEN_RADIO)) {
							soundEvent = id.getRadioSound().value();
						} else if (client.level.getBlockState(pos).is(CornerBlocks.TUNED_RADIO)) {
							soundEvent = id.getMusicSound().value();
						}

						LoopingPositionedSoundInstance
							.play(client.level, pos, soundEvent, SoundSource.RECORDS, 1.0F, 1.0F,
								RandomSource.create(), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
					}

				});
			});
	}

}
