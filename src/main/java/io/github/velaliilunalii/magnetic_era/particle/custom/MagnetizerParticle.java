package io.github.velaliilunalii.magnetic_era.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;

public class MagnetizerParticle extends SpriteBillboardParticle {
	final SpriteProvider spriteProvider;
	private final Direction.Axis axis;
	private final float scaleIncrease = 1.05F;

	MagnetizerParticle(ClientWorld world, double x, double y, double z,
					   double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, velocityX, velocityY, velocityZ);
		this.spriteProvider = spriteProvider;

		this.maxAge = 15;
		this.scale = 0.25F;

		this.colorRed = 1;
		this.colorGreen = 1;
		this.colorBlue = 1;
		this.colorAlpha = 1F;
		this.collidesWithWorld = false;
		this.velocityMultiplier = 1F;
		this.gravityStrength = 0.0F;
		this.setSpriteForAge(spriteProvider);
		axis = velocityX != 0 ? Direction.Axis.X : velocityY != 0 ? Direction.Axis.Y : Direction.Axis.Z;
	}

	public void tick() {
		float fadeOutTicks = 4;
		if(maxAge - age < fadeOutTicks) {
			this.colorAlpha -= (float) (0.75 / fadeOutTicks);
		}

		this.scale *= scaleIncrease;

		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteProvider);
		}
	}

	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

		Vec3f[] vec3fs = getVec3fs();

		float j = this.getSize(tickDelta);

//		Quaternion rotationX = new Quaternion(Vec3f.POSITIVE_X, this.angle, true);
//		Quaternion rotationY = new Quaternion(Vec3f.POSITIVE_Y, this.angle, true);
//		Quaternion rotationZ = new Quaternion(Vec3f.POSITIVE_Z, this.angle, true);

		for(int k = 0; k < 4; ++k) {
			Vec3f vec3f2 = vec3fs[k];
			vec3f2.scale(j);
//			if (xDirection) vec3f2.rotate(rotationX);
//			if (yDirection) vec3f2.rotate(rotationY);
//			if (zDirection) vec3f2.rotate(rotationZ);
			vec3f2.add(f, g, h);
		}

		float l = this.getMinU();
		float m = this.getMaxU();
		float n = this.getMinV();
		float o = this.getMaxV();
		int p = this.getBrightness(tickDelta);

		vertexConsumer.vertex(vec3fs[0].getX(), vec3fs[0].getY(), vec3fs[0].getZ())
			.uv(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(vec3fs[1].getX(), vec3fs[1].getY(), vec3fs[1].getZ())
			.uv(m, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(vec3fs[2].getX(), vec3fs[2].getY(), vec3fs[2].getZ())
			.uv(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(vec3fs[3].getX(), vec3fs[3].getY(), vec3fs[3].getZ())
			.uv(l, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();

		vertexConsumer.vertex(vec3fs[3].getX(), vec3fs[3].getY(), vec3fs[3].getZ())
			.uv(l, o).color(colorRed, colorGreen, colorBlue, colorAlpha).light(p).next();
		vertexConsumer.vertex(vec3fs[2].getX(), vec3fs[2].getY(), vec3fs[2].getZ())
			.uv(l, n).color(colorRed, colorGreen, colorBlue, colorAlpha).light(p).next();
		vertexConsumer.vertex(vec3fs[1].getX(), vec3fs[1].getY(), vec3fs[1].getZ())
			.uv(m, n).color(colorRed, colorGreen, colorBlue, colorAlpha).light(p).next();
		vertexConsumer.vertex(vec3fs[0].getX(), vec3fs[0].getY(), vec3fs[0].getZ())
			.uv(m, o).color(colorRed, colorGreen, colorBlue, colorAlpha).light(p).next();
	}

	private Vec3f @NotNull [] getVec3fs() {
		Vec3f[] vec3fs;

		if (axis.equals(Direction.Axis.X)) {
			vec3fs = new Vec3f[]{
				new Vec3f(0.0F, -1.0F, -1.0F),
				new Vec3f(0.0F, 1.0F, -1.0F),
				new Vec3f(0.0F, 1.0F, 1.0F),
				new Vec3f(0.0F, -1.0F, 1.0F)
			};
		} else if (axis.equals(Direction.Axis.Z)) {
			vec3fs = new Vec3f[]{
				new Vec3f(-1.0F, -1.0F, 0.0F),
				new Vec3f(-1.0F, 1.0F, 0.0F),
				new Vec3f(1.0F, 1.0F, 0.0F),
				new Vec3f(1.0F, -1.0F, 0.0F)
			};
		} else {
			vec3fs = new Vec3f[]{
				new Vec3f(-1.0F, 0.0F, -1.0F),
				new Vec3f(-1.0F, 0.0F, 1.0F),
				new Vec3f(1.0F, 0.0F, 1.0F),
				new Vec3f(1.0F, 0.0F, -1.0F)
			};
		}
		return vec3fs;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType parameters, ClientWorld clientWorld,
									   double x, double y, double z,
									   double velocityX, double velocityY, double velocityZ) {
			return new MagnetizerParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
		}
	}
}
