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
import net.malisis.core.renderer.element.Shape;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

/**
 * @author Ordinastie
 *
 */
public class PanelModel extends AdvertModel
{
	public enum FootType
	{
		WALL, SMALL, FULL,
	}

	protected FootType footType;
	private Shape smallFoot;
	private Shape fullFoot;
	private Shape panel;
	private Shape display;
	private IIcon panelIcon;

	public PanelModel(FootType footType, boolean wallMounted)
	{
		this.id = "PANEL_" + footType;
		this.name = id;
		this.width = 2;
		this.height = 3;
		this.objFile = new ResourceLocation(MalisisAdvert.modid, "models/panel.obj");
		this.placeHolder = new ResourceLocation(MalisisAdvert.modid, "textures/blocks/MA23.png");

		this.footType = footType;
		this.isWallMounted = wallMounted;
	}

	@Override
	public void loadModelFile()
	{
		if (loaded)
			return;

		super.loadModelFile();
		smallFoot = model.getShape("SmallFoot");
		fullFoot = model.getShape("FullFoot");
		panel = model.getShape("Panel");
		display = model.getShape("Advert");
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		panelIcon = register.registerIcon("malisisadvert:panel");
	}

	@Override
	public AxisAlignedBB[] getBoundingBox()
	{
		float w = 3F / 16F;
		AxisAlignedBB foot = AxisAlignedBB.getBoundingBox(0.375F, 0, 0.5F - w, 0.625F, 1, 0.5F);
		AxisAlignedBB panel = AxisAlignedBB.getBoundingBox(-0.5F, 0, 0.5F - w, 1.5F, 3, 0.5F);

		if (footType == FootType.WALL)
			panel.offset(0, 0, 0.5F);

		if (footType == FootType.FULL)
			panel.maxY++;

		AxisAlignedBB[] aabbs;
		if (footType == FootType.SMALL)
		{
			panel.offset(0, 1, 0);
			aabbs = new AxisAlignedBB[] { foot, panel };
		}
		else
			aabbs = new AxisAlignedBB[] { panel };

		return aabbs;
	}

	@Override
	public void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp)
	{
		rp.icon.set(panelIcon);
		if (footType == FootType.SMALL)
			renderer.drawShape(smallFoot, rp);
		else if (footType == FootType.FULL)
			renderer.drawShape(fullFoot, rp);
		else
			panel.translate(0, -1, -.5F);

		renderer.drawShape(panel, rp);
	}

	@Override
	public void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp)
	{
		if (footType == FootType.WALL)
			display.translate(0, -1, -.5F);

		renderer.drawShape(display, rp);
	}
}
