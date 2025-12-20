package io.github.velaliilunalii.coins_n_guns;

import io.github.velaliilunalii.coins_n_guns.block.ModBlocks;
import io.github.velaliilunalii.coins_n_guns.enchantment.ModEnchantments;
import io.github.velaliilunalii.coins_n_guns.entity.ModEntities;
import io.github.velaliilunalii.coins_n_guns.event.ModEvents;
import io.github.velaliilunalii.coins_n_guns.item.ModItems;
import io.github.velaliilunalii.coins_n_guns.loot.ModLootTableModifiers;
import io.github.velaliilunalii.coins_n_guns.particle.ModParticles;
import io.github.velaliilunalii.coins_n_guns.sound.ModSounds;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoinsNGuns implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Coins 'n' Guns");
	public static final String MOD_ID = "coins_n_guns";

	@Override
	public void onInitialize(ModContainer mod) {
		ModBlocks.register(mod);
		ModItems.register(mod);
		ModLootTableModifiers.register();
		ModEntities.register();
		ModEvents.register(mod);
		ModSounds.register();
		ModEnchantments.register();
		ModParticles.register();
	}
}
