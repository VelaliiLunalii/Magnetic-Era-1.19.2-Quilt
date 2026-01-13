package io.github.velaliilunalii.magnetic_era.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class MagneticFieldParticle extends SpriteBillboardParticle {
	final SpriteProvider spriteProvider;

	MagneticFieldParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, velocityX, velocityY, velocityZ);
		this.spriteProvider = spriteProvider;
		this.maxAge = 15;
		this.colorRed = 1;
		this.colorGreen = 1;
		this.colorBlue = 1;
		this.colorAlpha = 1F;
		this.scale = 0.05F;
		this.collidesWithWorld = false;
		this.velocityMultiplier = 1.1F;
		this.gravityStrength = 0.0F;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.setSpriteForAge(spriteProvider);
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

		float fadeOutTicks = 4;
		if(maxAge - age < fadeOutTicks) this.colorAlpha -= (float) (0.75/fadeOutTicks);

		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteProvider);
		}
	}

	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
		public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
				return new MagneticFieldParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			}
		}
}
