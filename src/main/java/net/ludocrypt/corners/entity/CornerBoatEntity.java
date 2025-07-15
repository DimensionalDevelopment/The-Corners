package net.ludocrypt.corners.entity;

import Z;
import java.util.function.Supplier;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class CornerBoatEntity extends Boat {

	private final CornerBoat boatData;

	public CornerBoatEntity(EntityType<? extends Boat> type, Level world, CornerBoat boatData) {
		super(type, world);
		this.boatData = boatData;
	}

	public CornerBoat getBoatData() {
		return boatData;
	}

	@Override
	public Boat.Type getVariant() {
		return Boat.Type.OAK;
	}

	@Override
	public void setVariant(Boat.Type type) {
	}

	@Override
	public Item getDropItem() {
		return boatData.boat().asItem();
	}

	public static Boat copy(Boat original, CornerBoat boatData) {
		var chest = original instanceof ChestBoat;
		var boat = boatData.factory(chest).create(boatData.entityType(chest), original.level());
		boat.absMoveTo(original.getX(), original.getY(), original.getZ());
		return boat;
	}

	@SuppressWarnings("deprecation")
	public enum CornerBoat implements StringRepresentable {

		GAIA("gaia", () -> CornerBlocks.GAIA_PLANKS, () -> CornerBlocks.GAIA_BOAT, () -> CornerBlocks.GAIA_CHEST_BOAT,
				() -> CornerEntities.GAIA_BOAT, () -> CornerEntities.GAIA_CHEST_BOAT);

		private final String name;
		private final Supplier<ItemLike> planks;
		private final Supplier<ItemLike> boat;
		private final Supplier<ItemLike> chestBoat;
		private final Supplier<EntityType<Boat>> entityType;
		private final Supplier<EntityType<Boat>> chestEntityType;
		public static final StringRepresentable.EnumCodec<CornerBoat> CODEC = StringRepresentable
			.fromEnum(CornerBoatEntity.CornerBoat::values);

		CornerBoat(String name, Supplier<ItemLike> planks, Supplier<ItemLike> boat,
				Supplier<ItemLike> chestBoat, Supplier<EntityType<Boat>> entityType,
				Supplier<EntityType<Boat>> chestEntityType) {
			this.name = name;
			this.planks = planks;
			this.boat = boat;
			this.chestBoat = chestBoat;
			this.entityType = entityType;
			this.chestEntityType = chestEntityType;
		}

		public ItemLike planks() {
			return planks.get();
		}

		public ItemLike boat() {
			return boat.get();
		}

		public ItemLike chestBoat() {
			return chestBoat.get();
		}

		public EntityType<Boat> entityType(boolean chest) {
			return chest ? chestEntityType.get() : entityType.get();
		}

		public static CornerBoat getType(String name) {
			return CODEC.byName(name, GAIA);
		}

		public EntityType.EntityFactory<Boat> factory(boolean chest) {
			return (type, world) -> chest ? new CornerChestBoatEntity(type, world, this)
					: new CornerBoatEntity(type, world, this);
		}

		public ResourceLocation id() {
			return TheCorners.id(name);
		}

		@Override
		public String getSerializedName() {
			return name;
		}

	}

}
