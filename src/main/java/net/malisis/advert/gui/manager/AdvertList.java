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

package net.malisis.advert.gui.manager;

import net.malisis.advert.advert.ClientAdvert;
import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIListContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.renderer.font.FontOptions;
import net.malisis.core.renderer.font.MalisisFont;
import net.minecraft.util.text.TextFormatting;

import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;

/**
 * @author Ordinastie
 *
 */
public class AdvertList extends UIListContainer<AdvertList, ClientAdvert>
{
	private MalisisFont font = MalisisFont.minecraftFont;
	private FontOptions nameOptions = FontOptions.builder().color(0xFFFFFF).shadow().build();
	private FontOptions nameHoverOptions = FontOptions.builder().color(0xFFFF99).shadow().build();

	private FontOptions fontOptions = FontOptions.builder().scale(2F / 3F).color(0x444444).build();
	private FontOptions hoverFontOptions = FontOptions.builder().scale(2F / 3F).color(0x666666).build();

	private UILabel emptyLabel;

	public AdvertList(MalisisGui gui)
	{
		super(gui);
		emptyLabel = new UILabel(gui);
		emptyLabel.setParent(this);
	}

	public AdvertList(MalisisGui gui, int width, int height)
	{
		this(gui);
		setSize(width, height);
	}

	@Override
	public int getElementHeight(ClientAdvert element)
	{
		return 20;
	}

	@Override
	public void drawEmpty(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		emptyLabel.setText(TextFormatting.ITALIC + (ClientAdvert.isPending() ? "malisisadvert.gui.querylist" : "malisisadvert.gui.noad"));
		emptyLabel.draw(renderer, mouseX, mouseY, partialTick);
	}

	@Override
	public void drawElementBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick, ClientAdvert advert, boolean isHovered)
	{
		if (!isSelected(advert))
			return;

		renderer.disableTextures();

		int color = /*selected ? 0xBBBBFF :*/0xBBBBEE;
		rp.colorMultiplier.set(color);

		shape.resetState();
		shape.setSize(getContentWidth(), getElementHeight(advert));
		shape.translate(1, 1);
		renderer.drawShape(shape, rp);

		renderer.next(GL11.GL_LINE_LOOP);
		GL11.glLineWidth(2);

		shape.resetState();
		shape.setSize(getContentWidth(), getElementHeight(advert));
		shape.translate(1, 1);
		rp.colorMultiplier.set(0x000000);
		renderer.drawShape(shape, rp);

		renderer.next(GL11.GL_QUADS);
		renderer.enableTextures();
	}

	@Override
	public void drawElementForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick, ClientAdvert advert, boolean isHovered)
	{
		//Name
		int x = 3;
		renderer.drawText(font, advert.getName(), x, 3, 0, isHovered ? nameHoverOptions : nameOptions);
		x += font.getStringWidth(advert.getName(), nameOptions) + 6;

		FontOptions options = isHovered ? hoverFontOptions : this.fontOptions;
		//Image Dimensions
		x = Math.max(70, x);
		String dim = advert.getWidth() + "x" + advert.getHeight();
		renderer.drawText(font, dim, x, 6, 0, options);
		x += font.getStringWidth(dim, options) + 3;

		//File size
		String size = FileUtils.byteCountToDisplaySize(advert.getSize());
		renderer.drawText(font, "(" + size + ")", x, 6, 0, options);

		//URL
		String url = font.clipString(advert.getUrl(), getWidth() - 6, options, true);
		renderer.drawText(font, url, 3, 14, 0, options);
	}

}
