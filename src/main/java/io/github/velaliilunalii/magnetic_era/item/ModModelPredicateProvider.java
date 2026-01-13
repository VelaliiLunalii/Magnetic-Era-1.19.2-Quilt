package io.github.velaliilunalii.magnetic_era.item;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModModelPredicateProvider {
	public static void register() {
		ModelPredicateProviderRegistry.register(ModItems.MAGNETIC_FISHING_ROD, new Identifier(MagneticEra.MOD_ID, "cast"),
			(itemStack, clientWorld, livingEntity, seed) -> {
				return livingEntity instanceof PlayerEntity player && player.fishHook != null ? 1.0F : 0.0F;
			});
	}
}
