package net.ludocrypt.corners.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.entity.CornerBoatEntity.CornerBoat;
import net.ludocrypt.corners.entity.DimensionalPaintingEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;

public class CornerEntities {

	public static final EntityType<DimensionalPaintingEntity> DIMENSIONAL_PAINTING_ENTITY = get("dimensional_painting",
		FabricEntityTypeBuilder
			.create()
			.entityFactory(DimensionalPaintingEntity::new)
			.dimensions(new EntityDimensions(0.5F, 0.5F, false))
			.trackRangeChunks(10)
			.trackedUpdateRate(Integer.MAX_VALUE)
			.build());
//	public static final EntityType<CorvusEntity> CORVUS_ENTITY = get("corvus", QuiltEntityTypeBuilder.createMob().entityFactory(CorvusEntity::new).spawnGroup(SpawnGroup.AMBIENT)
//			.setDimensions(new EntityDimensions(0.375F, 0.875F, false)).maxBlockTrackingRange(10).defaultAttributes(CorvusEntity.createAttributes()).build());
	public static final EntityType<Boat> GAIA_BOAT = get("gaia_boat",
		FabricEntityTypeBuilder
			.create(MobCategory.MISC, CornerBoat.GAIA.factory(false))
			.dimensions(EntityDimensions.scalable(1.375f, 0.5625f))
			.trackRangeChunks(10)
			.build());
	public static final EntityType<Boat> GAIA_CHEST_BOAT = get("gaia_chest_boat",
			FabricEntityTypeBuilder
			.create(MobCategory.MISC, CornerBoat.GAIA.factory(true))
			.dimensions(EntityDimensions.scalable(1.375f, 0.5625f))
			.trackRangeChunks(10)
			.build());

	public static void init() {
	}

	public static <E extends Entity, T extends EntityType<E>> T get(String id, T entity) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, TheCorners.id(id), entity);
	}

}
