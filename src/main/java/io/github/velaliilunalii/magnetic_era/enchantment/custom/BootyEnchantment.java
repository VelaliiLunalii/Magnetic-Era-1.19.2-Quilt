package io.github.velaliilunalii.magnetic_era.enchantment.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class BootyEnchantment extends Enchantment {
	public BootyEnchantment() {
		super(Rarity.COMMON, EnchantmentTarget.FISHING_ROD, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMinPower(int level) {
		return 1 + (level - 1) * 10;
	}

	@Override
	public int getMaxPower(int level) {return 50;}

	@Override
	public int getMaxLevel() {return 1;}
}
