package org.altadoon.gt6x.common.rendering;

import gregapi.render.IIconContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import static org.altadoon.gt6x.common.rendering.Geometry.*;

public class IconRotated implements IIcon {

	public static class RotatableIconContainer implements IIconContainer {
		protected IIconContainer inner;

		protected int rotation;

		public RotatableIconContainer(IIconContainer container, int rotation) {
			this.inner = container;
			this.rotation = rotation;
		}

		@Override public IIcon getIcon(int renderPass) {return new IconRotated(inner.getIcon(renderPass), rotation);}

		@Override
		public boolean isUsingColorModulation(int renderPass) {
			return inner.isUsingColorModulation(renderPass);
		}

		@Override
		public short[] getIconColor(int renderPass) {
			return inner.getIconColor(renderPass);
		}

		@Override
		public int getIconPasses() {
			return inner.getIconPasses();
		}

		@Override
		public ResourceLocation getTextureFile() {
			return inner.getTextureFile();
		}

		@Override
		public void registerIcons(IIconRegister iconRegister) {
			inner.registerIcons(iconRegister);
		}
	}

	protected IIcon baseIcon;
	protected int rotation;

	public IconRotated(IIcon icon, int rotation) {
		this.baseIcon = icon;
		this.rotation = rotation;
	}

	@Override
	public int getIconWidth() {
		return baseIcon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return baseIcon.getIconHeight();
	}

	@Override
	public float getMinU() {
		float half = (this.baseIcon.getMinU() + this.baseIcon.getMaxU()) / 2;
		boolean flip = this.rotation > 1;

		return switch (rotation) {
			case ROT_0, ROT_180 -> flip ? half : this.baseIcon.getMinU();
			case ROT_90, ROT_270 -> flip ? this.baseIcon.getMaxU() : half;
			default -> Float.NaN;
		};
	}

	@Override
	public float getMaxU() {
		float half = (this.baseIcon.getMinU() + this.baseIcon.getMaxU()) / 2;
		boolean flip = this.rotation > 1;

		return switch (rotation) {
			case ROT_0, ROT_180 -> flip ? this.baseIcon.getMinU() : half;
			case ROT_90, ROT_270 -> flip ? half : this.baseIcon.getMaxU();
			default -> Float.NaN;
		};
	}

	/**
	 * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
	 */
	@Override
	public float getInterpolatedU(double p_94214_1_) {
		float f = this.getMaxU() - this.getMinU();
		return this.getMinU() + f * ((float)p_94214_1_ / 16.0F);
	}

	@Override
	public float getMinV() {
		return this.rotation > 1 ? (this.baseIcon.getMaxV() + this.baseIcon.getMinV()) / 2 : this.baseIcon.getMinV();
	}

	@Override
	public float getMaxV() {
		return this.rotation > 1 ? this.baseIcon.getMinV() : (this.baseIcon.getMaxV() + this.baseIcon.getMinV()) / 2;
	}

	/**
	 * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
	 */
	@Override
	public float getInterpolatedV(double p_94207_1_) {
		float f = this.getMaxV() - this.getMinV();
		return this.getMinV() + f * ((float)p_94207_1_ / 16.0F);
	}

	@Override
	public String getIconName() {
		return baseIcon.getIconName();
	}
}
