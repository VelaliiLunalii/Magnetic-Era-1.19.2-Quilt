package io.github.velaliilunalii.coins_n_guns.item.custom;

import com.google.common.collect.Lists;
import io.github.velaliilunalii.coins_n_guns.enchantment.ModEnchantments;
import io.github.velaliilunalii.coins_n_guns.entity.custom.CopperCoinProjectileEntity;
import io.github.velaliilunalii.coins_n_guns.entity.custom.GoldCoinProjectileEntity;
import io.github.velaliilunalii.coins_n_guns.entity.custom.IronCoinProjectileEntity;
import io.github.velaliilunalii.coins_n_guns.item.ModItems;
import io.github.velaliilunalii.coins_n_guns.sound.ModSounds;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class PistolItem extends CrossbowItem {
	public static final Predicate<ItemStack> PISTOL_AMMO = (stack) ->
		stack.isOf(ModItems.GOLD_COIN) || stack.isOf(ModItems.IRON_COIN) || stack.isOf(ModItems.COPPER_COIN);
	private boolean charged = false;
	private boolean loaded = false;
	private int tax_evasion_cd = 0;

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	public static int getAmmo(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound == null) return 0;
		return nbtCompound.getInt("Ammo");
	}

	public static void setAmmo(ItemStack stack, int ammo) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putInt("Ammo", ammo);
	}

	public static boolean isCharged(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null && nbtCompound.getBoolean("Charged");
	}

	public static void setCharged(ItemStack stack, boolean charged) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putBoolean("Charged", charged);
	}

	public static int getTaxEvasionCD(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound == null) return 0;
		return nbtCompound.getInt("Tax_evasion_cd");
	}

	public static void setTaxEvasionCD(ItemStack stack, int tax_evasion_cd) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putInt("Tax_evasion_cd", tax_evasion_cd);
	}

	public static void decrementTaxEvasionCD(ItemStack stack, int decrement) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		int tax_evasion_cd = getTaxEvasionCD(stack);
		nbtCompound.putInt("Tax_evasion_cd", Math.max(tax_evasion_cd - decrement, 0));
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if(getTaxEvasionCD(stack) > 0) decrementTaxEvasionCD(stack, 1);
	}

	public PistolItem(Settings settings) {
		super(settings);
	}

	private static List<ItemStack> getProjectiles(ItemStack crossbow) {
		List<ItemStack> list = Lists.newArrayList();
		NbtCompound nbtCompound = crossbow.getNbt();
		if (nbtCompound != null && nbtCompound.contains("ChargedProjectiles", 9)) {
			NbtList nbtList = nbtCompound.getList("ChargedProjectiles", 10);
			if (nbtList != null) {
				for(int i = 0; i < nbtList.size(); ++i) {
					NbtCompound nbtCompound2 = nbtList.getCompound(i);
					list.add(ItemStack.fromNbt(nbtCompound2));
				}
			}
		}

		return list;
	}

	//->getPullProgress
	//->loadProjectiles
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int i = this.getMaxUseTime(stack) - remainingUseTicks;
		float f = getPullProgress(i, stack);
		if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
			setCharged(stack, true);
			setAmmo(stack, 3);
			SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
			world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), ModSounds.COIN_INSERT, soundCategory, 1F, 1.2F);
		}

	}

	public static float getPullProgress(int useTicks, ItemStack stack) {
		float f = (float)useTicks / (float)getPullTime(stack);
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	//->lookupLoadAmmo
	//->loadProjectile
	private static boolean loadProjectiles(LivingEntity shooter, ItemStack stack) {
		int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack);
		int j = i == 0 ? 1 : 3;
		int gambling_level = EnchantmentHelper.getLevel(ModEnchantments.GAMBLING, stack);
		float gambling_chance = (float) (gambling_level * 2) /10;

		boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
		ItemStack itemStack = lookupLoadAmmo(shooter);

		boolean fromPouch = itemStack.getItem() instanceof CoinPouchItem;
		boolean should_consume = new Random().nextFloat()>gambling_chance;
		if (fromPouch) itemStack = CoinPouchItem.decrementAndGetSelectedAmmo(itemStack, 1, should_consume);
		if (!should_consume && shooter instanceof ServerPlayerEntity serverPlayer)
			serverPlayer.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 0.7F, 1.0F);

		ItemStack itemStack2 = itemStack.copy();

		if (itemStack.isEmpty() && !bl) return false;

		for(int k = 0; k < j; ++k) {
			if (k > 0) {
				itemStack = itemStack2.copy();
			}

			if (itemStack.isEmpty() && bl) {
				itemStack = new ItemStack(ModItems.GOLD_COIN);
				itemStack2 = itemStack.copy();
			}

			loadProjectile(shooter, stack, itemStack, k > 0, bl, fromPouch);
		}

		return true;
	}

	private static ItemStack lookupLoadAmmo(LivingEntity shooter){
		if (shooter instanceof PlayerEntity player) {
			for (int i = 0; i < player.getInventory().size(); ++i) {
				ItemStack inventoryStack = player.getInventory().getStack(i);
				if (inventoryStack.getItem() instanceof CoinPouchItem && !CoinPouchItem.isSelectedEmpty(inventoryStack)) {
					return inventoryStack;
				}
			}
			for (int i = 0; i < player.getInventory().size(); ++i) {
				ItemStack inventoryStack = player.getInventory().getStack(i);
				if (PISTOL_AMMO.test(inventoryStack)) {
					return inventoryStack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private static boolean hasAmmo(LivingEntity shooter){
		if (shooter instanceof PlayerEntity player) {
			for (int i = 0; i < player.getInventory().size(); ++i) {
				ItemStack inventoryStack = player.getInventory().getStack(i);
				if (PISTOL_AMMO.test(inventoryStack) || (inventoryStack.getItem() instanceof CoinPouchItem && !CoinPouchItem.isSelectedEmpty(inventoryStack))) {
					return true;
				}
			}
		}
		return false;
	}

	//->putProjectile
	private static void loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean multishotCopy, boolean creative, boolean fromPouch) {
		ItemStack itemStack;
		if (!creative && !fromPouch) {
			itemStack = projectile.split(1);
			if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
				((PlayerEntity)shooter).getInventory().removeOne(projectile);
			}
		} else {
			itemStack = projectile.copy();
		}
		NbtCompound nbtCompound = itemStack.getOrCreateNbt();
		nbtCompound.putBoolean("Multishot_copy", multishotCopy);
		putProjectile(crossbow, itemStack);

	}

	private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
		NbtCompound nbtCompound = crossbow.getOrCreateNbt();
		NbtList nbtList;
		if (nbtCompound.contains("ChargedProjectiles", 9)) {
			nbtList = nbtCompound.getList("ChargedProjectiles", 10);
		} else {
			nbtList = new NbtList();
		}

		NbtCompound nbtCompound2 = new NbtCompound();
		projectile.writeNbt(nbtCompound2);
		nbtList.add(nbtCompound2);
		nbtCompound.put("ChargedProjectiles", nbtList);
	}

	//->getSpeed
	//->shootAll
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (isCharged(itemStack)) {
			shootAll(world, user, hand, itemStack, getSpeed(itemStack), 1.0F);
			user.getItemCooldownManager().set(this, 10);
			setAmmo(itemStack, getAmmo(itemStack)-1);
			if (getAmmo(itemStack) <= 0) setCharged(itemStack, false);
			return TypedActionResult.consume(itemStack);
		} else if (!lookupLoadAmmo(user).isEmpty() || user.getAbilities().creativeMode) {
			if (!isCharged(itemStack)) {
				this.charged = false;
				this.loaded = false;
				user.setCurrentHand(hand);
				int i = EnchantmentHelper.getLevel(ModEnchantments.TAX_EVASION, itemStack);
				if (i > 0 && getTaxEvasionCD(itemStack) <= 0){
					Vec3d normalised_velocity = user.getVelocity().normalize();
					double horizontal_speed = user.isOnGround() ? 1.6 : 0.8;
					double vertical_speed = user.isOnGround() ? 0.2 : 0;
					user.addVelocity(normalised_velocity.x * horizontal_speed, vertical_speed, normalised_velocity.z * horizontal_speed);
					setTaxEvasionCD(itemStack, 40);
				}
			}

			return TypedActionResult.consume(itemStack);
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	private static float getSpeed(ItemStack stack) {
		return 3.15F;
	}

	//->getSoundPitches
	//->shoot
	//->postShoot
	public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
		List<ItemStack> list = getProjectiles(stack);
		float[] fs = getSoundPitches(entity.getRandom());

		for(int i = 0; i < list.size(); ++i) {
			ItemStack itemStack = (ItemStack)list.get(i);
			boolean bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
			if (!itemStack.isEmpty()) {
				if (i == 0) {
					shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 0.0F);
				} else if (i == 1) {
					shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, -10.0F);
				} else if (i == 2) {
					shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 10.0F);
				}
			}
		}

		postShoot(world, entity, stack);

		int i = EnchantmentHelper.getLevel(ModEnchantments.CASH_DASH, stack);
		if (i > 0) {
			Vec3d normalised_velocity = entity.getVelocity().normalize();
			double horizontal_speed = entity.isOnGround() ? 0.8 : 0.4;
			double vertical_speed = entity.isOnGround() ? 0.2 : 0;
			entity.addVelocity(normalised_velocity.x * horizontal_speed, vertical_speed, normalised_velocity.z * horizontal_speed);
		}
	}

	//->getSoundPitch
	private static float[] getSoundPitches(RandomGenerator random) {
		boolean bl = random.nextBoolean();
		return new float[]{1.0F, getSoundPitch(bl, random), getSoundPitch(!bl, random)};
	}

	private static float getSoundPitch(boolean flag, RandomGenerator random) {
		float f = flag ? 0.63F : 0.43F;
		return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
	}

	private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
		if (!world.isClient) {
			int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
			String pouch_type = CoinPouchItem.getPouchType(projectile);
			NbtCompound nbt = projectile.getNbt();
			boolean multishot_copy = nbt != null && nbt.getBoolean("Multishot_copy");
			if(projectile.isOf(ModItems.GOLD_COIN)) {
				GoldCoinProjectileEntity coinProjectileEntity;
				coinProjectileEntity = new GoldCoinProjectileEntity(shooter, world);
				coinProjectileEntity.setProperties(shooter, shooter.getPitch(), shooter.getYaw(), 0.0F, 1.5F, 1.0F);

				if (shooter instanceof CrossbowUser) {
					CrossbowUser crossbowUser = (CrossbowUser) shooter;
					crossbowUser.shoot(crossbowUser.getTarget(), crossbow, coinProjectileEntity, simulated);
				} else {
					Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
					Quaternion quaternion = new Quaternion(new Vec3f(vec3d), simulated, true);
					Vec3d vec3d2 = shooter.getRotationVec(1.0F);
					Vec3f vec3f = new Vec3f(vec3d2);
					vec3f.rotate(quaternion);
					coinProjectileEntity.setVelocity((double) vec3f.getX(), (double) vec3f.getY(), (double) vec3f.getZ(), divergence);
				}

				crossbow.damage(1, shooter, (e) -> e.sendToolBreakStatus(hand));
				if (i > 0) coinProjectileEntity.setPierceLevel(i);
				coinProjectileEntity.setPouchType(pouch_type);
				coinProjectileEntity.setMultishotCopy(multishot_copy);
				if (coinProjectileEntity.getPouchType().equals("Ender")) coinProjectileEntity.setNoGravity(true);
				world.spawnEntity(coinProjectileEntity);
				world.playSound((PlayerEntity)null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.COIN_SHOT, SoundCategory.PLAYERS, 1.0F, soundPitch*0.9f);

			}
			if(projectile.isOf(ModItems.IRON_COIN)) {
				IronCoinProjectileEntity coinProjectileEntity;
				coinProjectileEntity = new IronCoinProjectileEntity(shooter, world);
				coinProjectileEntity.setProperties(shooter, shooter.getPitch(), shooter.getYaw(), 0.0F, 1.5F, 1.0F);

				if (shooter instanceof CrossbowUser) {
					CrossbowUser crossbowUser = (CrossbowUser) shooter;
					crossbowUser.shoot(crossbowUser.getTarget(), crossbow, coinProjectileEntity, simulated);
				} else {
					Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
					Quaternion quaternion = new Quaternion(new Vec3f(vec3d), simulated, true);
					Vec3d vec3d2 = shooter.getRotationVec(1.0F);
					Vec3f vec3f = new Vec3f(vec3d2);
					vec3f.rotate(quaternion);
					coinProjectileEntity.setVelocity((double) vec3f.getX(), (double) vec3f.getY(), (double) vec3f.getZ(), divergence);
				}

				crossbow.damage(1, shooter, (e) -> e.sendToolBreakStatus(hand));
				if (i > 0) coinProjectileEntity.setPierceLevel(i);
				coinProjectileEntity.setPouchType(pouch_type);
				coinProjectileEntity.setMultishotCopy(multishot_copy);
				if (coinProjectileEntity.getPouchType().equals("Ender")) coinProjectileEntity.setNoGravity(true);
				world.spawnEntity(coinProjectileEntity);
				world.playSound((PlayerEntity)null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.COIN_SHOT, SoundCategory.PLAYERS, 1.0F, soundPitch*0.8f);
			}
			if(projectile.isOf(ModItems.COPPER_COIN)) {
				CopperCoinProjectileEntity coinProjectileEntity;
				coinProjectileEntity = new CopperCoinProjectileEntity(shooter, world);
				coinProjectileEntity.setProperties(shooter, shooter.getPitch(), shooter.getYaw(), 0.0F, 1.5F, 1.0F);

				if (shooter instanceof CrossbowUser) {
					CrossbowUser crossbowUser = (CrossbowUser) shooter;
					crossbowUser.shoot(crossbowUser.getTarget(), crossbow, coinProjectileEntity, simulated);
				} else {
					Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
					Quaternion quaternion = new Quaternion(new Vec3f(vec3d), simulated, true);
					Vec3d vec3d2 = shooter.getRotationVec(1.0F);
					Vec3f vec3f = new Vec3f(vec3d2);
					vec3f.rotate(quaternion);
					coinProjectileEntity.setVelocity((double) vec3f.getX(), (double) vec3f.getY(), (double) vec3f.getZ(), divergence);
				}

				crossbow.damage(1, shooter, (e) -> e.sendToolBreakStatus(hand));
				if (i > 0) coinProjectileEntity.setPierceLevel(i);
				coinProjectileEntity.setPouchType(pouch_type);
				coinProjectileEntity.setMultishotCopy(multishot_copy);
				if (coinProjectileEntity.getPouchType().equals("Ender")) coinProjectileEntity.setNoGravity(true);
				world.spawnEntity(coinProjectileEntity);
				world.playSound((PlayerEntity)null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.COIN_SHOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
			}
		}
	}

	//->clearProjectiles
	private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
		if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
			if (!world.isClient) {
				Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
			}

			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
		}

		if (getAmmo(stack) <= 1) clearProjectiles(stack);
	}

	private static void clearProjectiles(ItemStack crossbow) {
		NbtCompound nbtCompound = crossbow.getNbt();
		if (nbtCompound != null) {
			NbtList nbtList = nbtCompound.getList("ChargedProjectiles", 9);
			nbtList.clear();
			nbtCompound.put("ChargedProjectiles", nbtList);
		}

	}

	//->getQuickChargeSound
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!world.isClient) {float f = (float)(stack.getMaxUseTime() - remainingUseTicks) / (float)getPullTime(stack);
			if (f < 0.2F) {
				this.charged = false;
				this.loaded = false;
			}

			if (f >= 0.2F && !this.charged) {
				this.charged = true;
				world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), ModSounds.COIN_INSERT, SoundCategory.PLAYERS, 1F, 0.8F);
			}

			if (f >= 0.5F && !this.loaded) {
				this.loaded = true;
				world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), ModSounds.COIN_INSERT, SoundCategory.PLAYERS, 1F, 1F);
			}
		}

	}

	private SoundEvent getQuickChargeSound(int stage) {
		switch (stage) {
			case 1 -> {
				return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
			}
			case 2 -> {
				return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
			}
			case 3 -> {
				return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
			}
			default -> {
				return SoundEvents.ITEM_CROSSBOW_LOADING_START;
			}
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		if (Screen.hasShiftDown()) {
			tooltip.add(Text.literal("Uses coins as ammo")
				.styled(style -> style.withColor(Formatting.GRAY)));
			tooltip.add(Text.literal("Copper coins")
				.styled(style -> style.withColor(0xe77c56))
				.append(Text.literal(" are lightweight but deal low damage")
					.styled(style -> style.withColor(Formatting.GRAY))));
			tooltip.add(Text.literal("Iron coins")
				.styled(style -> style.withColor(0xececec))
				.append(Text.literal(" are heavy but deal medium damage")
					.styled(style -> style.withColor(Formatting.GRAY))));
			tooltip.add(Text.literal("Gold coins")
				.styled(style -> style.withColor(0xfdf55f))
				.append(Text.literal(" can bounce on blocks to deal heavy damage")
					.styled(style -> style.withColor(Formatting.GRAY))));
		}else {
			tooltip.add(Text.literal(""));
			tooltip.add(Text.literal("Press ")
				.styled(style -> style.withColor(Formatting.GRAY))
				.append(Text.literal("SHIFT")
					.styled(style -> style.withColor(Formatting.GOLD)))
				.append(Text.literal(" for pouch info"))
				.styled(style -> style.withColor(Formatting.GRAY)));
		}
	}
}
