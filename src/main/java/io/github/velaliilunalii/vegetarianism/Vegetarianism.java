package io.github.velaliilunalii.vegetarianism;

import io.github.velaliilunalii.vegetarianism.block.ModBlocks;
import io.github.velaliilunalii.vegetarianism.entity.ModEntities;
import io.github.velaliilunalii.vegetarianism.event.ModEvents;
import io.github.velaliilunalii.vegetarianism.item.ModItems;
import io.github.velaliilunalii.vegetarianism.item.ModModelPredicateProvider;
import io.github.velaliilunalii.vegetarianism.loot.ModLootTableModifiers;
import io.github.velaliilunalii.vegetarianism.loot.ModLootTables;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vegetarianism implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Vegetarianism");
	public static final String MODID = "vegetarianism";

	@Override
	public void onInitialize(ModContainer mod) {
		ModBlocks.register(mod);
		ModItems.register(mod);
		ModLootTableModifiers.register();
		ModEntities.register();
		ModModelPredicateProvider.register();
		ModEvents.register(mod);
	}
}
