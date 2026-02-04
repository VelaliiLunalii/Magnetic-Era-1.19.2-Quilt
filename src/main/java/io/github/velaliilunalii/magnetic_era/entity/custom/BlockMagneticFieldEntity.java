package io.github.velaliilunalii.magnetic_era.entity.custom;

import io.github.velaliilunalii.magnetic_era.entity.ModEntities;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import io.github.velaliilunalii.magnetic_era.particle.ModParticles;
import io.github.velaliilunalii.magnetic_era.particle.effect.MagneticBeamParticleEffect;
import io.github.velaliilunalii.magnetic_era.data.ModTrackedData;
import io.github.velaliilunalii.magnetic_era.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity.FieldFeatures.TemperatureFeature;

import java.util.Random;

public class BlockMagneticFieldEntity extends MagneticFieldEntity {
	private static final TrackedData<Integer> DIRECTION_ID = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DIRECTION_LENGTH = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> SIDE_LENGTH = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> CENTER_STRENGTH = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> COIL_AMOUNT = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> IS_RESONATOR = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> PARTICLES_ENABLED = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<TemperatureFeature> TEMPERATURE_FEATURE = DataTracker.registerData(BlockMagneticFieldEntity.class, ModTrackedData.TEMPERATURE_FEATURE);
	private static final TrackedData<Boolean> CALIBRATED = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private float xLength;
	private float yLength;
	private float zLength;

	public BlockMagneticFieldEntity(EntityType<BlockMagneticFieldEntity> blockMagneticFieldEntityEntityType, World world) {
		super(blockMagneticFieldEntityEntityType, world);
	}

	public BlockMagneticFieldEntity(World world, Vec3d pos, float strength, Direction direction, int directionLength, int sideLength, float centerStrength, int coilAmount, boolean isResonator, TemperatureFeature temperatureFeature, boolean calibrated) {
		super(ModEntities.BLOCK_MAGNETIC_FIELD, world, pos, strength, 100);

		this.dataTracker.set(DIRECTION_ID, direction == null ? Direction.UP.getId() : direction.getId());
		this.dataTracker.set(DIRECTION_LENGTH, directionLength);
		this.dataTracker.set(SIDE_LENGTH, sideLength);
		this.dataTracker.set(CENTER_STRENGTH, centerStrength);
		this.dataTracker.set(COIL_AMOUNT, coilAmount);
		this.dataTracker.set(IS_RESONATOR, isResonator);
		this.dataTracker.set(PARTICLES_ENABLED, true);
		this.dataTracker.set(TEMPERATURE_FEATURE, temperatureFeature);
		this.dataTracker.set(CALIBRATED, calibrated);

		updateDimensions();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(DIRECTION_ID, Direction.UP.getId());
		this.dataTracker.startTracking(DIRECTION_LENGTH, 1);
		this.dataTracker.startTracking(SIDE_LENGTH, 1);
		this.dataTracker.startTracking(CENTER_STRENGTH, 0.0f);
		this.dataTracker.startTracking(COIL_AMOUNT, 4);
		this.dataTracker.startTracking(IS_RESONATOR, false);
		this.dataTracker.startTracking(PARTICLES_ENABLED, true);
		this.dataTracker.startTracking(TEMPERATURE_FEATURE, TemperatureFeature.TEMPERATE);
		this.dataTracker.startTracking(CALIBRATED, false);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.set(DIRECTION_ID, nbt.getInt("Direction"));
		this.dataTracker.set(DIRECTION_LENGTH, nbt.getInt("DirectionLength"));
		this.dataTracker.set(SIDE_LENGTH, nbt.getInt("SideLength"));
		this.dataTracker.set(CENTER_STRENGTH, nbt.getFloat("CenterStrength"));
		this.dataTracker.set(COIL_AMOUNT, nbt.getInt("CoilAmount"));
		this.dataTracker.set(IS_RESONATOR, nbt.getBoolean("isResonator"));
		this.dataTracker.set(PARTICLES_ENABLED, nbt.getBoolean("ParticlesEnabled"));
		this.dataTracker.set(TEMPERATURE_FEATURE, TemperatureFeature.getTemperatureFeature(nbt.getInt("TemperatureFeature")));
		this.dataTracker.set(CALIBRATED, nbt.getBoolean("Calibrated"));
		updateDimensions();
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Direction", this.dataTracker.get(DIRECTION_ID));
		nbt.putInt("DirectionLength", this.dataTracker.get(DIRECTION_LENGTH));
		nbt.putInt("SideLength", this.dataTracker.get(SIDE_LENGTH));
		nbt.putFloat("CenterStrength", this.dataTracker.get(CENTER_STRENGTH));
		nbt.putInt("CoilAmount", this.dataTracker.get(COIL_AMOUNT));
		nbt.putBoolean("isResonator", this.dataTracker.get(IS_RESONATOR));
		nbt.putBoolean("ParticlesEnabled", this.dataTracker.get(PARTICLES_ENABLED));
		nbt.putInt("TemperatureFeature", this.dataTracker.get(TEMPERATURE_FEATURE).Id());
		nbt.putBoolean("Calibrated", this.dataTracker.get(CALIBRATED));
	}

	private void updateDimensions() {
		Direction direction = getDirection();
		int directionLength = getDirectionLength();
		int sideLength = getSideLength();

		xLength = direction.getAxis() == Direction.Axis.X ? directionLength : sideLength;
		yLength = direction.getAxis() == Direction.Axis.Y ? directionLength : sideLength;
		zLength = direction.getAxis() == Direction.Axis.Z ? directionLength : sideLength;
	}

	public Direction getDirection() {
		return Direction.byId(this.dataTracker.get(DIRECTION_ID));
	}

	public int getDirectionLength() {
		return this.dataTracker.get(DIRECTION_LENGTH);
	}

	public int getSideLength() {
		return this.dataTracker.get(SIDE_LENGTH);
	}

	public float getCenterStrength() {
		return this.dataTracker.get(CENTER_STRENGTH);
	}

	public int getCoilAmount() {
		return this.dataTracker.get(COIL_AMOUNT);
	}

	public boolean isResonator() {
		return this.dataTracker.get(IS_RESONATOR);
	}

	public TemperatureFeature getTemperatureFeature() {
		return this.dataTracker.get(TEMPERATURE_FEATURE);
	}

	public boolean isCalibrated() {
		return this.dataTracker.get(CALIBRATED);
	}

	public float getXLength() {
		return this.xLength;
	}

	public float getYLength() {
		return this.yLength;
	}

	public float getZLength() {
		return this.zLength;
	}

	public void disableParticles(){
		this.dataTracker.set(PARTICLES_ENABLED, false);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.age == 0) {
			updateDimensions();
		}

		if (world instanceof ServerWorld serverWorld && this.dataTracker.get(PARTICLES_ENABLED)) {
			spawnParticle(serverWorld);
			if (this.age == 5) {
				Random random = new Random();
				if (random.nextFloat() < 0.25)
					world.playSound((PlayerEntity) null, getPos().getX(), getPos().getY(), getPos().getZ(),
						ModSounds.MAGNETIC_BUZZ, SoundCategory.PLAYERS,
						1.2f + (random.nextFloat() * 0.2f),
						0.9f + (random.nextFloat() * 0.2f));
			}
		}
	}

	public void spawnParticle(ServerWorld serverWorld) {
		if (isCalibrated()) {
			if (age % (20) == 0) {
				Vec3d direction = Vec3d.of(getDirection().getVector());
				float xLength = getXLength();
				float yLength = getYLength();
				float zLength = getZLength();
				Vec3d startPos = getPos().subtract(direction.multiply(xLength / 2, yLength / 2, zLength / 2)).add(0, 0.25, 0);

				float scale = 0.5f * ((float) getCoilAmount() /4);
				int maxAge = 20 * getDirectionLength();
				MagneticBeamParticleEffect particleEffect = new MagneticBeamParticleEffect(scale, maxAge,
					getDirection().equals(Direction.EAST) || getDirection().equals(Direction.UP) || getDirection().equals(Direction.SOUTH));

				serverWorld.spawnParticles(
					particleEffect,
					startPos.x, startPos.y, startPos.z,
					0,
					direction.x/20,
					direction.y/20,
					direction.z/20,
					1
				);
			}
		}
		else if (age % (80 / getCoilAmount()) == 0) {
			Vec3d direction = Vec3d.of(getDirection().getVector());
			Vec3d startPos = getPos().subtract(direction.multiply(xLength / 2, yLength / 2, zLength / 2)).add(0, 0.25, 0);
			Random random = new Random();
			double x = Math.abs(direction.x) != 1 ? (random.nextFloat() - 0.5) * xLength : 0;
			double y = Math.abs(direction.y) != 1 ? (random.nextFloat() - 0.5) * yLength : 0;
			double z = Math.abs(direction.z) != 1 ? (random.nextFloat() - 0.5) * zLength : 0;
			Vec3d randomStartPos = startPos.add(x, y, z);
			double length = Math.abs(direction.x * xLength) + Math.abs(direction.y * yLength) + Math.abs(direction.z * zLength);
			serverWorld.spawnParticles(ModParticles.MAGNETIC_FIELD_PARTICLE,
				randomStartPos.x, randomStartPos.y, randomStartPos.z, 0,
				(length * direction.x) / 30, (length * direction.y) / 30, (length * direction.z) / 30, 1);
		}
	}


	@Override
	public Box getEffectBox() {
		Vec3d startPos = this.getPos().subtract(xLength / 2, yLength / 2, zLength / 2);
		Vec3d endPos = this.getPos().add(xLength / 2, yLength / 2, zLength / 2);
		return new Box(startPos, endPos);
	}

	@Override
	public void applyEffects(Entity entity) {
		Direction.Axis axis = getDirection().getAxis();
		Vec3d entityToField = getPos().subtract(entity.getPos()).normalize();
		entityToField = entityToField.multiply(
			axis.equals(Direction.Axis.X) ? 0 : 1,
			axis.equals(Direction.Axis.Y) ? 0 : 1,
			axis.equals(Direction.Axis.Z) ? 0 : 1);
		Vec3d centerStrength = entityToField.multiply(getCenterStrength()).multiply(isResonator() ? 3 : 1);
		Vec3d directionVector = Vec3d.of(getDirection().getVector());
		Vec3d strength = directionVector.multiply(getStrength()).add(centerStrength);

		if (entity instanceof ItemEntity itemEntity){
			ItemStack itemStack = itemEntity.getStack();
			if (itemStack.isOf(Items.IRON_INGOT)){
				NbtCompound nbtComponent = itemStack.getOrCreateNbt();
				int magnetizing = nbtComponent.getInt("Magnetizing");
				if (magnetizing > 200) {
					nbtComponent.putInt("Magnetizing", 0);
					itemStack.split(1);
					world.spawnEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), ModItems.PHASE_INGOT.getDefaultStack()));
				}
				else nbtComponent.putInt("Magnetizing", magnetizing + 1);
			}
		}

		if (isCalibrated()){
			strength = strength.add(0, entity instanceof LivingEntity ? 0.08 : 0.04, 0);

			entity.setVelocity(strength);
			entity.fallDistance = 0;
		}else {
			entity.setVelocity(entity.getVelocity().add(strength));
		}

		if(entity instanceof LivingEntity) {
			TemperatureFeature temperatureFeature = getTemperatureFeature();
			if (world.getTime() % 20 == 0 && temperatureFeature.equals(TemperatureFeature.HOT)) entity.setOnFireFor(2);
			if (temperatureFeature.equals(TemperatureFeature.COLD)){
				boolean isImmune = false;
				for (ItemStack itemStack : entity.getArmorItems()){
					if (itemStack != null && itemStack.getItem() instanceof ArmorItem armorItem &&
						armorItem.getMaterial() == ArmorMaterials.LEATHER)
						isImmune = true;
				}
				if (!isImmune) {
					entity.setInPowderSnow(true);
					entity.setFrozenTicks(entity.getFrozenTicks() + 3);
				}
			}
		}
	}

	public class FieldFeatures{
		public enum TemperatureFeature{
			COLD(-1),
			TEMPERATE(0),
			HOT(1);

			private int temperatureId;

			TemperatureFeature(int temperatureId) {this.temperatureId = temperatureId;}

			public int Id(){return temperatureId;}
			public static TemperatureFeature getTemperatureFeature(int id){
				if (id == -1) return COLD;
				if (id == 1) return HOT;
				else return TEMPERATE;
			}
		}

		private TemperatureFeature temperatureFeature;

		public FieldFeatures(TemperatureFeature temperatureFeature){
			this.temperatureFeature = temperatureFeature;
		}

		public TemperatureFeature getTemperatureFeature() {
			return temperatureFeature;
		}
		public void setTemperatureFeature(TemperatureFeature temperatureFeature) {
			this.temperatureFeature = temperatureFeature;
		}
	}
}
