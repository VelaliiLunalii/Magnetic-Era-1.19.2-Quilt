package io.github.velaliilunalii.coins_n_guns.enchantment.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class GamblingEnchantment extends Enchantment {
	public GamblingEnchantment() {
		super(Rarity.COMMON, EnchantmentTarget.CROSSBOW, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMinPower(int level) {
		return 1 + (level - 1) * 10;
	}

	@Override
	public int getMaxPower(int level) {return 50;}

	@Override
	public int getMaxLevel() {return 3;}
}
