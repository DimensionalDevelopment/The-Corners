package net.ludocrypt.corners.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.advancements.AdvancementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

	public ServerPlayerEntityMixin(Level world, BlockPos pos, float f, GameProfile gameProfile) {
		super(world, pos, f, gameProfile);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void corners$tick(CallbackInfo ci) {

		if (this.level().dimension().location().getNamespace().equals("corners")) {
			AdvancementHelper.grantAdvancement(this, TheCorners.id("root"));
		}

	}

}
