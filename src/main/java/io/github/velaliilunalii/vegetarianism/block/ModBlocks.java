package io.github.velaliilunalii.vegetarianism.block;

import io.github.velaliilunalii.vegetarianism.block.custom.CrateBlock;
import io.github.velaliilunalii.vegetarianism.block.custom.SoyCropBlock;
import io.github.velaliilunalii.vegetarianism.block.custom.TofuBlock;
import io.github.velaliilunalii.vegetarianism.item.ModFoodComponents;
import io.github.velaliilunalii.vegetarianism.item.ModItemGroups;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ModBlocks {
	public static final Block SOY_CROP = new SoyCropBlock(QuiltBlockSettings.copyOf(Blocks.POTATOES).ticksRandomly());
	public static final Block TOFU = new TofuBlock(QuiltBlockSettings.copyOf(Blocks.CAKE));
	public static final Block CRATE = new CrateBlock(QuiltBlockSettings.copyOf(Blocks.OAK_PLANKS));

	public static void register(ModContainer mod) {
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "soy_crop"), SOY_CROP);
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "tofu"), TOFU);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "tofu"),
			new BlockItem(TOFU, new QuiltItemSettings().group(ModItemGroups.VEGETARIANISM_GROUP).food(ModFoodComponents.TOFU)));
		Registry.register(Registry.BLOCK, new Identifier(mod.metadata().id(), "crate"), CRATE);
		Registry.register(Registry.ITEM, new Identifier(mod.metadata().id(), "crate"),
			new BlockItem(CRATE, new QuiltItemSettings().group(ModItemGroups.VEGETARIANISM_GROUP)));
	}
}
