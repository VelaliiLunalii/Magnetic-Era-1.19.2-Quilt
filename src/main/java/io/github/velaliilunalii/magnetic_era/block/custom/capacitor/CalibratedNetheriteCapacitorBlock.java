package io.github.velaliilunalii.magnetic_era.block.custom.capacitor;

import io.github.velaliilunalii.magnetic_era.block.ModBlockEntities;
import io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor.CalibratedNetheriteCapacitorBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.block_entity.capacitor.IronCapacitorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class CalibratedNetheriteCapacitorBlock extends CopperCapacitorBlock {

	public CalibratedNetheriteCapacitorBlock(Settings settings, int maxCharge) {
		super(settings, maxCharge);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CalibratedNetheriteCapacitorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.CALIBRATED_NETHERITE_CAPACITOR_BLOCK_ENTITY,
			(world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity));
	}
}
