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

import io.netty.buffer.ByteBuf;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.model.TriangularColumn.Variant;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.MalisisCore;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.interaction.UICheckBox;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.animation.AnimationRenderer;
import net.malisis.core.renderer.animation.transformation.Rotation;
import net.malisis.core.renderer.element.Face;
import net.malisis.core.renderer.element.Shape;
import net.malisis.core.renderer.icon.MalisisIcon;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

/**
 * @author Ordinastie
 *
 */
public class TriangularColumn extends AdvertModel<Variant>
{
	private Shape base;
	private Shape topBottom;
	private Shape panels;
	@SideOnly(Side.CLIENT)
	private MalisisIcon icon;

	private AnimationRenderer ar;
	private Rotation rotation;

	public TriangularColumn()
	{
		this.id = "triangular_column";
		this.name = id;
		this.availableSlots = 3;
		this.width = 1.4F;
		this.height = 2.5F;
		this.objFile = new ResourceLocation(MalisisAdvert.modid, "models/triangular_column.obj");
		this.placeHolder = new ResourceLocation(MalisisAdvert.modid, "textures/blocks/MA23.png");

		if (MalisisCore.isClient())
			icon = new MalisisIcon(MalisisAdvert.modid + ":blocks/triangular_column");
	}

	@Override
	public void loadModelFile()
	{
		super.loadModelFile();

		base = model.getShape("base");
		topBottom = model.getShape("PanelsTopBottom");
		panels = model.getShape("Panels");
		ar = new AnimationRenderer();
		rotation = new Rotation(360, 0, 1, 0, 0, 0, 0).forTicks(600).loop(-1);
	}

	@Override
	public Variant defaultVariant(boolean wallMounted)
	{
		return new Variant();
	}

	@Override
	public void registerIcons(TextureMap map)
	{
		icon = icon.register(map);
	}

	@Override
	public AxisAlignedBB[] getBoundingBox(Variant variant)
	{
		AxisAlignedBB aabb = new AxisAlignedBB(-0.2F, 0, 0.9F, 1.2F, 3, -0.3F);
		return new AxisAlignedBB[] { aabb };
	}

	@Override
	public int getGuiComponent(MalisisGui gui, UIContainer<?> container, Variant variant)
	{
		UICheckBox cb = new UICheckBox(gui, "malisisadvert.gui.model.triangular_column.rotate").setName("rotate");
		cb.setChecked(variant.rotate);
		container.add(cb);

		return 15;
	}

	@Override
	public Variant getVariantFromGui(UIContainer<?> container)
	{
		Variant variant = new Variant();
		UICheckBox cb = (UICheckBox) container.getComponent("rotate");
		variant.rotate = cb.isChecked();

		return variant;
	}

	@Override
	public void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, Variant variant)
	{
		rp.icon.set(icon);
		renderer.drawShape(base, rp);
	}

	@Override
	public void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, Variant variant)
	{
		//topBottom.resetState();
		if (variant.rotate)
			ar.animate(topBottom, rotation);

		renderer.next(GL11.GL_TRIANGLES);
		rp.icon.set(icon);
		renderer.drawShape(topBottom, rp);
		renderer.next(GL11.GL_QUADS);

		//render advert faces :
		if (variant.rotate)
		{
			ar.animate(panels, rotation);
			panels.applyMatrix();
		}

		for (int i = 0; i < panels.getFaces().length; i++)
		{
			Face face = panels.getFaces()[i];
			AdvertSelection as = tileEntity.getSelection(i);
			renderer.renderAdvertFace(face, as);
		}
	}

	public static class Variant implements AdvertModel.IModelVariant
	{
		boolean rotate = true;

		@Override
		public boolean isWallMounted()
		{
			return false;
		}

		@Override
		public void readFromNBT(NBTTagCompound tagCompound)
		{
			rotate = tagCompound.hasKey("rotate") ? tagCompound.getBoolean("rotate") : true;
		}

		@Override
		public void writeToNBT(NBTTagCompound tagCompound)
		{
			tagCompound.setBoolean("rotate", rotate);
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			rotate = buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeBoolean(rotate);
		}
	}
}
