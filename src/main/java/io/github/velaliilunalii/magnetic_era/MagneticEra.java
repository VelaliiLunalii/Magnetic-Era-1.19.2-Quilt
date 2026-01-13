package io.github.velaliilunalii.magnetic_era;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import io.github.velaliilunalii.magnetic_era.enchantment.ModEnchantments;
import io.github.velaliilunalii.magnetic_era.entity.ModEntities;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import io.github.velaliilunalii.magnetic_era.particle.ModParticles;
import io.github.velaliilunalii.magnetic_era.sound.ModSounds;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagneticEra implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Magnetic Era");
	public static final String MOD_ID = "magnetic_era";

	@Override
	public void onInitialize(ModContainer mod) {
		ModBlocks.register(mod);
		ModItems.register(mod);
		ModEntities.register();
		ModSounds.register();
		ModEnchantments.register();
		ModParticles.register();
		ModBlockEntities.register();
	}
}
