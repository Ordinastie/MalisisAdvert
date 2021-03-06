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

import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.advert.model.ModelVariantContainer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.block.component.DirectionalComponent;
import net.malisis.core.renderer.MalisisRenderer;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.RenderType;
import net.malisis.core.renderer.element.Face;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.element.shape.Cube;
import net.malisis.core.renderer.icon.Icon;
import net.malisis.core.renderer.model.MalisisModel;
import net.malisis.core.util.EnumFacingUtils;
import net.minecraft.util.EnumFacing;

/**
 * @author Ordinastie
 *
 */
public class AdvertRenderer extends MalisisRenderer<AdvertTileEntity>
{
	private ModelVariantContainer<?> container;
	private MalisisModel model;
	private Shape cube = new Cube();
	private RenderParameters rp = new RenderParameters();

	public AdvertRenderer()
	{
		registerFor(AdvertTileEntity.class);
	}

	@Override
	public void render()
	{
		rp.reset();
		rp.rotateIcon.set(false);
		//rp.applyTexture.reset();

		if (tileEntity == null || tileEntity.getModelContainer() == null)
		{
			if (renderType == RenderType.BLOCK)
				drawShape(cube, rp);
			return;
		}

		container = tileEntity.getModelContainer();
		model = container.getModel().getModel();
		model.resetState();
		EnumFacing dir = DirectionalComponent.getDirection(blockState);
		model.rotate(EnumFacingUtils.getRotationCount(dir) * 90, 0, 1, 0, 0, 0, 0);

		if (renderType == RenderType.BLOCK)
		{
			container.renderBlock(this, tileEntity, rp);
			return;
		}

		if (renderType == RenderType.TILE_ENTITY)
		{
			container.renderTileEntity(this, tileEntity, rp);
		}
	}

	public void renderAdvertFace(Face face, AdvertSelection as)
	{
		renderAdvertFace(face, as, as != null ? as.getIcon() : null);
	}

	public void renderAdvertFace(Face face, AdvertSelection as, Icon icon)
	{
		ClientAdvert advert = null;

		if (as != null)
		{
			advert = as.getAdvert();
			if (advert == null && !ClientAdvert.isPending())
				tileEntity.addSelection(0, null);
		}

		rp.applyTexture.set(false);
		if (advert != null && advert.getTexture() != null)
			bindTexture(advert.getTexture().getResourceLocation());
		else
		{
			bindTexture(container.getModel().getPlaceHolder());

			rp.icon.set(null);
		}

		face.setTexture(icon);
		drawFace(face, rp);
		next();
	}
}
