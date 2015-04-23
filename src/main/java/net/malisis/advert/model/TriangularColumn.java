/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.advert.model;

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.animation.AnimationRenderer;
import net.malisis.core.renderer.animation.transformation.Rotation;
import net.malisis.core.renderer.element.Shape;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * @author Ordinastie
 *
 */
public class TriangularColumn extends AdvertModel
{
	private Shape base;
	private Shape topBottom;
	private Shape panels;
	private IIcon icon;
	private AnimationRenderer ar;
	private Rotation rotation;

	public TriangularColumn()
	{
		this.id = "triangular_column";
		this.name = id;
		this.width = 1.4F;
		this.height = 2.5F;
		this.objFile = new ResourceLocation(MalisisAdvert.modid, "models/triangular_column.obj");
		this.placeHolder = new ResourceLocation(MalisisAdvert.modid, "textures/blocks/MA23.png");

		this.isWallMounted = false;
	}

	@Override
	public void loadModelFile()
	{
		//loaded = false;
		if (loaded)
			return;

		super.loadModelFile();

		base = model.getShape("base");
		topBottom = model.getShape("PanelsTopBottom");
		panels = model.getShape("Panels");
		ar = new AnimationRenderer();
		rotation = new Rotation(360, 0, 1, 0, 0, 0, 0).forTicks(600).loop(-1);
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		icon = register.registerIcon("malisisadvert:triangular_column");
	}

	@Override
	public AxisAlignedBB[] getBoundingBox()
	{
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(-0.2F, 0, 0.1F, 1.2F, 3, 1.3F);
		return new AxisAlignedBB[] { aabb };
	}

	@Override
	public void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp)
	{
		rp.icon.set(icon);
		renderer.drawShape(base, rp);
	}

	@Override
	public void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp)
	{
		topBottom.resetState();
		ar.animate(topBottom, rotation);

		renderer.next(GL11.GL_TRIANGLES);
		rp.icon.set(icon);
		renderer.drawShape(topBottom, rp);
		renderer.next(GL11.GL_QUADS);
	}

	@Override
	public void renderAdvert(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp)
	{
		panels.resetState();
		ar.animate(panels, rotation);
		renderer.drawShape(panels, rp);
	}
}
