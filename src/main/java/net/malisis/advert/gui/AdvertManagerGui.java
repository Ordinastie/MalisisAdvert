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

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.Advert;
import net.malisis.advert.advert.AdvertManager;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIPanel;
import net.malisis.core.client.gui.component.container.UIWindow;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import com.google.common.eventbus.Subscribe;

/**
 * @author Ordinastie
 *
 */
public class AdvertManagerGui extends MalisisGui
{
	public static ResourceLocation iconsRl = new ResourceLocation(MalisisAdvert.modid, "textures/gui/advertmanager.png");
	public static GuiTexture icons = new GuiTexture(iconsRl, 96, 64);
	public static IIcon addIcon = icons.getIcon(0, 0, 32, 32);
	public static IIcon refreshIcon = icons.getIcon(64, 0, 32, 32);

	private UIPanel advertListPanel;
	private UIButton btnAdd;
	private UIButton btnRefresh;
	private UIButton btnClose;

	public AdvertManagerGui()
	{
		UIImage imgRefresh = new UIImage(this, icons, refreshIcon).setSize(10, 10);
		btnRefresh = new UIButton(this, imgRefresh).setPosition(0, 0, Anchor.RIGHT).setSize(16, 16).register(this);

		UIImage imgAdd = new UIImage(this, icons, addIcon).setSize(10, 10);
		btnAdd = new UIButton(this, imgAdd).setPosition(-17, 0, Anchor.RIGHT).setSize(16, 16).register(this);

		btnClose = new UIButton(this, "Close").setPosition(0, 0, Anchor.BOTTOM | Anchor.CENTER).register(this);

		advertListPanel = new UIPanel(this, 200, 150).setPosition(0, 20);
		UIWindow window = new UIWindow(this, "malisisadvert.gui.advertmanager.title", 400, 250);

		window.add(advertListPanel, btnAdd, btnClose, btnRefresh);

		addToScreen(window);

		updateAdvertList();
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event)
	{
		if (event.getComponent() == btnAdd)
			edit(null);
		else if (event.getComponent() == btnRefresh)
			refreshList();
		else if (event.getComponent() == btnClose)
			close();
	}

	public void refreshList()
	{
		//TODO : send request to server

		updateAdvertList();
	}

	public void updateAdvertList()
	{
		advertListPanel.removeAll();

		int y = 0;
		for (Advert advert : AdvertManager.instance)
		{
			AdvertComponent comp = new AdvertComponent(this, advert);
			comp.setPosition(0, y++ * 22);

			advertListPanel.add(comp);
		}
	}

	public void view(Advert advert)
	{

	}

	public void delete(Advert advert)
	{

	}

	public void edit(Advert advert)
	{

	}

}
