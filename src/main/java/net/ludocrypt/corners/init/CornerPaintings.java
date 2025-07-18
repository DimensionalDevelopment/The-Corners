package net.ludocrypt.corners.init;

import java.util.function.BiFunction;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.ludocrypt.corners.util.DimensionalPaintingVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CornerPaintings {

	public static final BiFunction<LivingEntity, DimensionalPaintingEntity, Vec3> overworldPaintingTarget = (entity,
			painting) -> {

		if (entity instanceof ServerPlayer player) {
			BlockPos pos = player.getRespawnPosition();

			if (pos != null) {
				ServerLevel serverWorld = player.getServer().overworld();
				return Player
					.findRespawnPositionAndUseSpawnBlock(serverWorld, pos, player.getRespawnAngle(), player.isRespawnForced(), true)
					.orElse(Vec3.atCenterOf(player.getServer().overworld().getSharedSpawnPos()));
			}

		}

		return Vec3.atCenterOf(entity.getServer().overworld().getSharedSpawnPos());

	};
	public static final PaintingVariant OVERWORLD = get("overworld",
		DimensionalPaintingVariant.create(48, 48, Level.OVERWORLD, overworldPaintingTarget));
	public static final PaintingVariant OVERWORLD_THIN = get("overworld_thin",
		DimensionalPaintingVariant.create(16, 32, Level.OVERWORLD, overworldPaintingTarget));
	public static final PaintingVariant OVERWORLD_WIDE = get("overworld_wide",
		DimensionalPaintingVariant.create(64, 32, Level.OVERWORLD, overworldPaintingTarget));
	public static final PaintingVariant YEARNING_CANAL = get("yearning_canal",
		DimensionalPaintingVariant.create(48, 48, CornerWorlds.YEARNING_CANAL_KEY, new Vec3(5.5D, 1.0D, 5.5D)));
	public static final PaintingVariant COMMUNAL_CORRIDORS = get("communal_corridors",
		DimensionalPaintingVariant
			.create(32, 32, CornerWorlds.COMMUNAL_CORRIDORS_KEY,
				(player, painting) -> player
					.position()
					.subtract(new Vec3(player.getX() % 8.0D, player.getY(), player.getZ() % 8.0D))
					.add(2.0D, 2.0D, 2.0D)));
	public static final PaintingVariant HOARY_CROSSROADS = get("hoary_crossroads",
		DimensionalPaintingVariant
			.create(32, 48, CornerWorlds.HOARY_CROSSROADS_KEY,
				(player, painting) -> player
					.position()
					.subtract(new Vec3(player.getX() % 512.0D, player.getY(), player.getZ() % 512.0D))
					.add(256.0D, 263.0D, 0.0D)
					.add(4.0D, 0, 4.0D)));

	public static void init() {
	}

	public static <T extends PaintingVariant> T get(String id, T painting) {
		return Registry.register(BuiltInRegistries.PAINTING_VARIANT, TheCorners.id(id), painting);
	}

}
