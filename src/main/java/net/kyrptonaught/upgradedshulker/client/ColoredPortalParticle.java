package net.kyrptonaught.upgradedshulker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class ColoredPortalParticle extends PortalParticle {
    protected ColoredPortalParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
    }

    @Environment(EnvType.CLIENT)
    public static class GreenFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public GreenFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            PortalParticle portalParticle = new ColoredPortalParticle(clientWorld, d, e, f, g, h, i);
            portalParticle.setSprite(this.spriteProvider);
            portalParticle.setColor(47 / 255f, 159 / 255f, 14 / 255f);// /255f
            return portalParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class BlueFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public BlueFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            PortalParticle portalParticle = new ColoredPortalParticle(clientWorld, d, e, f, g, h, i);
            portalParticle.setSprite(this.spriteProvider);
            portalParticle.setColor(81 / 255f, 255 / 255f, 249 / 255f);// /255f
            return portalParticle;
        }
    }
}
