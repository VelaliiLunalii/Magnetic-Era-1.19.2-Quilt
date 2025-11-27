package io.github.velaliilunalii.vegetarianism.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.quiltmc.loader.api.ModContainer;

import static io.github.velaliilunalii.vegetarianism.block.ModBlocks.SOY_CROP;
import static io.github.velaliilunalii.vegetarianism.block.ModBlocks.TOFU;

public class ModBlocksClient {
	public static void register(ModContainer mod) {
		BlockRenderLayerMap.INSTANCE.putBlock(SOY_CROP, RenderLayer.getCutoutMipped());
		BlockRenderLayerMap.INSTANCE.putBlock(TOFU, RenderLayer.getCutoutMipped());
	}
}
