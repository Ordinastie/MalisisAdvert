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

import net.malisis.advert.advert.Advert;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.element.SimpleGuiShape;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

/**
 * @author Ordinastie
 *
 */
public class AdvertComponent extends UIContainer
{
	private static IIcon deleteIcon = AdvertManagerGui.icons.getIcon(32, 0, 32, 32);
	private static IIcon editIcon = AdvertManagerGui.icons.getIcon(0, 32, 32, 32);
	private static IIcon viewIcon = AdvertManagerGui.icons.getIcon(32, 32, 32, 32);

	private Advert advert;
	private UIButton edit;
	private UIButton delete;
	private UIButton view;

	public AdvertComponent(MalisisGui gui, Advert advert)
	{

		super(gui);
		this.advert = advert;

		setSize(0, 20);
		setPadding(2, 2);

		UIImage imgView = new UIImage(gui, AdvertManagerGui.icons, viewIcon).setSize(10, 10);
		UIImage imgEdit = new UIImage(gui, AdvertManagerGui.icons, editIcon).setSize(10, 10);
		UIImage imgDelete = new UIImage(gui, AdvertManagerGui.icons, deleteIcon).setSize(10, 10);

		view = new UIButton(gui, imgView).setPosition(-34, 0, Anchor.RIGHT).setSize(16, 16).register(this);
		edit = new UIButton(gui, imgEdit).setPosition(-17, 0, Anchor.RIGHT).setSize(16, 16).register(this);
		delete = new UIButton(gui, imgDelete).setPosition(0, 0, Anchor.RIGHT).setSize(16, 16).register(this);

		view.setDisabled(!advert.isDownloaded());

		add(view, edit, delete);

		shape = new SimpleGuiShape();

	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		int color = isHovered() ? 0x999999 : 0xBBBBBB;
		renderer.disableTextures();
		rp.colorMultiplier.set(color);
		renderer.drawShape(shape, rp);
		renderer.next(GL11.GL_LINE_LOOP);

		shape.resetState();

		shape.setSize(getWidth(), getHeight());
		rp.colorMultiplier.set(0x000000);
		renderer.drawShape(shape, rp);

		renderer.next(GL11.GL_QUADS);
		renderer.enableTextures();
	}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		renderer.drawText(advert.getName(), 5, 5, 0xFFFFFF, true);

		view.draw(renderer, mouseX, mouseY, partialTick);
		edit.draw(renderer, mouseX, mouseY, partialTick);
		delete.draw(renderer, mouseX, mouseY, partialTick);
	}

	@Subscribe
	public void onClick(UIButton.ClickEvent event)
	{
		if (event.getComponent() == view)
			((AdvertManagerGui) getGui()).view(advert);
		else if (event.getComponent() == edit)
			((AdvertManagerGui) getGui()).edit(advert);
		else if (event.getComponent() == delete)
			((AdvertManagerGui) getGui()).delete(advert);
	}

}
