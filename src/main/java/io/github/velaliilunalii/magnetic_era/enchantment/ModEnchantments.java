package io.github.velaliilunalii.magnetic_era.enchantment;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.enchantment.custom.BootyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEnchantments {
	public static final Enchantment BOOTY = registerEnchantment("booty", new BootyEnchantment());

	private static Enchantment registerEnchantment(String name, Enchantment enchantment) {
		return Registry.register(Registry.ENCHANTMENT, new Identifier(MagneticEra.MOD_ID, name), enchantment);
	}

	public static void register() {
	}
}
