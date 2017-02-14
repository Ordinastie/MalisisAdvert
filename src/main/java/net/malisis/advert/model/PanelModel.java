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

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.model.PanelModel.Variant;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.MalisisCore;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.animation.AnimationRenderer;
import net.malisis.core.renderer.animation.transformation.ChainedTransformation;
import net.malisis.core.renderer.animation.transformation.ITransformable;
import net.malisis.core.renderer.animation.transformation.Transformation;
import net.malisis.core.renderer.element.Face;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.element.Vertex;
import net.malisis.core.renderer.icon.Icon;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Ordinastie
 *
 */
public class PanelModel extends AdvertModel<Variant>
{
	public enum FootType
	{
		WALL,
		SMALL,
		FULL,
	}

	private Shape smallFoot;
	private Shape fullFoot;
	private Shape panel;
	private Shape displayTop;
	private Shape displayBottom;
	@SideOnly(Side.CLIENT)
	private Icon panelIcon;

	private AnimationRenderer ar;
	private ChainedTransformation topTransform;
	private ChainedTransformation bottomTransform;
	private Icon baseIcon;
	private PanelData panelData;

	public PanelModel()
	{
		this.id = "panel";
		this.name = id;
		this.availableSlots = 2;
		float border = 3 / 16F;
		this.width = 2 - 2 * border;
		this.height = 3 - 2 * border;
		this.objFile = new ResourceLocation(MalisisAdvert.modid, "models/panel.obj");
		this.placeHolder = new ResourceLocation(MalisisAdvert.modid, "textures/blocks/MA23.png");

		if (MalisisCore.isClient())
			panelIcon = Icon.from(MalisisAdvert.modid + ":blocks/panel");
	}

	@Override
	public boolean canBeWallMounted()
	{
		return true;
	}

	@Override
	public void loadModelFile()
	{
		super.loadModelFile();
		smallFoot = model.getShape("SmallFoot");
		fullFoot = model.getShape("FullFoot");
		panel = model.getShape("Panel");
		displayTop = model.getShape("Advert");
		displayBottom = new Shape(displayTop);
		model.addShape(displayBottom);

		ar = new AnimationRenderer();
		baseIcon = new Icon();
		panelData = new PanelData();

		int speed = 60;//3s
		int delay = 200;//10s

		PanelTransform pt1 = new PanelTransform(true).forTicks(speed, delay).movement(Transformation.SINUSOIDAL);
		PanelTransform pt2 = new PanelTransform(true).forTicks(speed, delay).reversed(true).movement(Transformation.SINUSOIDAL);
		topTransform = new ChainedTransformation(pt1, pt2).loop(-1);

		pt1 = new PanelTransform(false).forTicks(speed, delay).movement(Transformation.SINUSOIDAL);
		pt2 = new PanelTransform(false).forTicks(speed, delay).reversed(true).movement(Transformation.SINUSOIDAL);
		bottomTransform = new ChainedTransformation(pt1, pt2).loop(-1);
	}

	@Override
	public Variant defaultVariant(boolean wallMounted)
	{
		Variant variant = new Variant();
		variant.type = wallMounted ? FootType.WALL : FootType.SMALL;
		return variant;
	}

	@Override
	public AxisAlignedBB[] getBoundingBox(Variant variant)
	{
		float w = 3F / 16F;
		AxisAlignedBB foot = new AxisAlignedBB(0.375F, 0, 0.5F, 0.625F, 1, 0.5F + w);
		AxisAlignedBB panel = new AxisAlignedBB(-0.5F, 0, 0.5F, 1.5F, variant.type == FootType.FULL ? 4 : 3, 0.5F + w);

		if (variant.type == FootType.WALL)
			panel = panel.offset(0, 0, -0.5F);

		AxisAlignedBB[] aabbs;
		if (variant.type == FootType.SMALL)
		{
			panel = panel.offset(0, 1, 0);
			aabbs = new AxisAlignedBB[] { foot, panel };
		}
		else
			aabbs = new AxisAlignedBB[] { panel };

		return aabbs;
	}

	@Override
	public int getGuiComponent(MalisisGui gui, UIContainer<?> container, Variant variant, boolean wallMounted)
	{
		UILabel label = new UILabel(gui, "malisisadvert.gui.model.panel.type");
		UISelect<FootType> select = new UISelect<>(gui, 150, Arrays.asList(FootType.values())).setName("footType");
		select.setLabelPattern("malisisadvert.gui.model.panel.%s");
		select.setPosition(0, 12);
		select.setSelectedOption(variant.type);
		select.setDisablePredicate(t -> wallMounted == (t != FootType.WALL));

		container.add(label);
		container.add(select);

		return 30;
	}

	@Override
	public Variant getVariantFromGui(UIContainer<?> container)
	{
		Variant variant = new Variant();
		@SuppressWarnings("unchecked")
		UISelect<FootType> sel = (UISelect<FootType>) container.getComponent("footType");
		variant.type = sel.getSelectedValue();

		return variant;
	}

	@Override
	public void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, Variant variant)
	{
		rp.icon.set(panelIcon);
		if (variant.type == FootType.SMALL)
			renderer.drawShape(smallFoot, rp);
		else if (variant.type == FootType.FULL)
			renderer.drawShape(fullFoot, rp);
		else
			panel.translate(0, -1, -.5F);

		renderer.drawShape(panel, rp);
	}

	@Override
	public void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, Variant variant)
	{
		if (variant.type == FootType.WALL)
		{
			displayTop.translate(0, -1, -.5F);
			displayBottom.translate(0, -1, -.5F);
		}

		boolean b = tileEntity.getSelection(1) != null;

		renderAdvert(renderer, displayTop, tileEntity.getSelection(0), topTransform, b);
		//displayBottom.translate(2, 0, 0);
		if (b)
			renderAdvert(renderer, displayBottom, tileEntity.getSelection(1), bottomTransform, b);
	}

	private void renderAdvert(AdvertRenderer renderer, Shape shape, AdvertSelection as, ChainedTransformation transform, boolean anim)
	{
		panelData.set(shape, as);
		if (anim)
			ar.animate(panelData, transform);
		shape.applyMatrix();
		renderer.renderAdvertFace(shape.getFaces()[0], as, panelData.icon);
	}

	public static class Variant implements AdvertModel.IModelVariant
	{
		public FootType type = FootType.SMALL;

		@Override
		public boolean isWallMounted()
		{
			return type == FootType.WALL;
		}

		@Override
		public void readFromNBT(NBTTagCompound tagCompound)
		{
			type = tagCompound.hasKey("footType") ? FootType.values()[tagCompound.getInteger("footType")] : FootType.SMALL;
		}

		@Override
		public void writeToNBT(NBTTagCompound tagCompound)
		{
			tagCompound.setInteger("footType", type.ordinal());
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			type = FootType.values()[buf.readInt()];
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(type.ordinal());
		}
	}

	private class PanelData implements ITransformable
	{
		private double y, Y;
		private float v, V;
		private Shape shape;
		private Icon icon;

		public void set(Shape shape, AdvertSelection as)
		{
			this.shape = shape;
			Icon icon = as != null ? as.getIcon() : null;
			if (icon == null)
				icon = baseIcon;
			this.icon.copyFrom(icon);

			Face f = shape.getFaces()[0];
			y = f.getVertexes()[0].getY();
			Y = f.getVertexes()[2].getY();

			v = icon.getMinV();
			V = icon.getMaxV();
		}
	}

	private class PanelTransform extends Transformation<PanelTransform, PanelData>
	{
		private boolean isTop;

		public PanelTransform(boolean isTop)
		{
			this.isTop = isTop;
		}

		@Override
		public PanelTransform self()
		{
			return this;
		}

		@Override
		protected void doTransform(PanelData data, float comp)
		{
			if (isTop && comp == 0)
				return;

			if (!isTop && reversed && comp == 0)
				return;

			if (reversed)
				comp = 1 - comp;

			transformVertex(data, comp);
			transformIcon(data, comp);
		}

		private void transformVertex(PanelData data, float comp)
		{
			double amount = (data.Y - data.y) * comp;

			Face f = data.shape.getFaces()[0];
			Vertex[] vertexes = new Vertex[] { f.getVertexes()[isTop ? 0 : 2], f.getVertexes()[isTop ? 1 : 3] };

			for (Vertex v : vertexes)
				v.setY(data.y + amount);
		}

		private void transformIcon(PanelData data, float comp)
		{
			float amount = (data.V - data.v) * comp;

			float v = isTop ? data.v + amount : data.v;
			float V = isTop ? data.V : data.v + amount;

			data.icon.setUVs(data.icon.getMinU(), v, data.icon.getMaxU(), V);
		}

		@Override
		public float completion(long elapsedTime)
		{
			return super.completion(elapsedTime);
		}

	}

}
