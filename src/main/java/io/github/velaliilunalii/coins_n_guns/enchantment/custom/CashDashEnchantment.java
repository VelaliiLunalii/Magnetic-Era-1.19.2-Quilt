package io.github.velaliilunalii.coins_n_guns.enchantment.custom;

import io.github.velaliilunalii.coins_n_guns.enchantment.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class CashDashEnchantment extends Enchantment {
	public CashDashEnchantment() {
		super(Rarity.COMMON, EnchantmentTarget.CROSSBOW, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMinPower(int level) {
		return 1 + (level - 1) * 10;
	}

	@Override
	public int getMaxPower(int level) {return 50;}

	@Override
	public int getMaxLevel() {return 1;}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != ModEnchantments.TAX_EVASION;
	}
}
