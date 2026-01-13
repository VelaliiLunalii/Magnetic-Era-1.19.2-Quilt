package io.github.velaliilunalii.magnetic_era.item.custom;

import io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFieldEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.List;

public class MagneticArmorItem extends ArmorItem {
	public MagneticArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if(!world.isClient){
			boolean isEquiped = false;
			for (ItemStack itemStack : entity.getArmorItems()){
				if (itemStack.equals(stack)) isEquiped = true;
			}

			boolean hasNoGravity = entity.hasNoGravity();
			if (isEquiped){
				BlockPos startPos = entity.getBlockPos().subtract(new Vec3i(100, 100, 100));
				BlockPos endPos = entity.getBlockPos().add(new Vec3i(100, 100, 100));
				Box box = new Box(startPos, endPos);
				List<MagneticFieldEntity> entityList = world.getEntitiesByClass(
					MagneticFieldEntity.class,
					box,
					Entity -> isMagneticAffected(Entity, entity.getPos())
				);
				if (entityList.isEmpty() && hasNoGravity || !entityList.isEmpty() && !hasNoGravity)
					entity.setNoGravity(!hasNoGravity);
			}else{
				if (hasNoGravity)
					entity.setNoGravity(false);
			}
		}
	}

	private boolean isMagneticAffected(MagneticFieldEntity magneticField, Vec3d pos){
		return magneticField.getEffectBox().contains(pos);
	}

//	public static int getCooldown(ItemStack stack) {
//		NbtCompound nbtCompound = stack.getNbt();
//		if (nbtCompound == null) return 0;
//		return nbtCompound.getInt("Cooldown");
//	}
//
//	public static void setCooldown(ItemStack stack, int cooldown) {
//		NbtCompound nbtCompound = stack.getOrCreateNbt();
//		nbtCompound.putInt("Cooldown", cooldown);
//	}
//
//	public static void decrementCooldown(ItemStack stack, int decrement) {
//		NbtCompound nbtCompound = stack.getOrCreateNbt();
//		nbtCompound.putInt("Cooldown", getCooldown(stack) - decrement);
//	}
}
