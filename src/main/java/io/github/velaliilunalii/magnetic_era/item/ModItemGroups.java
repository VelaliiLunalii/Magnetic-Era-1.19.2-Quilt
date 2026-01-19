package io.github.velaliilunalii.magnetic_era.item;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroups {
	public static final ItemGroup MAGNETIC_ERA_GROUP = FabricItemGroupBuilder.build(
		new Identifier(MagneticEra.MOD_ID, "magnetic_era_group"),
		() -> new ItemStack(ModItems.PHASE_INGOT)
	);
}
