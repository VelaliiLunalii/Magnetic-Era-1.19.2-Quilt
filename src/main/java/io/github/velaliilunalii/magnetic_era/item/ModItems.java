package io.github.velaliilunalii.magnetic_era.item;

import io.github.velaliilunalii.magnetic_era.item.custom.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ModItems {
	public static final Item MAGNETIC_FISHING_ROD = new MagneticFishingRodItem(
		new QuiltItemSettings().maxDamage(64).group(ModItemGroups.MAGNETIC_ERA_GROUP));
	public static final Item MAGNET = new MagnetItem(
		new QuiltItemSettings().maxCount(1).group(ModItemGroups.MAGNETIC_ERA_GROUP));
	public static final Item MAGNETITE = new Item(
		new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP));
	public static final Item MAGNETIC_SHIELD = new MagneticShieldItem(
		new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP).maxCount(1));
	public static final Item MAGNETIC_BOOTS = new MagneticArmorItem(ArmorMaterials.IRON, EquipmentSlot.FEET,
		new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP));
	public static final Item PHASE_INGOT = new Item(
		new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP));
	public static final Item COPPER_WIRE = new CopperWireItem(
		new QuiltItemSettings().group(ModItemGroups.MAGNETIC_ERA_GROUP));


	public static void register(ModContainer mod) {
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnetic_fishing_rod"), MAGNETIC_FISHING_ROD);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnet"), MAGNET);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnetite"), MAGNETITE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnetic_shield"), MAGNETIC_SHIELD);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnetic_boots"), MAGNETIC_BOOTS);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "phase_ingot"), PHASE_INGOT);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "copper_wire"), COPPER_WIRE);
	}
}
