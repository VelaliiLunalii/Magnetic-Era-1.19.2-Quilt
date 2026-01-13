package io.github.velaliilunalii.magnetic_era.entity.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.velaliilunalii.magnetic_era.MagneticEra;
import io.github.velaliilunalii.magnetic_era.entity.custom.MagneticFishingRodBobberEntity;
import io.github.velaliilunalii.magnetic_era.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

public class MagneticFishingRodBobberRenderer extends EntityRenderer<MagneticFishingRodBobberEntity> {
	private static final Identifier TEXTURE = new Identifier(MagneticEra.MOD_ID, "textures/entity/magnetic_fishing_rod_bobber.png");
	private static final RenderLayer LAYER;
	private static final double BOBBING_VIEW_SCALE = (double)960.0F;

	public MagneticFishingRodBobberRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	public void render(MagneticFishingRodBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
		if (playerEntity != null) {
			matrixStack.push();
			matrixStack.push();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.multiply(this.dispatcher.getRotation());
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			MatrixStack.Entry entry = matrixStack.peek();
			Matrix4f matrix4f = entry.getModel();
			Matrix3f matrix3f = entry.getNormal();
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
			vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 0, 0, 1);
			vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 0, 1, 1);
			vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 1, 1, 0);
			vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 1, 0, 0);
			matrixStack.pop();
			int j = playerEntity.getMainArm() == Arm.RIGHT ? 1 : -1;
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (!itemStack.isOf(ModItems.MAGNETIC_FISHING_ROD)) {
				j = -j;
			}

			float h = playerEntity.getHandSwingProgress(g);
			float k = MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
			float l = MathHelper.lerp(g, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * ((float)Math.PI / 180F);
			double d = (double)MathHelper.sin(l);
			double e = (double)MathHelper.cos(l);
			double m = (double)j * 0.35;
			double n = 0.8;
			double o;
			double p;
			double q;
			float r;
			if ((this.dispatcher.gameOptions == null || this.dispatcher.gameOptions.getPerspective().isFirstPerson()) && playerEntity == MinecraftClient.getInstance().player) {
				double s = (double)960.0F / (double)(Integer)this.dispatcher.gameOptions.getFov().get();
				Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition((float)j * 0.525F, -0.1F);
				vec3d = vec3d.multiply(s);
				vec3d = vec3d.rotateY(k * 0.5F);
				vec3d = vec3d.rotateX(-k * 0.7F);
				o = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) + vec3d.x;
				p = MathHelper.lerp((double)g, playerEntity.prevY, playerEntity.getY()) + vec3d.y;
				q = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) + vec3d.z;
				r = playerEntity.getStandingEyeHeight();
			} else {
				o = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) - e * m - d * 0.8;
				p = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.getY() - playerEntity.prevY) * (double)g - 0.45;
				q = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) - d * m + e * 0.8;
				r = playerEntity.isInSneakingPose() ? -0.1875F : 0.0F;
			}

			double s = MathHelper.lerp((double)g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
			double t = MathHelper.lerp((double)g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + (double)0.25F;
			double u = MathHelper.lerp((double)g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
			float v = (float)(o - s);
			float w = (float)(p - t) + r;
			float x = (float)(q - u);
			VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
			MatrixStack.Entry entry2 = matrixStack.peek();
			int y = 16;

			for(int z = 0; z <= 16; ++z) {
				drawArcSection(v, w, x, vertexConsumer2, entry2, percentage(z, 16), percentage(z + 1, 16));
			}

			matrixStack.pop();
			super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}

	private static float percentage(int value, int max) {
		return (float)value / (float)max;
	}

	private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
		buffer.vertex(matrix, x - 0.5F, (float)y - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)u, (float)v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
	}

	private static void drawArcSection(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry normal, float startPercent, float endPercent) {
		float f = x * startPercent;
		float g = y * (startPercent * startPercent + startPercent) * 0.5F + 0.25F;
		float h = z * startPercent;
		float i = x * endPercent - f;
		float j = y * (endPercent * endPercent + endPercent) * 0.5F + 0.25F - g;
		float k = z * endPercent - h;
		float l = MathHelper.sqrt(i * i + j * j + k * k);
		i /= l;
		j /= l;
		k /= l;
		buffer.vertex(normal.getModel(), f, g, h).color(0, 0, 0, 255).normal(normal.getNormal(), i, j, k).next();
	}

	public Identifier getTexture(MagneticFishingRodBobberEntity fishingBobberEntity) {
		return TEXTURE;
	}

	static {
		LAYER = RenderLayer.getEntityCutout(TEXTURE);
	}
}
