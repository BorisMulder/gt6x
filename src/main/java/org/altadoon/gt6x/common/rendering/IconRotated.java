package org.altadoon.gt6x.common.rendering;

import gregapi.render.IIconContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class IconRotated implements IIcon {

	public static class RotatableIconContainer implements IIconContainer {
		protected IIconContainer inner;

		protected int rotation;
		protected boolean flipU;
		protected boolean flipV;

		public RotatableIconContainer(IIconContainer container, int rotation, boolean flipU, boolean flipV) {
			this.inner = container;
			this.rotation = rotation;
			this.flipU = flipU;
			this.flipV = flipV;
		}

		@Override public IIcon getIcon(int renderPass) {return new IconRotated(inner.getIcon(renderPass), rotation, flipU, flipV);}

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
	protected boolean flipU;
	protected boolean flipV;

	public IconRotated(IIcon icon, int rotation, boolean flipU, boolean flipV) {
		this.baseIcon = icon;
		this.rotation = rotation;
		this.flipU = flipU;
		this.flipV = flipV;
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
		//TODO
		return this.flipU ? this.baseIcon.getMaxU() : this.baseIcon.getMinU();
	}

	@Override
	public float getMaxU() {
		return this.flipU ? this.baseIcon.getMinU() : this.baseIcon.getMaxU();
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
		return this.flipV ? this.baseIcon.getMaxV() : this.baseIcon.getMinV();
	}

	@Override
	public float getMaxV() {
		return this.flipV ? this.baseIcon.getMinV() : this.baseIcon.getMaxV();
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
