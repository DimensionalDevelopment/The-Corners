package net.ludocrypt.corners.mixin;

import net.ludocrypt.corners.TheCorners;
import net.ludocrypt.corners.client.render.DeepBookshelfRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(ModelManager.class)
public class BakedModelManagerMixin {

	@Shadow
	@Final
	@Mutable
	private static Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;
	static {
		Map<ResourceLocation, ResourceLocation> newAtli = new HashMap<ResourceLocation, ResourceLocation>();
		newAtli.putAll(VANILLA_ATLASES);
		newAtli.put(DeepBookshelfRenderer.DEEP_BOOKSHELF_ATLAS_TEXTURE, TheCorners.id("deep"));
		VANILLA_ATLASES = Map.copyOf(newAtli);
	}

}
