package io.github.velaliilunalii.vegetarianism.loot;

import io.github.velaliilunalii.vegetarianism.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {
	public static final Identifier SHIPWRECK_SUPPLY = new Identifier("minecraft", "chests/shipwreck_supply");
	public static final Identifier SHIPWRECK_TREASURE = new Identifier("minecraft", "chests/shipwreck_treasure");
	public static final Identifier SHIPWRECK_MAP = new Identifier("minecraft", "chests/shipwreck_map");

	public static void register() {
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (id.equals(SHIPWRECK_SUPPLY)) {
				LootPool pool = LootPool.builder()
					.with(ItemEntry.builder(ModItems.SOY_BEANS))  // your custom item
					.rolls(ConstantLootNumberProvider.create(1))       // number of rolls
					.build();
				tableBuilder.pool(pool);
			}
			if (id.equals(SHIPWRECK_TREASURE)) {
				LootPool pool = LootPool.builder()
					.with(ItemEntry.builder(ModItems.SOY_BEANS))  // your custom item
					.rolls(ConstantLootNumberProvider.create(1))       // number of rolls
					.build();
				tableBuilder.pool(pool);
			}
			if (id.equals(SHIPWRECK_MAP)) {
				LootPool pool = LootPool.builder()
					.with(ItemEntry.builder(ModItems.SOY_BEANS))  // your custom item
					.rolls(ConstantLootNumberProvider.create(1))       // number of rolls
					.build();
				tableBuilder.pool(pool);
			}
		});
	}
}
