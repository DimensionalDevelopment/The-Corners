package net.ludocrypt.corners.client.entity.corvus;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class CorvusAnimations {

	public static final Keyframe ROTATE_ORIGIN = new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
		AnimationChannel.Interpolations.CATMULLROM);

	public static AnimationDefinition.Builder tuckWings(AnimationDefinition.Builder curr, Keyframe leftWing,
			Keyframe leftWing2, Keyframe leftWingMid, Keyframe rightWing,
			Keyframe rightWing2, Keyframe rightWingMid, float time) {
		return curr
			.addAnimation("left_wing",
				new AnimationChannel(AnimationChannel.Targets.ROTATION, leftWing,
					new Keyframe(time, KeyframeAnimations.degreeVec(0.0F, 0.0F, 90.0F), AnimationChannel.Interpolations.CATMULLROM)))
			.addAnimation("left_wing_2",
				new AnimationChannel(AnimationChannel.Targets.ROTATION, leftWing2,
					new Keyframe(time, KeyframeAnimations.degreeVec(20.0F, -90.0F, -30.0F),
						AnimationChannel.Interpolations.CATMULLROM)))
			.addAnimation("left_wing_mid_r1",
				new AnimationChannel(AnimationChannel.Targets.ROTATION, leftWingMid,
					new Keyframe(time, KeyframeAnimations.degreeVec(0.0F, 90.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)))
			.addAnimation("right_wing",
				new AnimationChannel(AnimationChannel.Targets.ROTATION, rightWing,
					new Keyframe(time, KeyframeAnimations.degreeVec(0.0F, 0.0F, -90.0F), AnimationChannel.Interpolations.CATMULLROM)))
			.addAnimation("right_wing_2",
				new AnimationChannel(AnimationChannel.Targets.ROTATION, rightWing2,
					new Keyframe(time, KeyframeAnimations.degreeVec(20.0F, 90.0F, 30.0F), AnimationChannel.Interpolations.CATMULLROM)))
			.addAnimation("right_wing_mid_r1", new AnimationChannel(AnimationChannel.Targets.ROTATION, rightWingMid,
				new Keyframe(time, KeyframeAnimations.degreeVec(0.0F, -90.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)));
	}

}
