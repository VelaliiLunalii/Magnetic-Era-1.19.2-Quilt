package io.github.velaliilunalii.vegetarianism.item;

import io.github.velaliilunalii.vegetarianism.Vegetarianism;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroups {
	public static final ItemGroup VEGETARIANISM_GROUP = FabricItemGroupBuilder.build(
		new Identifier(Vegetarianism.MODID, "vegetarianism_group"),
		() -> new ItemStack(ModItems.TOFU_SLICE)
	);
}
