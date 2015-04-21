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
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.renderer.RenderParameters;
import net.malisis.core.renderer.model.MalisisModel;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * @author Ordinastie
 *
 */
public abstract class AdvertModel
{
	protected String id;
	protected String name;
	protected float width;
	protected float height;
	protected boolean isWallMounted = false;
	protected ResourceLocation objFile;
	protected ResourceLocation placeHolder;
	protected MalisisModel model;

	protected boolean loaded = false;

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

	public boolean isWallMounted()
	{
		return isWallMounted;
	}

	public void setWallMounted(boolean isWallMounted)
	{
		this.isWallMounted = isWallMounted;
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

	public boolean isLoaded()
	{
		return loaded;
	}

	public void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}

	//#end Getters/Setters

	public void loadModelFile()
	{
		if (objFile == null || loaded)
			return;

		model = new MalisisModel(objFile);

		loaded = true;
	}

	public void writeToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setString("model", id);
	}

	public void readFromNBT(NBTTagCompound tagCompound)
	{}

	public abstract void registerIcons(IIconRegister register);

	public abstract AxisAlignedBB[] getBoundingBox();

	public abstract void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp);

	public abstract void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp);

	@Override
	public String toString()
	{
		return name;
	}

	public static AdvertModel fromNBT(NBTTagCompound tagCompound)
	{
		String modelId;
		if (tagCompound.hasKey("model", NBT.TAG_INT))
			modelId = new String[] { "PANEL_WALL", "PANEL_SMALL_FOOT", "PANEL_FULL_FOOT" }[tagCompound.getInteger("model")];
		else
			modelId = tagCompound.getString("model");

		AdvertModel model = MalisisAdvert.getModel(modelId);
		if (model != null)
			model.readFromNBT(tagCompound);
		return model;
	}
}
