package net.ludocrypt.corners.entity;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.mixin.AbstractDecorationEntityAccessor;
import net.ludocrypt.corners.mixin.PaintingEntityAccessor;
import net.ludocrypt.corners.util.DimensionalPaintingVariant;
import net.ludocrypt.limlib.api.LimlibTravelling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;

public class DimensionalPaintingEntity extends Painting {

	public DimensionalPaintingEntity(EntityType<? extends DimensionalPaintingEntity> type, Level world) {
		super(type, world);
	}

	public static DimensionalPaintingEntity create(Level world, BlockPos pos) {
		DimensionalPaintingEntity entity = new DimensionalPaintingEntity(CornerEntities.DIMENSIONAL_PAINTING_ENTITY, world);
		entity.pos = pos;
		return entity;
	}

	public static DimensionalPaintingEntity create(Level world, BlockPos pos, Direction direction, PaintingVariant variant) {

		if (variant instanceof DimensionalPaintingVariant) {
			DimensionalPaintingEntity entity = create(world, pos);
			((PaintingEntityAccessor) entity)
				.callSetVariant(
					BuiltInRegistries.PAINTING_VARIANT.getHolder(BuiltInRegistries.PAINTING_VARIANT.getResourceKey(variant).get()).get());
			entity.setDirection(direction);
			return entity;
		}

		TheCorners.LOGGER.warn("PaintingVariant {} is not DimensionalPaintingVariant, has nowhere to go!", variant);
		throw new UnsupportedOperationException();
	}

	public static Painting createRegular(Level world, BlockPos pos, Direction direction, PaintingVariant variant) {
		Painting entity = new Painting(EntityType.PAINTING, world);
		entity.setPos(pos.getX(), pos.getY(), pos.getZ());
		((PaintingEntityAccessor) entity)
			.callSetVariant(BuiltInRegistries.PAINTING_VARIANT.getHolder(BuiltInRegistries.PAINTING_VARIANT.getResourceKey(variant).get()).get());
		((AbstractDecorationEntityAccessor) entity).callSetDirection(direction);
		return entity;
	}

	public static Painting createFromMotive(Level world, BlockPos pos, Direction direction, PaintingVariant variant) {

		if (variant instanceof DimensionalPaintingVariant) {
			return create(world, pos, direction, variant);
		} else {
			return createRegular(world, pos, direction, variant);
		}

	}

	@Override
	public void playerTouch(Player player) {
		super.playerTouch(player);

		if (this.getVariant().value() instanceof DimensionalPaintingVariant variant) {
			AABB box = this.getBoundingBox().inflate(0.3D);

			if (box.contains(player.getEyePosition()) && box.contains(player.position()) && box
				.contains(player.position().add(0.0D, player.getBbHeight(), 0.0D))) {

				if (player.getDeltaMovement().length() > 0.05) {

					if (this.level() instanceof ServerLevel && player instanceof ServerPlayer spe) {
						ServerLevel world = player.getServer().getLevel(variant.dimension.apply(spe, this));
						PortalInfo teleportTarget = variant.teleportTarget.apply(spe, this);
						LimlibTravelling
							.travelTo(spe, world, teleportTarget, CornerSoundEvents.PAINTING_PORTAL_TRAVEL.value(), 0.25F,
								world.getRandom().nextFloat() * 0.4F + 0.8F);
					}

				}

			}

		}

	}

}
