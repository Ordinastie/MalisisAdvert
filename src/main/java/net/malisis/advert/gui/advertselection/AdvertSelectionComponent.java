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

import net.malisis.advert.advert.Advert;
import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITextField;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;

/**
 * @author Ordinastie
 *
 */
public class AdvertSelectionComponent extends UIContainer<AdvertSelectionComponent>
{
	private AdvertSelection advertSelection;

	private UISelect<ClientAdvert> selAdvert;
	private UITextField fromX;
	private UITextField fromY;
	private UITextField toX;
	private UITextField toY;

	public AdvertSelectionComponent(MalisisGui gui)
	{
		super(gui);

		createSelect(gui);
		createClipping(gui);

		//UIImage imgView = new UIImage(gui, AdvertManagerGui.icons, AdvertManagerGui.viewIcon).setSize(10, 10);
		//UIButton btnView = new UIButton(gui, imgView).setAnchor(Anchor.RIGHT).register(this);

		//add(btnView);
	}

	private void createSelect(MalisisGui gui)
	{
		//select advert
		UILabel labelAdvert = new UILabel(gui, "malisisadvert.gui.advert");
		Function<ClientAdvert, String> labelFunc = new Function<ClientAdvert, String>()
		{
			@Override
			public String apply(ClientAdvert advert)
			{
				return advert.getName();
			}
		};
		selAdvert = new UISelect<ClientAdvert>(gui, 100, ClientAdvert.listAdverts()).setPosition(0, 12).register(this);
		selAdvert.setLabelFunction(labelFunc);

		add(labelAdvert);
		add(selAdvert);
	}

	private void createClipping(MalisisGui gui)
	{
		int y = 27;
		int w = 30;
		//clipping textfields
		UILabel labelSelection = new UILabel(gui, "malisisadvert.gui.clip").setPosition(0, y);
		y += 12;
		int x = 0;

		//FROM
		UILabel from = new UILabel(gui, "malisisadvert.gui.from").setPosition(x, y + 3);
		x += from.getWidth() + 2;
		fromX = new UITextField(gui, false).setPosition(x, y).setSize(w, 0);
		x += w + 2;
		UILabel commaFrom = new UILabel(gui, ", ").setPosition(x, y + 3);
		x += commaFrom.getWidth();
		fromY = new UITextField(gui, false).setPosition(x, y).setSize(w, 0);
		x += w + 2;

		//TO
		UILabel to = new UILabel(gui, "malisisadvert.gui.to").setPosition(x, y + 3);
		x += to.getWidth() + 2;
		toX = new UITextField(gui, false).setPosition(x, y).setSize(w, 0);
		x += w + 2;
		UILabel commaTo = new UILabel(gui, ", ").setPosition(x, y + 3);
		x += commaTo.getWidth();
		toY = new UITextField(gui, false).setPosition(x, y).setSize(w, 0);
		x += w + 2;

		add(labelSelection);

		add(from, fromX, commaFrom, fromY);
		add(to, toX, commaTo, toY);
	}

	public AdvertSelection getAdvertSelection()
	{
		return advertSelection;
	}

	public void setAdvertSelection(AdvertSelection advertSelection)
	{
		this.advertSelection = advertSelection;
		updateComponents();
	}

	public void updateComponents()
	{
		if (advertSelection == null)
		{
			selAdvert.setSelectedOption((ClientAdvert) null);
			fromX.setText("");
			fromY.setText("");
			toX.setText("");
			toY.setText("");
		}
		else
		{
			AdvertSelection as = advertSelection;
			selAdvert.setSelectedOption(as.getAdvert());
			fromX.setText("" + as.getX(as.u));
			fromY.setText("" + as.getY(as.v));
			toX.setText("" + as.getX(as.U));
			toY.setText("" + as.getY(as.V));
		}
	}

	public void saveUVs()
	{
		if (advertSelection == null)
			return;
		int x = 0, y = 0, X = 0, Y = 0;
		try
		{
			x = Integer.decode(fromX.getText());
			y = Integer.decode(fromY.getText());
			X = Integer.decode(toX.getText());
			Y = Integer.decode(toY.getText());
		}
		catch (NumberFormatException e)
		{}

		advertSelection.setPixels(x, y, X, Y);
	}

	@Subscribe
	public void onSelect(UISelect.SelectEvent<Advert> event)
	{
		setAdvertSelection(new AdvertSelection(event.getNewValue().getId()));
		((AdvertSelectionGui) getGui()).viewAdvertSelection(advertSelection);
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event)
	{
		((AdvertSelectionGui) getGui()).viewAdvertSelection(advertSelection);
	}
}
