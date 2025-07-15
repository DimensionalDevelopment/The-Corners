package net.ludocrypt.corners.mixin;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface SignTypeAccessor {

	@Invoker
	static WoodType callRegister(WoodType type) {
		throw new UnsupportedOperationException();
	}

}
