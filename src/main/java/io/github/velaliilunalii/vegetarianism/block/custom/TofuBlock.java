package io.github.velaliilunalii.vegetarianism.block.custom;

import io.github.velaliilunalii.vegetarianism.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class TofuBlock extends Block {
	public static final int MAX_BITES = 1;
	public static final IntProperty BITES = IntProperty.of("bites", 0, 1);
	public static final int DEFAULT_COMPARATOR_OUTPUT;
	protected static final VoxelShape[] BITES_TO_SHAPE;

    public TofuBlock(Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(BITES, 0));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BITES_TO_SHAPE[(Integer)state.get(BITES)];
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand
			hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);

		if (world instanceof ServerWorld serverWorld) {
			serverWorld.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_STEP, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}

		if (world.isClient) {
			if (takeSlice(world, pos, state, player, hand).isAccepted()) {
				return ActionResult.SUCCESS;
			}

			if (itemStack.isEmpty()) {
				return ActionResult.CONSUME;
			}
		}

		return takeSlice(world, pos, state, player, hand);
	}

	protected static ActionResult takeSlice(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, Hand
			hand) {
		int i = (Integer)state.get(BITES);
		if (i < MAX_BITES) {
			world.setBlockState(pos, (BlockState)state.with(BITES, i + 1), 3);
		} else {
			world.removeBlock(pos, false);
			world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
		}

		if(!world.isClient()){
			ItemStack slice = new ItemStack(ModItems.TOFU_SLICE);
			PlayerInventory inv = player.getInventory();
			if(player.getStackInHand(hand).isEmpty()){
				inv.setStack(inv.selectedSlot, slice);
			}else{
				inv.insertStack(slice);
			}
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(new Property[]{BITES});
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return getComparatorOutput((Integer)state.get(BITES));
	}

	public static int getComparatorOutput(int bites) {
		return (2-bites)*7;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	static {
		DEFAULT_COMPARATOR_OUTPUT = getComparatorOutput(0);
		BITES_TO_SHAPE = new VoxelShape[]{
			Block.createCuboidShape((double)1.0F, (double)0.0F, (double)1.0F, (double)15.0F, (double)8.0F, (double)15.0F),
			Block.createCuboidShape((double)9.0F, (double)0.0F, (double)1.0F, (double)15.0F, (double)8.0F, (double)15.0F)};
	}
}
