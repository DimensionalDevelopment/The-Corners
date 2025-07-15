package net.ludocrypt.corners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.ludocrypt.corners.client.render.StrongPostEffect;
import net.ludocrypt.corners.config.CornerConfig;
import net.ludocrypt.corners.init.CornerBiomes;
import net.ludocrypt.corners.init.CornerBlocks;
import net.ludocrypt.corners.init.CornerEntities;
import net.ludocrypt.corners.init.CornerModelRenderers;
import net.ludocrypt.corners.init.CornerPaintings;
import net.ludocrypt.corners.init.CornerRadioRegistry;
import net.ludocrypt.corners.init.CornerSoundEvents;
import net.ludocrypt.corners.packet.ClientToServerPackets;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class TheCorners implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("The Corners");

	@Override
	public void onInitialize(ModContainer mod) {
		AutoConfig.register(CornerConfig.class, GsonConfigSerializer::new);
		CornerBlocks.init();
		CornerBiomes.init();
		CornerEntities.init();
		CornerPaintings.init();
		CornerSoundEvents.init();
		CornerRadioRegistry.init();
		CornerModelRenderers.init();
		ClientToServerPackets.manageClientToServerPackets();
		Registry.register(PostEffect.POST_EFFECT_CODEC, id("strong_shader"), StrongPostEffect.CODEC);
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation("corners", id);
	}

}
