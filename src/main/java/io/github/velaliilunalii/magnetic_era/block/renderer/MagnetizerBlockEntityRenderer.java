package io.github.velaliilunalii.magnetic_era.block.renderer;

import io.github.velaliilunalii.magnetic_era.block.block_entity.MagnetizerBlockEntity;
import io.github.velaliilunalii.magnetic_era.block.custom.MagnetizerBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class MagnetizerBlockEntityRenderer  implements BlockEntityRenderer<MagnetizerBlockEntity> {
	private static final float SCALE = 0.450F;
	private final ItemRenderer itemRenderer;

	public MagnetizerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.itemRenderer = ctx.getItemRenderer();
	}

	public void render(MagnetizerBlockEntity magnetizerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		Direction direction = magnetizerBlockEntity.getCachedState().get(MagnetizerBlock.FACING);
		DefaultedList<ItemStack> defaultedList = magnetizerBlockEntity.getItemsBeingMagnetized();
		int k = (int)magnetizerBlockEntity.getPos().asLong();

		for(int l = 0; l < defaultedList.size(); ++l) {
			ItemStack itemStack = (ItemStack)defaultedList.get(l);
			if (itemStack != ItemStack.EMPTY) {
				matrixStack.push();

				matrixStack.translate((double)0.5F, (double)0.5F, (double)0.5F);
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) (magnetizerBlockEntity.getWorld().getTime() * 2 % 360)));
				matrixStack.scale(SCALE, SCALE, SCALE);
				this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider, k);

				matrixStack.pop();
			}
		}

	}
}
