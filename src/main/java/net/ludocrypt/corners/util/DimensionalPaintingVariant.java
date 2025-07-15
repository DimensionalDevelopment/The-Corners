package net.ludocrypt.corners.util;

import java.util.function.BiFunction;

import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

public class DimensionalPaintingVariant extends PaintingVariant {

	public final ResourceKey<Level> radioRedirect;
	public final BiFunction<LivingEntity, DimensionalPaintingEntity, ResourceKey<Level>> dimension;
	public final BiFunction<LivingEntity, DimensionalPaintingEntity, PortalInfo> teleportTarget;

	public DimensionalPaintingVariant(int width, int height, ResourceKey<Level> radioRedirect,
			BiFunction<LivingEntity, DimensionalPaintingEntity, ResourceKey<Level>> dimension,
			BiFunction<LivingEntity, DimensionalPaintingEntity, PortalInfo> teleportTarget) {
		super(width, height);
		this.radioRedirect = radioRedirect;
		this.dimension = dimension;
		this.teleportTarget = teleportTarget;
	}

	public DimensionalPaintingVariant(int width, int height, ResourceKey<Level> dimension,
			BiFunction<LivingEntity, DimensionalPaintingEntity, PortalInfo> teleportTarget) {
		this(width, height, dimension, (player, painting) -> dimension, teleportTarget);
	}

	public DimensionalPaintingVariant(int width, int height, ResourceKey<Level> dimension, PortalInfo teleport) {
		this(width, height, dimension, (player, painting) -> dimension, (player, painting) -> teleport);
	}

	public static DimensionalPaintingVariant create(int width, int height, ResourceKey<Level> dimension,
			BiFunction<LivingEntity, DimensionalPaintingEntity, Vec3> teleportTarget) {
		return new DimensionalPaintingVariant(width, height, dimension, (player, painting) -> dimension,
			(player, painting) -> new PortalInfo(teleportTarget.apply(player, painting), player.getDeltaMovement(),
				player.getYRot(), player.getXRot()));
	}

	public static DimensionalPaintingVariant create(int width, int height, ResourceKey<Level> dimension, Vec3 dest) {
		return new DimensionalPaintingVariant(width, height, dimension, (player, painting) -> dimension,
			(player, painting) -> new PortalInfo(dest, player.getDeltaMovement(), player.getYRot(), player.getXRot()));
	}

}
