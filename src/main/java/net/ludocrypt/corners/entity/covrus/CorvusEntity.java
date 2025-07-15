package net.ludocrypt.corners.entity.covrus;

import net.ludocrypt.corners.entity.covrus.goal.CorvusIdlingGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;

public class CorvusEntity extends Mob implements FlyingAnimal {

	public static final EntityDataSerializer<CorvusPose> CORVUS_POSE_DATA_HANDLER = EntityDataSerializer
		.simpleEnum(CorvusPose.class);
	public static final EntityDataAccessor<CorvusPose> CORVUS_POSE = SynchedEntityData
		.defineId(CorvusEntity.class, CORVUS_POSE_DATA_HANDLER);
	public AnimationState restingAnimation = new AnimationState();

	public CorvusEntity(EntityType<? extends CorvusEntity> entityType, Level world) {
		super(entityType, world);
		this.goalSelector.addGoal(10, new CorvusIdlingGoal(this));
	}

	public static AttributeSupplier.Builder createLivingAttributes() {
		return Mob.createMobAttributes();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CORVUS_POSE, CorvusPose.SITTING);
	}

	@Override
	public boolean isFlying() {
		return false;
	}

	public CorvusPose getCorvusPose() {
		return CorvusPose.IDLING;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.tickCount % 60 == 0) {
			this.restingAnimation.start(this.tickCount);
		}

	}

	static {
		EntityDataSerializers.registerSerializer(CORVUS_POSE_DATA_HANDLER);
	}

}
