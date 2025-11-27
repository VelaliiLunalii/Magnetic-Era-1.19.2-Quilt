package io.github.velaliilunalii.vegetarianism.item;

import io.github.velaliilunalii.vegetarianism.Vegetarianism;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModModelPredicateProvider {
	public static void register(){
		ModelPredicateProviderRegistry.register(ModItems.METAL_DETECTOR_ROD, new Identifier(Vegetarianism.MODID, "cast"),
			(itemStack, clientWorld, livingEntity, seed) -> {
			return livingEntity instanceof PlayerEntity player && player.fishHook != null ? 1.0F : 0.0F;
		});
	}
}
