package io.github.velaliilunalii.vegetarianism.item;

import io.github.velaliilunalii.vegetarianism.block.ModBlocks;
import io.github.velaliilunalii.vegetarianism.item.custom.MagnetItem;
import io.github.velaliilunalii.vegetarianism.item.custom.MetalDetectorRodItem;
import io.github.velaliilunalii.vegetarianism.item.custom.SoyPodItem;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ModItems {
	public static final Item SOY_BEANS = new AliasedBlockItem(ModBlocks.SOY_CROP,
		new QuiltItemSettings().group(ModItemGroups.VEGETARIANISM_GROUP));
	public static final Item SOY_POD = new SoyPodItem(
		new QuiltItemSettings().group(ModItemGroups.VEGETARIANISM_GROUP));
	public static final Item TOFU_SLICE = new Item(
		new QuiltItemSettings().group(ModItemGroups.VEGETARIANISM_GROUP).food(ModFoodComponents.TOFU_SLICE));
	public static final Item METAL_DETECTOR_ROD = new MetalDetectorRodItem(
		new QuiltItemSettings().maxDamage(64).group(ModItemGroups.VEGETARIANISM_GROUP));
	public static final Item MAGNET = new MagnetItem(
		new QuiltItemSettings().maxCount(1).group(ModItemGroups.VEGETARIANISM_GROUP));

	public static void register(ModContainer mod) {
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "soy_beans"), SOY_BEANS);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "soy_pod"), SOY_POD);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "tofu_slice"), TOFU_SLICE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "metal_detector_rod"), METAL_DETECTOR_ROD);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "magnet"), MAGNET);
	}
}
