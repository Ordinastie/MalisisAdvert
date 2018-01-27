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

import org.apache.commons.io.FileUtils;

import net.malisis.advert.advert.ClientAdvert;
import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.renderer.font.FontOptions;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Ordinastie
 *
 */
public class AdvertComponent extends UIBackgroundContainer
{
	private ClientAdvert advert;
	private FontOptions nameOptions = FontOptions.builder().color(0xFFFFFF).shadow().build();
	private FontOptions nameHoverOptions = FontOptions.builder().color(0xFFFF99).shadow().build();

	private FontOptions fontOptions = FontOptions.builder().scale(2F / 3F).color(0x444444).build();
	private FontOptions hoverFontOptions = FontOptions.builder().scale(2F / 3F).color(0x888866).build();

	private UILabel emptyLabel;
	private UILabel name;
	private UILabel dimensions;
	private UILabel fileSize;
	private UILabel url;

	public AdvertComponent(MalisisGui gui, ClientAdvert advert)
	{
		super(gui);
		setSize(INHERITED, 20);

		this.advert = advert;
		emptyLabel = new UILabel(gui);
		emptyLabel.setParent(this);

		int x = 3;
		name = new UILabel(gui, advert.getName());
		name.setPosition(x, 3);
		add(name);

		x += Math.max(70, name.getWidth() + 3);
		dimensions = new UILabel(gui, advert.getWidth() + "x" + advert.getHeight());
		dimensions.setPosition(x, 6);
		add(dimensions);

		x += dimensions.getWidth() + 3;
		String size = FileUtils.byteCountToDisplaySize(advert.getSize());
		fileSize = new UILabel(gui, "(" + size + ")");
		fileSize.setPosition(x, 6);
		add(fileSize);

		url = new UILabel(gui, advert.getUrl());
		url.setPosition(3, 14);
		add(url);
	}

	private void updateColor()
	{
		if (advert == AdvertManagerGui.advert)
		{
			setColor(0xBBBBEE);
			setBorder(0x333333, 1, 255);
		}
		else
		{
			setColor(0xC6C6C6);
			setBorder(0xC6C6C6, 1, 255);
		}
	}

	private void updateLabels()
	{
		name.setFontOptions(isHovered() ? nameHoverOptions : nameOptions);
		FontOptions options = isHovered() ? hoverFontOptions : fontOptions;
		dimensions.setFontOptions(options);
		fileSize.setFontOptions(options);
		url.setFontOptions(options);
	}

	public void drawEmpty(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		emptyLabel.setText(TextFormatting.ITALIC + (ClientAdvert.isPending() ? "malisisadvert.gui.querylist" : "malisisadvert.gui.noad"));
		emptyLabel.draw(renderer, mouseX, mouseY, partialTick);
	}

	@Override
	public void draw(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		updateColor();
		updateLabels();
		super.draw(renderer, mouseX, mouseY, partialTick);
	}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{

		super.drawForeground(renderer, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean onClick(int x, int y)
	{
		//return super.onClick(x, y);
		AdvertManagerGui gui = ((AdvertManagerGui) getGui());
		gui.showForm(true);
		gui.selectAdvert(advert);
		return true;
	}
}
