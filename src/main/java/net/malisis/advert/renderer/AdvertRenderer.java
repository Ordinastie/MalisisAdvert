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

package net.malisis.advert.renderer;

import net.malisis.advert.AdvertModel;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.advert.block.AdvertBlock;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.renderer.MalisisRenderer;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.RenderType;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.element.shape.Cube;
import net.malisis.core.renderer.model.MalisisModel;
import net.minecraft.util.ResourceLocation;

/**
 * @author Ordinastie
 *
 */
public class AdvertRenderer extends MalisisRenderer
{
	private ResourceLocation rlModel;
	private ResourceLocation rlMA;
	private MalisisModel model;
	private Shape smallFoot;
	private Shape fullFoot;
	private Shape panel;
	private Shape display;
	private Shape cube;

	private AdvertTileEntity tileEntity;

	@Override
	protected void initialize()
	{
		rlModel = new ResourceLocation(MalisisAdvert.modid, "models/panel.obj");
		rlMA = new ResourceLocation(MalisisAdvert.modid, "textures/blocks/MA23.png");

		model = new MalisisModel(rlModel);
		smallFoot = model.getShape("SmallFoot");
		fullFoot = model.getShape("FullFoot");
		panel = model.getShape("Panel");
		display = model.getShape("Advert");

		cube = new Cube();

		rp = new RenderParameters();
	}

	private int getRotation()
	{
		switch (blockMetadata & 3)
		{
			case AdvertBlock.DIR_SOUTH:
				return 180;
			case AdvertBlock.DIR_EAST:
				return 270;
			case AdvertBlock.DIR_WEST:
				return 90;
			case AdvertBlock.DIR_NORTH:
			default:
				return 0;
		}
	}

	@Override
	public void render()
	{
		tileEntity = (AdvertTileEntity) super.tileEntity;
		rp.icon.reset();
		rp.useCustomTexture.reset();

		model.resetState();
		model.rotate(getRotation(), 0, 1, 0, 0, 0, 0);
		if (renderType == RenderType.ISBRH_WORLD)
		{
			renderBlock();
		}

		if (renderType == RenderType.ISBRH_INVENTORY)
		{
			drawShape(cube, rp);
		}

		if (renderType == RenderType.TESR_WORLD)
		{
			renderTileEntity();
		}
	}

	private void renderBlock()
	{
		rp.icon.set(((AdvertBlock) block).getPanelIcon());
		if (tileEntity.getModel() == AdvertModel.PANEL_SMALL_FOOT)
			drawShape(smallFoot, rp);
		else if (tileEntity.getModel() == AdvertModel.PANEL_FULL_FOOT)
			drawShape(fullFoot, rp);
		else
			panel.translate(0, -1, -.5F);

		drawShape(panel, rp);
	}

	private void renderTileEntity()
	{
		AdvertSelection as = tileEntity.getCurrentSelection();
		ClientAdvert advert = null;
		if (as != null)
		{
			advert = as.getAdvert();
			if (advert == null && !ClientAdvert.isPending())
				tileEntity.addSelection(0, null);
		}

		if (advert != null && advert.getTexture() != null)
		{
			bindTexture(advert.getTexture().getResourceLocation());
			rp.icon.set(as.getIcon());
		}
		else
		{
			bindTexture(rlMA);
			rp.useCustomTexture.set(true);
			rp.icon.set(null);
		}

		if (tileEntity.getModel() == AdvertModel.PANEL_WALL)
			display.translate(0, -1, -.5F);

		drawShape(display, rp);
	}
}
