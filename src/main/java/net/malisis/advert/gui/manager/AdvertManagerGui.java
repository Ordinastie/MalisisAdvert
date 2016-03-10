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

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.advert.gui.AdvertView;
import net.malisis.advert.network.AdvertDeleteMessage;
import net.malisis.advert.network.AdvertSaveMessage;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.ComponentPosition;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.container.UIListContainer;
import net.malisis.core.client.gui.component.container.UIPanel;
import net.malisis.core.client.gui.component.container.UITabGroup;
import net.malisis.core.client.gui.component.container.UIWindow;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITab;
import net.malisis.core.renderer.icon.GuiIcon;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.StringUtils;

import com.google.common.eventbus.Subscribe;

/**
 * @author Ordinastie
 *
 */
public class AdvertManagerGui extends MalisisGui
{
	public static ResourceLocation iconsRl = new ResourceLocation(MalisisAdvert.modid, "textures/gui/advertmanager.png");
	public static GuiTexture icons = new GuiTexture(iconsRl, 96, 96);
	public static GuiIcon addIcon = icons.getIcon(0, 0, 32, 32);
	public static GuiIcon refreshIcon = icons.getIcon(64, 0, 32, 32);
	public static GuiIcon editIcon = AdvertManagerGui.icons.getIcon(0, 32, 32, 32);
	public static GuiIcon viewIcon = AdvertManagerGui.icons.getIcon(32, 32, 32, 32);

	public static ClientAdvert advert;

	private AdvertList advertList;
	private UITabGroup tabs;
	private UIPanel formCont;
	private AdvertForm advertForm;
	private AdvertView advertView;
	private UIContainer<?> advertViewCont;

	private UITab tabEdit;
	private UITab tabView;

	private int width = 400;
	private int height = 250;

	@Override
	public void construct()
	{
		//top buttons
		UIImage imgRefresh = new UIImage(this, icons, refreshIcon).setSize(10, 10);
		UIImage imgAdd = new UIImage(this, icons, addIcon).setSize(10, 10);

		UIButton btnAdd = new UIButton(this, imgAdd).setPosition(0, 0, Anchor.RIGHT).setSize(16, 16).register(this);
		btnAdd.setName("advertAdd");
		btnAdd.setTooltip("malisisadvert.gui.add");

		UIButton btnRefresh = new UIButton(this, imgRefresh).setPosition(-17, 0, Anchor.RIGHT).setSize(16, 16).register(this);
		btnRefresh.setName("listRefresh");
		btnRefresh.setTooltip("malisisadvert.gui.refresh");

		//advert list
		advertList = new AdvertList(this, 0, height - 60).register(this);
		advertList.setPosition(0, 20);
		advertList.setElementSpacing(1);
		advertList.setElements(ClientAdvert.listAdverts());

		createForm();

		//close button
		UIButton btnClose = new UIButton(this, "malisisadvert.gui.close").setPosition(0, 0, Anchor.BOTTOM | Anchor.CENTER).register(this);
		btnClose.setName("close");

		UIWindow window = new UIWindow(this, "malisisadvert.gui.advertmanager", width, 250);

		//tabs
		UIImage imgEdit = new UIImage(this, AdvertManagerGui.icons, editIcon).setSize(10, 10);
		UIImage imgView = new UIImage(this, AdvertManagerGui.icons, viewIcon).setSize(10, 10);

		tabs = new UITabGroup(this, ComponentPosition.RIGHT);
		tabView = tabs.addTab(new UITab(this, imgView), advertViewCont);
		tabEdit = tabs.addTab(new UITab(this, imgEdit), advertForm);
		tabs.attachTo(formCont, true);
		tabs.setActiveTab(tabView);
		tabs.setVisible(false);

		window.add(advertList, tabs, formCont, btnAdd, btnRefresh, btnClose);

		addToScreen(window);

		advert = null;
	}

	private void createForm()
	{
		//form
		advertForm = new AdvertForm(this);
		//view
		advertView = new AdvertView(this, false);
		advertViewCont = new UIContainer<>(this);
		advertViewCont.add(advertView);

		//container holding form, view and tabs
		formCont = new UIPanel(this);
		formCont.setSize(width / 2 - 8, height - 60).setPosition(0, 20, Anchor.RIGHT);
		formCont.setVisible(false);

		//tabs.setActiveTab()

		formCont.add(advertForm, advertViewCont);
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event)
	{
		if (event.isFrom("advertAdd"))
		{
			showForm(true);
			selectAdvert(null);
		}
		else if (event.isFrom("advertSave"))
		{
			String name = advertForm.getName();
			String url = advertForm.getUrl();
			if (StringUtils.isEmpty(name) || StringUtils.isEmpty(url))
				return;

			if (advert == null)
				advert = new ClientAdvert(0);

			advert.setInfos(name, url);

			AdvertSaveMessage.save(advert);

			if (advert.getId() != 0)
				selectAdvert(advert);
			else
				showForm(false);

		}
		else if (event.isFrom("advertDelete"))
		{
			if (advert != null)
				AdvertDeleteMessage.queryDelete(advert);
			showForm(false);
			selectAdvert(null);
		}
		else if (event.isFrom("listRefresh"))
			ClientAdvert.queryAdvertList();
		if (event.isFrom("close"))
			close();
	}

	@Subscribe
	public void onAdvertSelect(UIListContainer.SelectEvent<AdvertList, ClientAdvert> event)
	{
		showForm(event.getSelected() != null);
		selectAdvert(event.getSelected());
	}

	private void showForm(boolean show)
	{
		formCont.setVisible(show);
		tabs.setVisible(show);
		advertList.setSize(show ? width / 2 - 7 : 0, advertList.getHeight());
	}

	public void selectAdvert(ClientAdvert advert)
	{
		setViewTab(advert != null);
		AdvertManagerGui.advert = advert;
		advertForm.fillForm(advert);
		advertView.setAdvert(advert);
		advertList.setSelected(advert);
	}

	public void setViewTab(boolean b)
	{
		tabs.setActiveTab(b ? tabView : tabEdit);
	}
}
