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

package net.malisis.advert.model;

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.model.AdvertModel.IModelVariant;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.MalisisCore;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.animation.AnimationRenderer;
import net.malisis.core.renderer.animation.transformation.ChainedTransformation;
import net.malisis.core.renderer.animation.transformation.Rotation;
import net.malisis.core.renderer.animation.transformation.Transformation;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.icon.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Ordinastie
 *
 */
public class BillboardModel extends AdvertModel<IModelVariant>
{
	@SideOnly(Side.CLIENT)
	private Icon boardIcon;
	@SideOnly(Side.CLIENT)
	private Icon platformIcon;

	private Shape foot;
	private Shape billboard;
	private Shape platform;
	private ShiftedRotations transform;
	private AnimationRenderer ar;

	public BillboardModel()
	{
		this.id = "billboard";
		this.name = id;
		this.availableSlots = 3;
		float border = 2 / 16F;
		this.width = 10 - 2 * border;
		this.height = 5 - 2 * border;
		this.objFile = new ResourceLocation(MalisisAdvert.modid, "models/billboard.obj");
		this.placeHolder = new ResourceLocation(MalisisAdvert.modid, "textures/blocks/MA105.png");

		if (MalisisCore.isClient())
		{
			boardIcon = Icon.from(MalisisAdvert.modid + ":blocks/board");
			platformIcon = Icon.from(MalisisAdvert.modid + ":blocks/platform");
		}
	}

	@Override
	public void loadModelFile()
	{
		super.loadModelFile();
		foot = model.getShape("Foot");
		billboard = model.getShape("Board");
		platform = model.getShape("Platform");

		ar = new AnimationRenderer();

		int speed = 20;//1s
		int delay = 100;//5s

		Rotation r1 = new Rotation(0, -120).aroundAxis(0, 1, 0).forTicks(speed, delay).movement(Transformation.SINUSOIDAL);
		Rotation r2 = new Rotation(-120, -240).aroundAxis(0, 1, 0).forTicks(speed, delay).movement(Transformation.SINUSOIDAL);
		Rotation r3 = new Rotation(-240, -360).aroundAxis(0, 1, 0).forTicks(speed, delay).movement(Transformation.SINUSOIDAL);
		transform = new ShiftedRotations(r1, r2, r3);
		transform.loop(-1);
	}

	@Override
	public AdvertModel.IModelVariant defaultVariant(boolean wallMounted)
	{
		return null;
	}

	@Override
	public AxisAlignedBB[] getBoundingBox(AdvertModel.IModelVariant variant)
	{
		AxisAlignedBB foot = new AxisAlignedBB(.125F, 0, .25F, .875F, 5F, .75F);
		AxisAlignedBB billboard = new AxisAlignedBB(-4.5F, 5, .125F, 5.5F, 10F, .875F);
		AxisAlignedBB platform = new AxisAlignedBB(-4.5F, 5.0625F, .875F, 5.5F, 5.125F, 1.6875F);

		return new AxisAlignedBB[] { foot, billboard, platform };
	}

	@Override
	public int getGuiComponent(MalisisGui gui, UIContainer<?> container, AdvertModel.IModelVariant variant, boolean wallMounted)
	{
		return 0;
	}

	@Override
	public AdvertModel.IModelVariant getVariantFromGui(UIContainer<?> container)
	{
		return null;
	}

	@Override
	public void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, AdvertModel.IModelVariant variant)
	{
		rp.icon.set(boardIcon);
		renderer.drawShape(foot, rp);
		renderer.drawShape(billboard, rp);
		rp.icon.set(platformIcon);
		renderer.drawShape(platform, rp);
	}

	@Override
	public void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, AdvertModel.IModelVariant variant)
	{
		if (tileEntity.getSelection(1) == null && tileEntity.getSelection(2) == null)
		{
			Shape s = model.getShape("Plane");
			s.applyMatrix();
			renderer.renderAdvertFace(s.getFaces()[0], tileEntity.getSelection(0));
			return;
		}

		for (int i = 0; i <= 19; i++)
		{
			Shape s = model.getShape("Prism" + i);
			ar.animate(s, transform.shift(i));
			s.applyMatrix();
			for (int f = 0; f < 3; f++)
				renderer.renderAdvertFace(s.getFaces()[f], tileEntity.getSelection(2 - f));
		}
	}

	public class ShiftedRotations extends ChainedTransformation
	{
		private float shift = 0.48F;
		private float first = -4.05463F - 0.5F;

		public ShiftedRotations(Rotation... rotations)
		{
			addTransformations(rotations);
		}

		public ShiftedRotations shift(int n)
		{
			for (Transformation<?, ?> r : listTransformations)
			{
				//System.out.println("delay = " + (100 + n));
				((Rotation) r).offset(first + shift * n, 0, .66896F - 0.5F).delay(100);
			}
			return this;
		}
	}
}
