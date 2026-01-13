package io.github.velaliilunalii.magnetic_era.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.velaliilunalii.magnetic_era.particle.effect.MagneticBeamParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;

public class MagneticBeamParticle extends SpriteBillboardParticle {
	final SpriteProvider spriteProvider;
	private boolean xDirection;
	private boolean yDirection;
	private boolean zDirection;
	private boolean isClockwise;

	MagneticBeamParticle(ClientWorld world, double x, double y, double z,
						 double velocityX, double velocityY, double velocityZ,
						 float scale, int maxAge, boolean isClockwise, SpriteProvider spriteProvider) {
		super(world, x, y, z, velocityX, velocityY, velocityZ);
		this.spriteProvider = spriteProvider;

		this.maxAge = maxAge;
		this.scale = scale;
		this.isClockwise = isClockwise;

		this.colorRed = 1;
		this.colorGreen = 1;
		this.colorBlue = 1;
		this.colorAlpha = 1F;
		this.collidesWithWorld = false;
		this.velocityMultiplier = 1F;
		this.gravityStrength = 0.0F;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.setSpriteForAge(spriteProvider);
		xDirection = velocityX != 0;
		yDirection = velocityY != 0;
		zDirection = velocityZ != 0;
	}

	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		this.x += velocityX;
		this.y += velocityY;
		this.z += velocityZ;
		this.velocityX *= velocityMultiplier;
		this.velocityY *= velocityMultiplier;
		this.velocityZ *= velocityMultiplier;
		this.angle += isClockwise ? 1 : -1;

		float fadeOutTicks = 4;
		if(maxAge - age < fadeOutTicks) {
			this.colorAlpha -= (float) (0.75 / fadeOutTicks);
		}

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

		Quaternion rotationX = new Quaternion(Vec3f.POSITIVE_X, this.angle, true);
		Quaternion rotationY = new Quaternion(Vec3f.POSITIVE_Y, this.angle, true);
		Quaternion rotationZ = new Quaternion(Vec3f.POSITIVE_Z, this.angle, true);

		for(int k = 0; k < 4; ++k) {
			Vec3f vec3f2 = vec3fs[k];
			vec3f2.scale(j);
			if (xDirection) vec3f2.rotate(rotationX);
			if (yDirection) vec3f2.rotate(rotationY);
			if (zDirection) vec3f2.rotate(rotationZ);
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
		double absX = Math.abs(this.velocityX);
		double absY = Math.abs(this.velocityY);
		double absZ = Math.abs(this.velocityZ);

		Vec3f[] vec3fs;

		if (absX > absZ && absX > absY) {
			vec3fs = new Vec3f[]{
				new Vec3f(0.0F, -1.0F, -1.0F),
				new Vec3f(0.0F, 1.0F, -1.0F),
				new Vec3f(0.0F, 1.0F, 1.0F),
				new Vec3f(0.0F, -1.0F, 1.0F)
			};
		} else if (absZ > absX && absZ > absY) {
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
	public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<MagneticBeamParticleEffect> {
		public Particle createParticle(MagneticBeamParticleEffect parameters, ClientWorld clientWorld,
										   double x, double y, double z,
										   double velocityX, double velocityY, double velocityZ) {
			return new MagneticBeamParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ,
				parameters.getScale(), parameters.getMaxAge(), parameters.isClockwise(), this.spriteProvider);
		}
	}
}
