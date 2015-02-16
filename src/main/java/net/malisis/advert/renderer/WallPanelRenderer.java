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

import net.malisis.advert.block.WallPanel;
import net.malisis.core.renderer.MalisisRenderer;
import net.malisis.core.renderer.RenderType;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.element.shape.Cube;
import net.malisis.core.renderer.model.MalisisModel;

/**
 * @author Ordinastie
 *
 */
public class WallPanelRenderer extends MalisisRenderer
{
	MalisisModel model = new MalisisModel();

	@Override
	protected void initialize()
	{
		Shape panel = new Cube();
		panel.setSize(2, 3, .1875F);
		panel.translate(-.5F, 0, 0);
		model.addShape("panel", panel);

		Shape foot = new Cube();
		foot.setSize(.1875F, 1, .1875F);
		foot.translate(.5F - .1875F / 2, 0, 0);
		model.addShape("foot", foot);

		model.storeState();

		rp.interpolateUV.set(false);
		rp.renderAllFaces.set(true);
		rp.calculateAOColor.set(false);
		rp.calculateBrightness.set(true);
		rp.useBlockBrightness.set(true);
	}

	private int getRotation()
	{
		switch (blockMetadata & 3)
		{
			case WallPanel.DIR_SOUTH:
				return 180;
			case WallPanel.DIR_EAST:
				return 270;
			case WallPanel.DIR_WEST:
				return 90;
			case WallPanel.DIR_NORTH:
			default:
				return 0;

		}

	}

	@Override
	public void render()
	{
		if (renderType == RenderType.ISBRH_WORLD)
		{
			initialize();

			model.resetState();
			model.rotate(getRotation(), 0, 1, 0, 0, 0, 0);

			if (WallPanel.hasFoot(blockMetadata))
			{
				model.translate(0, 0, 0.5F - .1875F / 2);
				model.getShape("panel").translate(0, 1, 0);
				drawShape(model.getShape("foot"), rp);
			}

			model.getShape("panel").applyMatrix();
			model.getShape("panel").deductParameters();

			drawShape(model.getShape("panel"), rp);
		}
	}
}
