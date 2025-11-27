package io.github.velaliilunalii.vegetarianism.block.custom;

import io.github.velaliilunalii.vegetarianism.Vegetarianism;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

import java.util.List;

public class CrateBlock extends Block {
	private final Identifier tableToUse = new Identifier(Vegetarianism.MODID, "gameplay/crate");

	public CrateBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(world.isClient) {
			//playsound
			player.sendMessage(Text.of("hello"), true);
			return ActionResult.success(true);
		}else{
			LootTable lootTable = world.getServer().getLootManager().getTable(tableToUse);
			LootContext.Builder builder = new LootContext.Builder((ServerWorld) world).parameter(LootContextParameters.ORIGIN,
				new Vec3d(pos.getX(), pos.getY(), pos.getZ())).random(RandomGenerator.createLegacy()).luck(player.getLuck());
			List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.CHEST));

			for (ItemStack itemStack : list) {
				ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
				world.spawnEntity(itemEntity);
			}

			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
			return ActionResult.CONSUME;
		}

	}
}
