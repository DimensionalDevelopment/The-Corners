package net.ludocrypt.corners.entity.covrus.goal;

import java.util.EnumSet;

import net.ludocrypt.corners.entity.covrus.CorvusEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class CorvusIdlingGoal extends Goal {

	private final CorvusEntity mob;
	private double deltaX;
	private double deltaZ;
	private int lookTime;

	public CorvusIdlingGoal(CorvusEntity mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return this.mob.getRandom().nextFloat() < 0.02F;
	}

	@Override
	public boolean canContinueToUse() {
		return this.lookTime >= 0;
	}

	@Override
	public void start() {
		double d = Math.PI * 2 * this.mob.getRandom().nextDouble();
		this.deltaX = Math.cos(d);
		this.deltaZ = Math.sin(d);
		this.lookTime = 20 + this.mob.getRandom().nextInt(20);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		--this.lookTime;
		this.mob.getLookControl().setLookAt(this.mob.getX() + this.deltaX, this.mob.getEyeY(), this.mob.getZ() + this.deltaZ);
	}

}
