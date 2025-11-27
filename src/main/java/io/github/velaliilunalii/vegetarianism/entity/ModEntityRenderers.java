package io.github.velaliilunalii.vegetarianism.entity;

import io.github.velaliilunalii.vegetarianism.entity.custom.MetalDetectorBobberRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModEntityRenderers {
	public static void register() {
		EntityRendererRegistry.register(ModEntities.METAL_DETECTOR_BOBBER, MetalDetectorBobberRenderer::new);
	}
}
