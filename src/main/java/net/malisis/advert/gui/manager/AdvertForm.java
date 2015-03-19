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

import net.malisis.advert.advert.Advert;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.minecraft.util.IIcon;

/**
 * @author Ordinastie
 *
 */
public class AdvertForm extends UIContainer<AdvertForm>
{
	private static IIcon deleteIcon = AdvertManagerGui.icons.getIcon(32, 0, 32, 32);
	private static IIcon saveIcon = AdvertManagerGui.icons.getIcon(0, 64, 32, 32);

	private UITextField tfName;
	private UITextField tfUrl;

	public AdvertForm(MalisisGui gui)
	{
		super(gui);

		createForm(gui);
		createSideButtons(gui);
	}

	private void createForm(MalisisGui gui)
	{
		UILabel lblName = new UILabel(gui, "malisisadvert.gui.advertname");
		UILabel lblUrl = new UILabel(gui, "malisisadvert.gui.adverturl").setPosition(0, 30);

		tfName = new UITextField(gui, null).setPosition(0, 10).setSize(-20, 12);
		tfUrl = new UITextField(gui, null).setPosition(0, 40).setSize(-20, 12);

		add(lblName, tfName, lblUrl, tfUrl);
	}

	private void createSideButtons(MalisisGui gui)
	{
		UIImage imgDelete = new UIImage(gui, AdvertManagerGui.icons, deleteIcon).setSize(10, 10);
		UIImage imgSave = new UIImage(gui, AdvertManagerGui.icons, saveIcon).setSize(10, 10);

		int y = 10;

		UIButton btnSave = new UIButton(gui, imgSave).setPosition(0, y, Anchor.RIGHT).setSize(16, 16).register(gui);
		btnSave.setName("advertSave");
		btnSave.setTooltip("malisisadvert.gui.save");

		UIButton btnDelete = new UIButton(gui, imgDelete).setPosition(0, y + 17, Anchor.RIGHT).setSize(16, 16).register(gui);
		btnDelete.setName("advertDelete");
		btnDelete.setTooltip("malisisadvert.gui.delete");

		add(btnSave, btnDelete);
	}

	public void fillForm(Advert advert)
	{
		tfName.setText(advert != null ? advert.getName() : "");
		tfUrl.setText(advert != null ? advert.getUrl() : "");
	}

	@Override
	public String getName()
	{
		return tfName.getText();
	}

	public String getUrl()
	{
		return tfUrl.getText();
	}
}
