package io.github.velaliilunalii.vegetarianism;

import io.github.velaliilunalii.vegetarianism.block.ModBlocksClient;
import io.github.velaliilunalii.vegetarianism.entity.ModEntityRenderers;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class VegetarianismClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ModBlocksClient.register(mod);
		ModEntityRenderers.register();
	}
}
