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

package net.malisis.advert.gui;

import java.util.List;

import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.advert.gui.advertselection.AdvertSelectionGui;
import net.malisis.advert.model.AdvertModel;
import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.renderer.font.FontRenderOptions;
import net.malisis.core.renderer.font.MalisisFont;
import net.malisis.core.util.MouseButton;
import net.minecraft.client.gui.GuiScreen;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

/**
 * @author Ordinastie
 *
 */
public class AdvertView extends UIComponent<AdvertView>
{
	private MalisisFont font = MalisisFont.minecraftFont;
	private FontRenderOptions fro;
	private boolean editable = false;
	private AdvertSelection advertSelection;
	private int sX, sY, eX, eY;
	private ClientAdvert advert;
	private boolean move = false;

	public AdvertView(MalisisGui gui, boolean editable)
	{
		super(gui);
		this.editable = editable;
		resetUVs();

		fro = new FontRenderOptions();
		fro.color = 0xFFFFFF;
		fro.shadow = true;
	}

	public void setAdvert(ClientAdvert advert)
	{
		this.advert = advert;
	}

	public void setAdvertSelection(AdvertSelection advertSelection)
	{
		this.advertSelection = advertSelection;
		if (advertSelection != null)
		{
			advert = advertSelection.getAdvert();
			sX = advertSelection.getX(advertSelection.u);
			sY = advertSelection.getY(advertSelection.v);
			eX = advertSelection.getX(advertSelection.U);
			eY = advertSelection.getY(advertSelection.V);
		}
		else
			advert = null;
	}

	public float getFactor()
	{
		if (advert == null || advert.getTexture() == null)
			return 1;
		return Math.min((float) getWidth() / advert.getTexture().getWidth(), (float) getHeight() / advert.getTexture().getHeight());
	}

	public int factorX(int x)
	{
		int f = Math.round(relativeX(x - 1) / getFactor());
		return Math.min(Math.max(0, f), advert.getTexture().getWidth());
	}

	public int factorY(int y)
	{
		int f = Math.round(relativeY(y - 1) / getFactor());
		return Math.min(Math.max(0, f), advert.getTexture().getHeight());
	}

	private boolean isTexture()
	{
		return advert != null && advert.getTexture() != null;
	}

	private void resetUVs()
	{
		if (!isTexture())
			return;

		sX = sY = 0;
		eX = advert.getTexture().getWidth();
		eY = advert.getTexture().getHeight();
		if (advertSelection != null)
			advertSelection.setPixels(sX, sY, eX, eY);
		updateComponents();
	}

	private void applyUVs()
	{
		if (advertSelection != null)
			advertSelection.setPixels(sX, sY, eX, eY);
		updateComponents();
	}

	private void updateComponents()
	{
		((AdvertSelectionGui) getGui()).updateAdvertComponent();
	}

	private void forceRatio()
	{
		AdvertModel<?> model = ((AdvertSelectionGui) getGui()).getModel();
		float mratio = model.getWidth() / model.getHeight();

		int dy = (int) (Math.abs(eX - sX) / mratio);
		if (sY < eY)
			eY = sY + dy;
		else
			eY = sY - dy;

		if (eY < 0 || eY > advert.getTexture().getHeight())
		{
			eY = eY < 0 ? 0 : advert.getTexture().getHeight();
			int dx = (int) (Math.abs(eY - sY) * mratio);
			if (sX < eX)
				eX = sX + dx;
			else
				eX = sX - dx;
		}
	}

	@Override
	public boolean onRightClick(int x, int y)
	{
		if (!editable)
			return super.onRightClick(x, y);

		resetUVs();

		return true;
	}

	@Override
	public boolean onButtonPress(int x, int y, MouseButton button)
	{
		if (editable && button == MouseButton.LEFT && isTexture())
		{
			if (GuiScreen.isCtrlKeyDown())
				move = true;
			else
			{
				move = false;
				sX = eX = factorX(x);
				sY = eY = factorY(y);
			}
		}
		return super.onButtonPress(x, y, button);
	}

	@Override
	public boolean onButtonRelease(int x, int y, MouseButton button)
	{
		//		sX = sX > eX ? eX : sX;
		//		eX = sX > eX ? sX : eX;
		//		sY = sY > eY ? eY : sY;
		//		eY = sY > eY ? sY : eY;
		move = false;
		return super.onButtonRelease(x, y, button);
	}

	@Override
	public boolean onDrag(int lastX, int lastY, int x, int y, MouseButton button)
	{
		if (!editable || button != MouseButton.LEFT || !isTexture())
			return super.onDrag(lastX, lastY, x, y, button);

		if (move)
		{
			float f = getFactor();
			int maxX = advert.getTexture().getWidth();
			int maxY = advert.getTexture().getHeight();
			int dx = Math.round((x - lastX) / f);
			int dy = Math.round((y - lastY) / f);
			if ((Math.min(sX, eX) > 0 && dx < 0) || (Math.max(sX, eX) < maxX && dx > 0))
			{
				sX += dx;
				eX += dx;
			}
			if ((Math.min(sY, eY) > 0 && dy < 0) || (Math.max(eY, sY) < maxY && dy > 0))
			{
				sY += dy;
				eY += dy;
			}
			applyUVs();
			return true;
		}

		eX = factorX(x);
		eY = factorY(y);

		if (GuiScreen.isShiftKeyDown())
			forceRatio();

		applyUVs();

		return true;
	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		if (advert == null || StringUtils.isEmpty(advert.getUrl()))
			return;

		GuiTexture texture = advert.getTexture();
		if (texture == null)
		{
			String text = advert.getError();
			if (text != null)
			{
				int y = 0;
				List<String> err = font.wrapText(text, getWidth());
				for (String e : err)
					renderer.drawText(null, e, 0, 11 * y++, 0, fro);
			}
			else
				renderer.drawText("Downloading...");
			return;
		}

		shape.resetState();
		shape.setSize((int) (texture.getWidth() * getFactor()), (int) (texture.getHeight() * getFactor()));
		renderer.bindTexture(texture);
		renderer.drawShape(shape, rp);

		if (editable/* && (u != 0 || v != 0 || U != 1 || V != 1)*/)
		{
			renderer.next(GL11.GL_LINE_LOOP);
			renderer.disableTextures();
			GL11.glLineWidth(1);

			float u = advertSelection.U - advertSelection.u;
			float v = advertSelection.V - advertSelection.v;
			float w = texture.getWidth() * getFactor();
			float h = texture.getHeight() * getFactor();

			shape.resetState();
			shape.setSize((int) (u * w), (int) (v * h));
			shape.translate(Math.round(advertSelection.u * w), Math.round(advertSelection.v * h));
			rp.colorMultiplier.set(0xFF0000);
			renderer.drawShape(shape, rp);

			renderer.next(GL11.GL_QUADS);
			renderer.enableTextures();
		}
	}
}
