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

import java.util.Arrays;

import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.gui.AdvertView;
import net.malisis.advert.model.AdvertModel;
import net.malisis.advert.model.ModelVariantContainer;
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

	UISelect<AdvertModel<?>> selModel;
	private UIButton btnSave;
	private UIButton btnClose;
	private UIContainer<?> modelCont, ascCont;
	private UISeparator separator;
	private AdvertSelectionComponent[] ascs = new AdvertSelectionComponent[0];
	private AdvertView adview;
	private int variantHeight;

	public AdvertSelectionGui(AdvertTileEntity tileEntity)
	{
		this.tileEntity = tileEntity;
	}

	@Override
	public void construct()
	{
		int width = 400;
		int height = 300;

		int y = 0;

		//LEFT CONTAINER
		UILabel labelModel = new UILabel(this, "malisisadvert.gui.model");
		y += 12;

		selModel = new UISelect<>(this, 150, AdvertModel.list()).setPosition(0, y);
		selModel.setLabelPattern("malisisadvert.gui.model.%s");
		selModel.register(this);
		y += 18;

		modelCont = new UIContainer<>(this);
		modelCont.setPosition(5, y).setSize(1, 1);
		y += 2;

		separator = new UISeparator(this).setPosition(0, y).setColor(0x999999);
		y += 5;

		ascCont = new UIContainer<>(this).setPosition(0, y);
		ascCont.setPadding(1, 1);

		UIContainer<?> left = new UIContainer<>(this, width / 2 - 15, height - 55).setPosition(5, 15);
		left.add(labelModel);
		left.add(selModel);
		left.add(modelCont);
		left.add(separator);
		left.add(ascCont);

		//RIGHT CONTAINER
		adview = new AdvertView(this, true).register(this);

		UIPanel right = new UIPanel(this, width / 2 - 15, height - 55).setPosition(-5, 15, Anchor.RIGHT);
		right.add(adview);

		//SAVE/CLOSE
		btnSave = new UIButton(this, "malisisadvert.gui.save").setPosition(-32, 0, Anchor.BOTTOM | Anchor.CENTER)
																.setSize(60)
																.register(this);
		btnClose = new UIButton(this, "malisisadvert.gui.close").setPosition(32, 0, Anchor.BOTTOM | Anchor.CENTER)
																.setSize(60)
																.register(this);

		//WINDOW
		UIWindow window = new UIWindow(this, "malisisadvert.gui.advertselection", width, height);

		window.add(left, right, btnSave, btnClose);

		addToScreen(window);
		TileEntityUtils.linkTileEntityToGui(tileEntity, this);
	}

	public AdvertModel<?> getModel()
	{
		return selModel.getSelectedValue();
	}

	public void setModel(AdvertModel<?> model)
	{
		modelCont.removeAll();

		ModelVariantContainer<?> container = tileEntity.getModelContainer();
		if (container.getModel() != model)
			container = new ModelVariantContainer<>(model, null, tileEntity.isWallMounted());

		variantHeight = container.getGuiComponent(this, modelCont);
		modelCont.setSize(0, variantHeight);
		separator.setPosition(0, 30 + variantHeight);
		ascCont.setPosition(0, 35 + variantHeight);
		setAscs(model);
	}

	public void setAscs(AdvertModel<?> model)
	{
		int count = ascs.length;
		int y = 60 * count;
		ascs = Arrays.copyOf(ascs, model.getAvailableSlots());
		for (int i = count; i < model.getAvailableSlots(); i++)
		{
			ascs[i] = new AdvertSelectionComponent(this, i, adview).setPosition(0, y);
			y += ascs[i].getHeight();
		}

		ascCont.removeAll();
		ascCont.add(ascs);
	}

	@Override
	public void updateGui()
	{
		ModelVariantContainer<?> container = tileEntity.getModelContainer();

		if (selModel.getSelectedValue() != container.getModel())
		{
			selModel.setSelectedOption(container.getModel());
			setModel(container.getModel());
		}

		AdvertSelection[] selections = tileEntity.getSelections();
		for (int i = 0; i < selections.length; i++)
		{
			ascs[i].setAdvertSelection(selections[i]);
		}

		//viewAdvertSelection(asc.getAdvertSelection());
	}

	@Subscribe
	public void onModelSelect(UISelect.SelectEvent<AdvertModel<?>> event)
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
			ModelVariantContainer<?> container = ModelVariantContainer.fromGui(selModel, modelCont, tileEntity.isWallMounted());

			AdvertSelection[] selections = new AdvertSelection[container.getModel().getAvailableSlots()];
			for (int i = 0; i < ascs.length; i++)
				selections[i] = ascs[i].getAdvertSelection();
			AdvertSelectionMessage.saveSelection(tileEntity, container, selections);
			close();
		}
	}

}
