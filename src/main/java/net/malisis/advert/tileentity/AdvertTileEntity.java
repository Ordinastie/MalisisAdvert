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

package net.malisis.advert.tileentity;

import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.model.AdvertModel;
import net.malisis.advert.model.AdvertModel.IModelVariant;
import net.malisis.core.block.BoundingBoxType;
import net.malisis.core.block.MalisisBlock;
import net.malisis.core.util.AABBUtils;
import net.malisis.core.util.TileEntityUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * @author Ordinastie
 *
 */
public class AdvertTileEntity extends TileEntity
{
	protected AdvertModel model;
	protected IModelVariant variant;
	protected boolean wallMounted;
	protected int availableSlots = 1;
	protected AdvertSelection[] selectedAdverts;
	protected int currentAdvert;

	public AdvertTileEntity()
	{
		selectedAdverts = new AdvertSelection[availableSlots];
		setModel(null, null);
	}

	public AdvertModel getModel()
	{
		return model;
	}

	public IModelVariant getModelVariant()
	{
		return variant;
	}

	public void setModel(AdvertModel model, IModelVariant variant)
	{
		if (model == null)
			model = AdvertModel.getModel(null);

		if (variant == null || variant.isWallMounted() != isWallMounted())
			variant = model.defaultVariant(isWallMounted());

		this.model = model;
		this.variant = variant;
	}

	public void setWallMounted(boolean wallMounted)
	{
		this.wallMounted = wallMounted;
	}

	public boolean isWallMounted()
	{
		return wallMounted;
	}

	public int availableSlots()
	{
		return 1;
	}

	public int selectedAdverts()
	{
		int c = 0;
		for (AdvertSelection as : selectedAdverts)
			if (as != null)
				c++;
		return c;
	}

	public AdvertSelection getCurrentSelection()
	{
		return selectedAdverts[currentAdvert];
	}

	public void addSelection(int index, AdvertSelection advertSelection)
	{
		if (index < 0 || index >= availableSlots)
			return;

		selectedAdverts[index] = advertSelection;
		if (getWorld() != null)
			getWorld().markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void removeSelection(int index)
	{
		addSelection(index, null);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);

		tagCompound.setBoolean("wall_mounted", wallMounted);
		if (model != null)
		{
			tagCompound.setString("model", model.getId());
			model.writeToNBT(this, tagCompound);
		}
		if (variant != null)
			variant.writeToNBT(tagCompound);

		AdvertSelection as = selectedAdverts[0];
		if (as == null)
			return;

		as.toNBT(tagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);

		wallMounted = tagCompound.getBoolean("wall_mounted");

		String modelId;
		if (tagCompound.hasKey("model", NBT.TAG_INT))
			modelId = new String[] { "PANEL_WALL", "PANEL_SMALL_FOOT", "PANEL_FULL_FOOT" }[tagCompound.getInteger("model")];
		else
			modelId = tagCompound.getString("model");

		AdvertModel model = AdvertModel.getModel(modelId);
		model.readFromNBT(this, tagCompound);

		IModelVariant variant = model.defaultVariant(isWallMounted());
		variant.readFromNBT(tagCompound);

		setModel(model, variant);

		addSelection(0, AdvertSelection.fromNBT(tagCompound));
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		this.readFromNBT(packet.getNbtCompound());
		TileEntityUtils.updateGui(this);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AABBUtils
				.combine(((MalisisBlock) getBlockType()).getBoundingBox(getWorld(), xCoord, yCoord, zCoord, BoundingBoxType.RENDER))
				.offset(xCoord, yCoord, zCoord);
	}

}
