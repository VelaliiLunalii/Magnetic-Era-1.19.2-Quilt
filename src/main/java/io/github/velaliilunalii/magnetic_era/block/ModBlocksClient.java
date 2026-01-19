package io.github.velaliilunalii.magnetic_era.block;

import io.github.velaliilunalii.magnetic_era.block.renderer.MagnetizerBlockEntityRenderer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.quiltmc.loader.api.ModContainer;

import static io.github.velaliilunalii.magnetic_era.block.ModBlocks.*;

public class ModBlocksClient {
	public static void register(ModContainer mod) {
		BlockRenderLayerMap.INSTANCE.putBlock(COPPER_COIL, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(WEAK_COPPER_COIL, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(STRONG_COPPER_COIL, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(PHASE_COIL, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(WEAK_PHASE_COIL, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(STRONG_PHASE_COIL, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(MAGNETIC_CORE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(WEAK_MAGNETIC_CORE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(STRONG_MAGNETIC_CORE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(COPPER_WIRE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(PHASE_WIRE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(RESONATOR, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(MAGNETIZER, RenderLayer.getTranslucent());
		BlockEntityRendererFactories.register(
			ModBlockEntities.MAGNETIZER_BLOCK_ENTITY,
			MagnetizerBlockEntityRenderer::new
		);
	}
}
