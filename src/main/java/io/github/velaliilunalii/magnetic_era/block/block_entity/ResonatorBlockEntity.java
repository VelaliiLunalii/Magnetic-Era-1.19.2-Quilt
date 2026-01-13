package io.github.velaliilunalii.magnetic_era.block.block_entity;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity;
import io.github.velaliilunalii.magnetic_era.entity.custom.CalibratedBlockMagneticFieldEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;

import static io.github.velaliilunalii.magnetic_era.block.custom.capacitor.CopperCapacitorBlock.ENABLED;
import static io.github.velaliilunalii.magnetic_era.block.custom.ResonatorBlock.FACING;
import static io.github.velaliilunalii.magnetic_era.block.custom.ResonatorBlock.LIT;
import static io.github.velaliilunalii.magnetic_era.entity.custom.BlockMagneticFieldEntity.FieldType.*;
import static io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFieldEntity.*;

public class ResonatorBlockEntity extends BlockEntity implements BlockEntityTicker<ResonatorBlockEntity> {
	public ResonatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.RESONATOR_BLOCK_ENTITY, pos, state);
	}

	@Override
	public void tick(World world, BlockPos blockPos, BlockState blockState, ResonatorBlockEntity blockEntity) {
		if (this.world == null) return;
		if (!this.world.isClient && this.world.getTime() % 100 == 10) {
			BlockPos startPos = pos.subtract(new Vec3i(100, 100, 100));
			BlockPos endPos = pos.add(new Vec3i(100, 100, 100));
			Box box = new Box(startPos, endPos);
			List<BlockMagneticFieldEntity> entityList = world.getEntitiesByClass(
				BlockMagneticFieldEntity.class,
				box,
				Entity -> isMagneticAffected(Entity, pos)
			);

			boolean lit = (Boolean)blockState.get(LIT);
			if ((!lit && (!entityList.isEmpty() || world.isReceivingRedstonePower(pos))) || (lit && entityList.isEmpty() && !world.isReceivingRedstonePower(pos))){
				world.setBlockState(pos, (BlockState)blockState.cycle(LIT), 2);
				world.updateNeighborsAlways(pos, blockState.getBlock());
			}

			if (!entityList.isEmpty()){
				BlockMagneticFieldEntity closest = entityList.get(0);
				for (BlockMagneticFieldEntity entity : entityList){
					if (entity.getPos().distanceTo(Vec3d.of(pos)) < closest.getPos().distanceTo(Vec3d.of(pos))) closest = entity;
				}

				Direction direction = blockState.get(FACING);
				Direction originalFieldDirection = closest.getDirection();
				Direction finalDirection = direction == originalFieldDirection.getOpposite()
					? direction
					: direction.getOpposite();
				if (originalFieldDirection.getAxis() == direction.getAxis()) finalDirection = finalDirection.getOpposite();

				int directionLength = isAGenerator(closest.getFieldType()) ? (closest.getDirectionLength() + 1) * 4 : closest.getDirectionLength();
				Vec3d fieldPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ())
					.add(0.5, 0.25, 0.5)
					.relative(direction, ((double) directionLength /2)+0.5);

				BlockMagneticFieldEntity magneticFieldEntity = isCalibrated(closest.getFieldType())
					? new CalibratedBlockMagneticFieldEntity(world, fieldPos, closest.getStrength(), finalDirection, directionLength, 1, closest.getCenterStrength(), 4, CALIBRATED_RESONATOR.getTypeId())
					: new BlockMagneticFieldEntity(world, fieldPos, closest.getStrength(), finalDirection, directionLength, 1, closest.getCenterStrength(), 4, RESONATOR.getTypeId());
				if (!blockState.get(ENABLED)) magneticFieldEntity.disableParticles();
				world.spawnEntity(magneticFieldEntity);
			}
		}
	}
}
