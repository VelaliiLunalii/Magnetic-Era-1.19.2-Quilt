package io.github.velaliilunalii.magnetic_era.block.custom.crate;

import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

import java.util.List;

public class IronCrateBlock extends Block implements ICrateBlock {
	private final Identifier tableToUse = new Identifier(MagneticEra.MOD_ID, "gameplay/iron_crate");

	public IronCrateBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_METAL_BREAK, SoundCategory.NEUTRAL, 1F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		if(world.isClient) {
			return ActionResult.success(true);
		}else{
			ItemStack mainHandStack = player.getMainHandStack();
			if (mainHandStack.isOf(Items.NETHER_STAR)) {
				world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.NEUTRAL, 1F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
				world.setBlockState(pos, ModBlocks.WEIGHTED_COMPANION_CRATE.getDefaultState(), Block.NOTIFY_ALL);
			}else {
				LootTable lootTable = world.getServer().getLootManager().getTable(tableToUse);
				LootContext.Builder builder = new LootContext.Builder((ServerWorld) world).parameter(LootContextParameters.ORIGIN,
					new Vec3d(pos.getX(), pos.getY(), pos.getZ())).random(RandomGenerator.createLegacy()).luck(player.getLuck());
				List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.CHEST));

				for (ItemStack itemStack : list) {
					ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
					world.spawnEntity(itemEntity);
				}

				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
			}
			return ActionResult.CONSUME;
		}
	}
}
