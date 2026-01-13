package io.github.velaliilunalii.magnetic_era.entity.custom;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import io.github.velaliilunalii.magnetic_era.mixin.FishingBobberEntityAccessorMixin;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class MagneticFishingRodBobberEntity extends FishingBobberEntity {
	private int booty = 0;

	public MagneticFishingRodBobberEntity(EntityType<? extends FishingBobberEntity> type, World world, int luckOfTheSeaLevel, int lureLevel) {
		super(type, world, luckOfTheSeaLevel, lureLevel);
	}

	public MagneticFishingRodBobberEntity(EntityType<? extends FishingBobberEntity> entityType, World world) {
		super(entityType, world);
	}

	public MagneticFishingRodBobberEntity(PlayerEntity thrower, World world, int luckOfTheSeaLevel, int lureLevel) {
		super(thrower, world, luckOfTheSeaLevel, lureLevel);
	}

	private boolean removeIfInvalid(PlayerEntity player) {
		ItemStack itemStack = player.getMainHandStack();
		ItemStack itemStack2 = player.getOffHandStack();
		boolean bl = itemStack.isOf(ModItems.MAGNETIC_FISHING_ROD);
		boolean bl2 = itemStack2.isOf(ModItems.MAGNETIC_FISHING_ROD);
		if (!player.isRemoved() && player.isAlive() && (bl || bl2) && !(this.squaredDistanceTo(player) > (double)1024.0F)) {
			return false;
		} else {
			this.discard();
			return true;
		}
	}

	public void setThrow(){
		float f = getOwner().getPitch();
		float g = getOwner().getYaw();
		float h = MathHelper.cos(-g * ((float)Math.PI / 180F) - (float)Math.PI);
		float i = MathHelper.sin(-g * ((float)Math.PI / 180F) - (float)Math.PI);
		float j = -MathHelper.cos(-f * ((float)Math.PI / 180F));
		float k = MathHelper.sin(-f * ((float)Math.PI / 180F));
		double d = getOwner().getX() - (double)i * 0.3;
		double e = getOwner().getEyeY();
		double l = getOwner().getZ() - (double)h * 0.3;
		this.refreshPositionAndAngles(d, e, l, g, f);
		Vec3d vec3d = new Vec3d((double)(-i), (double)MathHelper.clamp(-(k / j), -5.0F, 5.0F), (double)(-h));
		double m = vec3d.length();
		vec3d = vec3d.multiply(0.6 / m + this.random.nextTriangular((double)0.5F, 0.0103365), 0.6 / m + this.random.nextTriangular((double)0.5F, 0.0103365), 0.6 / m + this.random.nextTriangular((double)0.5F, 0.0103365));
		this.setVelocity(vec3d);
		this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI)));
		this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * (double)(180F / (float)Math.PI)));
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
	}

	@Override
	public int use(ItemStack usedItem) {
		FishingBobberEntityAccessorMixin accessor = (FishingBobberEntityAccessorMixin) this;
		PlayerEntity playerEntity = this.getPlayerOwner();
		if (!this.world.isClient && playerEntity != null && !this.removeIfInvalid(playerEntity)) {
			int i = 0;
			if (this.getHookedEntity() != null) {
				this.pullHookedEntity(this.getHookedEntity());
				Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, Collections.emptyList());
				this.world.sendEntityStatus(this, (byte)31);
				i = this.getHookedEntity() instanceof ItemEntity ? 3 : 5;
			} else if (accessor.getCaughtFish()) {
				float fishWeight = 85.0F; float junkWeight = 10.0F; float treasureWeight = 5.0F;
				int luck = accessor.getLuckOfTheSeaLevel();
				fishWeight     += luck * 1.0F;
				junkWeight     -= luck * 2.0F;
				treasureWeight += luck * 2.0F;

				float total = fishWeight + junkWeight + treasureWeight;
				float roll = random.nextFloat() * total;

				Identifier tableToUse;

				if(booty == 1){
					tableToUse = new Identifier(MagneticEra.MOD_ID, "gameplay/booty_magnetic_fishing");
				}else {
					if (roll < fishWeight) {
						tableToUse = new Identifier(MagneticEra.MOD_ID, "gameplay/magnetic_fishing");
					} else if (roll < fishWeight + junkWeight) {
						tableToUse = LootTables.FISHING_JUNK_GAMEPLAY;
					} else {
						tableToUse = LootTables.FISHING_TREASURE_GAMEPLAY;
					}
				}

				LootTable lootTable = this.world.getServer().getLootManager().getTable(tableToUse);
				LootContext.Builder builder = new LootContext.Builder((ServerWorld) this.world).parameter(LootContextParameters.ORIGIN, this.getPos()).parameter(LootContextParameters.TOOL, usedItem).parameter(LootContextParameters.THIS_ENTITY, this).random(this.random).luck(luck + this.getPlayerOwner().getLuck());
				List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.FISHING));
				Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, list);

				for(ItemStack itemStack : list) {
					ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), itemStack);
					double d = playerEntity.getX() - this.getX();
					double e = playerEntity.getY() - this.getY();
					double f = playerEntity.getZ() - this.getZ();
					double g = 0.1;
					itemEntity.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
					this.world.spawnEntity(itemEntity);
					playerEntity.world.spawnEntity(new ExperienceOrbEntity(playerEntity.world, playerEntity.getX(), playerEntity.getY() + (double)0.5F, playerEntity.getZ() + (double)0.5F, this.random.nextInt(6) + 1));
					if (itemStack.isIn(ItemTags.FISHES)) {
						playerEntity.increaseStat(Stats.FISH_CAUGHT, 1);
					}
				}

				i = 1;
			}

			if (this.onGround) {
				i = 2;
			}

			this.discard();
			return i;
		} else {
			return 0;
		}
	}

	public void setBootyLevel(int level){this.booty = level;}
}
