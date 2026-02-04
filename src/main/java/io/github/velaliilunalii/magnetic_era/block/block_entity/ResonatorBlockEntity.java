package io.github.velaliilunalii.magnetic_era.block.block_entity;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity;
import io.github.velaliilunalii.magnetic_era.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static io.github.velaliilunalii.magnetic_era.block.custom.CapacitorBlock.ENABLED;
import static io.github.velaliilunalii.magnetic_era.block.custom.ResonatorBlock.FACING;
import static io.github.velaliilunalii.magnetic_era.block.custom.ResonatorBlock.LIT;
import static io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFieldEntity.*;

public class ResonatorBlockEntity extends BlockEntity implements BlockEntityTicker<ResonatorBlockEntity> {
	private List<BlockMagneticFieldEntity> entityList;

	public ResonatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.RESONATOR_BLOCK_ENTITY, pos, state);
	}

	@Override
	public void tick(World world, BlockPos blockPos, BlockState blockState, ResonatorBlockEntity blockEntity) {
		if (this.world == null || this.world.isClient) return;

		if (this.world.getTime() % 20 == 5) {
			entityList = getBlockMagneticFields(world, pos);

			boolean lit = (Boolean)blockState.get(LIT);
			if ((!lit && (!entityList.isEmpty() || world.isReceivingRedstonePower(pos))) || (lit && entityList.isEmpty() && !world.isReceivingRedstonePower(pos))){
				world.setBlockState(pos, (BlockState)blockState.cycle(LIT), 2);
				world.updateNeighborsAlways(pos, blockState.getBlock());
				Random random = new Random();
				if (lit) world.playSound((PlayerEntity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					ModSounds.CAPACITOR_POWERING, SoundCategory.PLAYERS, 1.0F, 0.6f + (random.nextFloat() * 0.2f));
				else world.playSound((PlayerEntity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					ModSounds.CAPACITOR_POWERING, SoundCategory.PLAYERS, 1.0F, 1.0f + (random.nextFloat() * 0.2f));
			}
		}

		if (this.world.getTime() % 92 == 5 && entityList != null && !entityList.isEmpty()) {
			BlockMagneticFieldEntity closest = getClosest(entityList, pos);
			Direction direction = blockState.get(FACING);
			int directionLength = closest.isResonator() ? closest.getDirectionLength() : (closest.getDirectionLength() + 1) * 4;

			BlockMagneticFieldEntity magneticFieldEntity = new BlockMagneticFieldEntity(
				world,
				new Vec3d(pos.getX(), pos.getY(), pos.getZ())
					.add(0.5, 0.25, 0.5)
					.relative(direction, ((double) directionLength /2)+0.5),
				closest.getStrength(),
				getFieldDirection(closest, direction),
				directionLength,
				1,
				closest.getCenterStrength(),
				4,
				true,
				closest.getTemperatureFeature(),
				closest.isCalibrated());
			if (!blockState.get(ENABLED)) magneticFieldEntity.disableParticles();
			world.spawnEntity(magneticFieldEntity);

		}
	}

	public static List<BlockMagneticFieldEntity> getBlockMagneticFields(World world, BlockPos pos){
		BlockPos startPos = pos.subtract(new Vec3i(100, 100, 100));
		BlockPos endPos = pos.add(new Vec3i(100, 100, 100));
		Box box = new Box(startPos, endPos);
		return world.getEntitiesByClass(
			BlockMagneticFieldEntity.class,
			box,
			Entity -> isMagneticAffected(Entity, pos)
		);
	}

	public static BlockMagneticFieldEntity getClosest(List<BlockMagneticFieldEntity> entityList, BlockPos pos){
		BlockMagneticFieldEntity closest = entityList.get(0);
		for (BlockMagneticFieldEntity entity : entityList){
			if (entity.getPos().distanceTo(Vec3d.of(pos)) < closest.getPos().distanceTo(Vec3d.of(pos))) closest = entity;
		}
		return closest;
	}

	public static Direction getFieldDirection(BlockMagneticFieldEntity closest, Direction direction){
		Direction originalFieldDirection = closest.getDirection();
		Direction fieldDirection = direction == originalFieldDirection.getOpposite()
			? direction
			: direction.getOpposite();
		if (originalFieldDirection.getAxis() == direction.getAxis()) fieldDirection = fieldDirection.getOpposite();
		return fieldDirection;
	}
}
