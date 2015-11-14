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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.malisis.advert.model.AdvertModel.IModelVariant;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.MalisisRegistry;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.icon.IIconRegister;
import net.malisis.core.renderer.model.MalisisModel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

/**
 * @author Ordinastie
 *
 */
public abstract class AdvertModel<T extends IModelVariant> implements IIconRegister
{
	private static Map<String, AdvertModel> registry = new HashMap<>();

	protected String id;
	protected String name;
	protected int availableSlots = 1;
	protected float width;
	protected float height;
	protected ResourceLocation objFile;
	protected ResourceLocation placeHolder;
	protected MalisisModel model;

	public AdvertModel()
	{}

	//#region Getters/Setters
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getAvailableSlots()
	{
		return availableSlots;
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		this.width = width;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public ResourceLocation getObjFile()
	{
		return objFile;
	}

	public void setObjFile(ResourceLocation objFile)
	{
		this.objFile = objFile;
	}

	public ResourceLocation getPlaceHolder()
	{
		return placeHolder;
	}

	public void setPlaceHolder(ResourceLocation placeHolder)
	{
		this.placeHolder = placeHolder;
	}

	public MalisisModel getModel()
	{
		return model;
	}

	public void setModel(MalisisModel model)
	{
		this.model = model;
	}

	//#end Getters/Setters

	public void loadModelFile()
	{
		if (objFile == null)
			return;

		model = new MalisisModel(objFile);
	}

	public void writeToNBT(AdvertTileEntity tileEntity, NBTTagCompound tagCompound)
	{}

	public void readFromNBT(AdvertTileEntity te, NBTTagCompound tagCompound)
	{}

	public abstract T defaultVariant(boolean wallMounted);

	public abstract AxisAlignedBB[] getBoundingBox(T variant);

	public abstract int getGuiComponent(MalisisGui gui, UIContainer container, T variant);

	public abstract T getVariantFromGui(UIContainer container);

	public abstract void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, T variant);

	public abstract void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp, T variant);

	@Override
	public String toString()
	{
		return name;
	}

	public static AdvertModel getModel(String id)
	{
		AdvertModel model = registry.get(id);
		if (model == null)
			model = registry.get("panel");
		return model;
	}

	public static void register(AdvertModel model)
	{
		registry.put(model.getId(), model);
		MalisisRegistry.registerIconRegister(model);
		model.loadModelFile();
	}

	public static Collection<AdvertModel> list()
	{
		return registry.values();
	}

	public static interface IModelVariant
	{
		public boolean isWallMounted();

		public void readFromNBT(NBTTagCompound tagCompound);

		public void writeToNBT(NBTTagCompound tagCompound);

		public void fromBytes(ByteBuf buf);

		public void toBytes(ByteBuf buf);
	}
}
