package io.github.velaliilunalii.magnetic_era;

import io.github.velaliilunalii.magnetic_era.block.ModBlocksClient;
import io.github.velaliilunalii.magnetic_era.entity.ModEntityRenderers;
import io.github.velaliilunalii.magnetic_era.item.ModModelPredicateProvider;
import io.github.velaliilunalii.magnetic_era.particle.ModParticlesClient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class MagneticEraClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ModModelPredicateProvider.register();
		ModBlocksClient.register(mod);
		ModEntityRenderers.register();
		ModParticlesClient.register();
	}
}
