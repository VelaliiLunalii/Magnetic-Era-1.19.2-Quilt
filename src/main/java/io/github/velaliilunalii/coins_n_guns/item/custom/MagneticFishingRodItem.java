package io.github.velaliilunalii.coins_n_guns.item.custom;

import io.github.velaliilunalii.coins_n_guns.enchantment.ModEnchantments;
import io.github.velaliilunalii.coins_n_guns.entity.ModEntities;
import io.github.velaliilunalii.coins_n_guns.entity.custom.MagneticFishingRodBobberEntity;
import io.github.velaliilunalii.coins_n_guns.mixin.FishingBobberEntityAccessorMixin;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class MagneticFishingRodItem extends FishingRodItem {
	public MagneticFishingRodItem(Settings settings) {
		super(settings);
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (user.fishHook != null) {
			if (!world.isClient) {
				int i = user.fishHook.use(itemStack);
				itemStack.damage(i, user, (p) -> p.sendToolBreakStatus(hand));
			}

			world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
		} else {
			world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			if (!world.isClient) {
				int i = EnchantmentHelper.getLure(itemStack);
				int j = EnchantmentHelper.getLuckOfTheSea(itemStack);
				int k = EnchantmentHelper.getLevel(ModEnchantments.BOOTY, itemStack);
				MagneticFishingRodBobberEntity bobber = ModEntities.MAGNETIC_FISHING_ROD_BOBBER.create(world);
				if (bobber != null) {
					bobber.setPosition(user.getX(), user.getY() + user.getStandingEyeHeight(), user.getZ());
					bobber.setOwner(user); // or set owner UUID
					FishingBobberEntityAccessorMixin accessor = (FishingBobberEntityAccessorMixin) bobber;
					accessor.setLureLevelLevel(i);
					accessor.setLuckOfTheSeaLevel(j);
					bobber.setBootyLevel(k);
					bobber.setThrow();
					world.spawnEntity(bobber);
				}
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			user.emitGameEvent(GameEvent.ITEM_INTERACT_START);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}
}
