package io.github.velaliilunalii.magnetic_era.entity;

import io.github.velaliilunalii.magnetic_era.entity.client.*;
import io.github.velaliilunalii.magnetic_era.entity.client.MagneticFishingRodBobberRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModEntityRenderers {
	public static void register() {
		EntityRendererRegistry.register(ModEntities.MAGNETIC_FISHING_ROD_BOBBER, MagneticFishingRodBobberRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLOCK_MAGNETIC_FIELD, MagneticFieldEntityRenderer::new);
	}
}
