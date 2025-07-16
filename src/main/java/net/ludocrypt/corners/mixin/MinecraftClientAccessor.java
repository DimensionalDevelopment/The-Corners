package net.ludocrypt.corners.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {

	@Accessor("pausePartialTick")
	float getPausedTickDelta();

}
