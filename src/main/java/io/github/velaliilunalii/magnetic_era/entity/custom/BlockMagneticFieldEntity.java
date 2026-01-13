package io.github.velaliilunalii.magnetic_era.entity.custom;

import io.github.velaliilunalii.magnetic_era.entity.ModEntities;
import io.github.velaliilunalii.magnetic_era.particle.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class BlockMagneticFieldEntity extends MagneticFieldEntity {
	public enum FieldType{
		CAPACITOR(0),
		RESONATOR(1),
		CALIBRATED_CAPACITOR(2),
		CALIBRATED_RESONATOR(3);

		private final int typeId;

		FieldType(int typeId) {
			this.typeId = typeId;
		}

		public int getTypeId() {
			return typeId;
		}

		public static boolean isCalibrated(int typeId){
			return typeId == CALIBRATED_CAPACITOR.getTypeId() || typeId == CALIBRATED_RESONATOR.getTypeId();
		}

		public static boolean isAGenerator(int typeId){
			return typeId == CAPACITOR.getTypeId() || typeId == CALIBRATED_CAPACITOR.getTypeId();
		}

		public static FieldType getType(int typeId){
			if (typeId == CAPACITOR.getTypeId()) return CAPACITOR;
			if (typeId == RESONATOR.getTypeId()) return RESONATOR;
			if (typeId == CALIBRATED_CAPACITOR.getTypeId()) return CALIBRATED_CAPACITOR;
			if (typeId == CALIBRATED_RESONATOR.getTypeId()) return CALIBRATED_RESONATOR;
			return CAPACITOR;
		}
	}

	private static final TrackedData<Integer> DIRECTION_ID = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DIRECTION_LENGTH = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> SIDE_LENGTH = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> CENTER_STRENGTH = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> COIL_AMOUNT = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> FIELD_TYPE = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> PARTICLES_ENABLED = DataTracker.registerData(BlockMagneticFieldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private float xLength;
	private float yLength;
	private float zLength;

	public BlockMagneticFieldEntity(EntityType<BlockMagneticFieldEntity> blockMagneticFieldEntityEntityType, World world) {
		super(blockMagneticFieldEntityEntityType, world);
	}

	public BlockMagneticFieldEntity(World world, Vec3d pos, float strength, Direction direction, int directionLength, int sideLength, float centerStrength, int coilAmount, int typeId) {
		super(ModEntities.BLOCK_MAGNETIC_FIELD, world, pos, strength, 110);

		this.dataTracker.set(DIRECTION_ID, direction == null ? Direction.UP.getId() : direction.getId());
		this.dataTracker.set(DIRECTION_LENGTH, directionLength);
		this.dataTracker.set(SIDE_LENGTH, sideLength);
		this.dataTracker.set(CENTER_STRENGTH, centerStrength);
		this.dataTracker.set(COIL_AMOUNT, coilAmount);
		this.dataTracker.set(FIELD_TYPE, typeId);
		this.dataTracker.set(PARTICLES_ENABLED, true);

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
		this.dataTracker.startTracking(FIELD_TYPE, FieldType.CAPACITOR.getTypeId());
		this.dataTracker.startTracking(PARTICLES_ENABLED, true);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.set(DIRECTION_ID, nbt.getInt("Direction"));
		this.dataTracker.set(DIRECTION_LENGTH, nbt.getInt("DirectionLength"));
		this.dataTracker.set(SIDE_LENGTH, nbt.getInt("SideLength"));
		this.dataTracker.set(CENTER_STRENGTH, nbt.getFloat("CenterStrength"));
		this.dataTracker.set(COIL_AMOUNT, nbt.getInt("CoilAmount"));
		this.dataTracker.set(FIELD_TYPE, nbt.getInt("FieldType"));
		this.dataTracker.set(PARTICLES_ENABLED, nbt.getBoolean("ParticlesEnabled"));
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
		nbt.putInt("FieldType", this.dataTracker.get(FIELD_TYPE));
		nbt.putBoolean("ParticlesEnabled", this.dataTracker.get(PARTICLES_ENABLED));
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

	public int getFieldType() {
		return this.dataTracker.get(FIELD_TYPE);
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
		}
	}

	public void spawnParticle(ServerWorld serverWorld) {
		if (age % (80 / getCoilAmount()) == 0) {
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
	public void applyVelocity(Entity entity, float appliedStrength) {
		Vec3d entityToField = getPos().subtract(entity.getPos()).normalize();
		Vec3d centerPull = entityToField.multiply(getCenterStrength());

		Direction direction = getDirection();
		Vec3d directionVector = Vec3d.of(direction.getVector());

		entity.setVelocity(entity.getVelocity().add(directionVector.multiply(appliedStrength)).add(centerPull));
	}
}
