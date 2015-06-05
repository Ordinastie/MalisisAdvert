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

package net.malisis.advert.gui.advertselection;

import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.gui.AdvertView;
import net.malisis.advert.model.AdvertModel;
import net.malisis.advert.model.AdvertModel.IModelVariant;
import net.malisis.advert.network.AdvertSelectionMessage;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.container.UIPanel;
import net.malisis.core.client.gui.component.container.UIWindow;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.decoration.UISeparator;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.util.TileEntityUtils;

import com.google.common.eventbus.Subscribe;

/**
 * @author Ordinastie
 *
 */
public class AdvertSelectionGui extends MalisisGui
{
	private AdvertTileEntity tileEntity;

	UISelect<AdvertModel> selModel;
	private UIButton btnSave;
	private UIButton btnClose;
	private UIContainer modelCont;
	private UISeparator separator;
	private AdvertSelectionComponent asc;
	private AdvertView adview;

	public AdvertSelectionGui(AdvertTileEntity tileEntity)
	{
		this.tileEntity = tileEntity;
		TileEntityUtils.linkTileEntityToGui(tileEntity, this);
	}

	@Override
	public void construct()
	{
		int width = 400;
		int height = 300;

		int y = 0;

		UILabel labelModel = new UILabel(this, "malisisadvert.gui.model");
		y += 12;

		selModel = new UISelect<AdvertModel>(this, 150, AdvertModel.list()).setPosition(0, y);
		selModel.setLabelPattern("malisisadvert.gui.model.%s");
		selModel.register(this);
		y += 18;

		modelCont = new UIContainer(this);
		modelCont.setPosition(5, y).setSize(1, 1);
		y += 2;

		separator = new UISeparator(this).setPosition(0, y).setColor(0x999999);
		y += 5;

		asc = new AdvertSelectionComponent(this).setPosition(0, y);

		adview = new AdvertView(this, true).register(this);

		UIContainer<UIContainer> left = new UIContainer<>(this, width / 2 - 15, height - 55).setPosition(5, 15);

		left.add(labelModel);
		left.add(selModel);
		left.add(modelCont);
		left.add(separator);
		left.add(asc);

		UIPanel right = new UIPanel(this, width / 2 - 15, height - 55).setPosition(-5, 15, Anchor.RIGHT);
		right.add(adview);

		btnSave = new UIButton(this, "malisisadvert.gui.save").setPosition(-32, 0, Anchor.BOTTOM | Anchor.CENTER).setSize(60)
				.register(this);
		btnClose = new UIButton(this, "malisisadvert.gui.close").setPosition(32, 0, Anchor.BOTTOM | Anchor.CENTER).setSize(60)
				.register(this);

		UIWindow window = new UIWindow(this, "malisisadvert.gui.advertselection", width, height);

		window.add(left, right, btnSave, btnClose);

		addToScreen(window);
		updateGui();
	}

	public void viewAdvertSelection(AdvertSelection advertSelection)
	{
		adview.setAdvertSelection(advertSelection);
	}

	public void updateAdvertComponent()
	{
		asc.updateComponents();
	}

	public AdvertModel getModel()
	{
		return selModel.getSelectedValue();
	}

	public void setModel(AdvertModel model)
	{
		modelCont.removeAll();

		IModelVariant variant = tileEntity.getModel() == model ? tileEntity.getModelVariant() : model.defaultVariant(tileEntity
				.isWallMounted());

		int height = model.getGuiComponent(this, modelCont, variant);
		modelCont.setSize(0, height);

		separator.setPosition(0, 30 + height);
		asc.setPosition(0, 35 + height);
	}

	@Override
	public void updateGui()
	{
		selModel.setSelectedOption(tileEntity.getModel());
		asc.setAdvertSelection(tileEntity.getCurrentSelection());
		viewAdvertSelection(asc.getAdvertSelection());
		setModel(tileEntity.getModel());
	}

	@Subscribe
	public void onModelSelect(UISelect.SelectEvent<AdvertModel> event)
	{
		setModel(event.getNewValue());
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event)
	{
		if (event.getComponent() == btnClose)
			close();

		if (event.getComponent() == btnSave)
		{
			asc.saveUVs();
			AdvertModel model = selModel.getSelectedValue();
			IModelVariant variant = model.getVariantFromGui(modelCont);
			AdvertSelectionMessage.saveSelection(tileEntity, selModel.getSelectedValue(), variant, asc.getAdvertSelection());
			close();
		}
	}

}
