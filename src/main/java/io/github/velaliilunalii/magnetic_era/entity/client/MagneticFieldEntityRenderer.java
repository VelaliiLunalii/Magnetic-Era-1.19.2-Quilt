package io.github.velaliilunalii.magnetic_era.entity.client;

import io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFieldEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MagneticFieldEntityRenderer extends EntityRenderer<MagneticFieldEntity> {

	public MagneticFieldEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public Identifier getTexture(MagneticFieldEntity entity) {
		return null;
	}

	@Override
	public void render(MagneticFieldEntity entity, float yaw, float tickDelta, MatrixStack matrices,
					   VertexConsumerProvider vertexConsumers, int light) {
		if (MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes()) {
			super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		}
	}
}
