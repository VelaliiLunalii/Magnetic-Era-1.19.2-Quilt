package io.github.velaliilunalii.magnetic_era.entity.custom;

import io.github.velaliilunalii.magnetic_era.particle.effect.MagneticBeamParticleEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CalibratedBlockMagneticFieldEntity extends BlockMagneticFieldEntity {
	public CalibratedBlockMagneticFieldEntity(EntityType<BlockMagneticFieldEntity> blockMagneticFieldEntityEntityType, World world) {
		super(blockMagneticFieldEntityEntityType, world);
	}

	public CalibratedBlockMagneticFieldEntity(World world, Vec3d pos, float strength, Direction direction, int directionLength, int sideLength, float centerStrength, int coilAmount, int typeId) {
		super(world, pos, strength, direction, directionLength, sideLength, centerStrength, coilAmount, typeId);
	}

	@Override
	public void spawnParticle(ServerWorld serverWorld) {
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

	@Override
	public void applyVelocity(Entity entity, float appliedStrength) {
		Vec3d entityToField = getPos().subtract(entity.getPos()).normalize();
		Vec3d centerPull = entityToField.multiply(getCenterStrength());

		Direction direction = getDirection();
		Vec3d directionVector = Vec3d.of(direction.getVector());

		entity.setVelocity(directionVector.multiply(appliedStrength).add(centerPull).add(0, 0.08, 0));
		entity.fallDistance = 0;
	}
}
