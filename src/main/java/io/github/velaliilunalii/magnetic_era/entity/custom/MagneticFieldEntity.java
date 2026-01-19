package io.github.velaliilunalii.magnetic_era.entity.custom;

import io.github.velaliilunalii.magnetic_era.item.ModItems;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MagneticFieldEntity extends Entity {
	private int maxAge = 100;
	private float strength = 0.5F;
	public final int maxVelocity = 12;

	public MagneticFieldEntity(EntityType<BlockMagneticFieldEntity> blockMagneticFieldEntityEntityType, World world) {
		super(blockMagneticFieldEntityEntityType, world);
	}

	public MagneticFieldEntity(EntityType<?> type, World world, Vec3d pos, float strength, int maxAge) {
		super(type, world);
		this.noClip = true;
		this.setPosition(pos);
		this.strength = strength;
		this.maxAge = maxAge;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.age >= maxAge) this.kill();

		List<Entity> entityList = getEntityList();
		for (Entity entity : entityList) {
			float appliedStrength = strength;
			if (entity instanceof ProjectileEntity || entity instanceof AbstractMinecartEntity) appliedStrength *= 2;
			double speed = entity.getVelocity().length();
			if (speed < maxVelocity) applyVelocity(entity, appliedStrength);

			//TODO Cross mod compatibility
//			if (entity instanceof CopperCoinProjectileEntity coin) {
//				coin.setGravity(coin.getGravity() / 2);
//				coin.setMagneticFieldIncrease(true);
//			}
//			if (entity instanceof IronCoinProjectileEntity coin) {
//				coin.setGravity(coin.getGravity() / 2);
//				coin.setMagneticFieldIncrease(true);
//			}
//			if (entity instanceof GoldCoinProjectileEntity coin) {
//				coin.setGravity(coin.getGravity()/2);
//				coin.setMagneticFieldIncrease(true);
//			}
			entity.velocityModified = true;
			entity.velocityDirty = true;
		}
	}

	public List<Entity> getEntityList(){
		Box box = getEffectBox();
		List<Entity> entityList = world.getEntitiesByClass(
			Entity.class,
			box,
			this::isCorrectEntity
		);
		return entityList;
	}

	public int getMaxAge() {
		return this.maxAge;
	}

	public void applyVelocity(Entity entity, float appliedStrength){
	}

	public boolean isCorrectEntity(Entity entity){
		//to override and add
//		if (entity == this.owner || ((owner != null && owner.hasVehicle() && owner.getVehicle() == entity))) return false;

//		for (ItemStack itemStack : entity.getArmorItems()) {
//			if (itemStack.getItem().equals(ModItems.MAGNETIC_BOOTS)) return false;
//		}
		if (entity instanceof ProjectileEntity || entity instanceof ItemEntity || entity instanceof AbstractMinecartEntity)
			return true;
		for (ItemStack itemStack : entity.getArmorItems()){
			if (itemStack != null && itemStack.getItem() instanceof ArmorItem armorItem &&
				(armorItem.getMaterial() == ArmorMaterials.IRON || armorItem.getMaterial() == ArmorMaterials.CHAIN
					|| armorItem.getMaterial() == ArmorMaterials.NETHERITE))
				return true;
		}
		return  false;
	}

	//------------------------------Static-------------------------------------------

	public static boolean isMagneticAffected(MagneticFieldEntity magneticField, BlockPos pos){
		return magneticField.getEffectBox().contains(Vec3d.of(pos).add(0.5, 0.5, 0.5));
	}

	public static boolean containsOppositeFields(List<MagneticFieldEntity> entityList){
		if (entityList.size() < 2) return false;
		for (MagneticFieldEntity magneticField1 : entityList){
			if (magneticField1 instanceof BlockMagneticFieldEntity blockMagneticField1) {
				for (MagneticFieldEntity magneticField2 : entityList) {
					if (magneticField2 instanceof BlockMagneticFieldEntity blockMagneticField2) {
						if (blockMagneticField1 != blockMagneticField2 &&
							blockMagneticField1.getDirection().equals(blockMagneticField2.getDirection().getOpposite()))
							return true;
					}
				}
			}
		}
		return false;
	}

	public Box getEffectBox(){
		return null;
	}

	public float getStrength(){return strength;}

	//------------------------------Properties-------------------------------------------

	@Override
	public boolean doesRenderOnFire() {
		return false;
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void initDataTracker() {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
