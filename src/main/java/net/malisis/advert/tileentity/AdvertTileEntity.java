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

import java.util.Arrays;

import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.model.ModelVariantContainer;
import net.malisis.core.util.TileEntityUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * @author Ordinastie
 *
 */
public class AdvertTileEntity extends TileEntity
{
	protected ModelVariantContainer<?> container;
	protected boolean wallMounted;
	protected AdvertSelection[] selectedAdverts = new AdvertSelection[0];

	public AdvertTileEntity()
	{
		setModelContainer(null);
	}

	public ModelVariantContainer<?> getModelContainer()
	{
		return container;
	}

	public void setModelContainer(ModelVariantContainer<?> container)
	{
		if (container == null)
			container = ModelVariantContainer.getDefaultContainer();
		this.container = container;

		this.selectedAdverts = Arrays.copyOf(selectedAdverts, container.getModel().getAvailableSlots());

		TileEntityUtils.notifyUpdate(this);

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

	public AdvertSelection getSelection(int index)
	{
		if (selectedAdverts.length == 0)
			return null;

		if (index < 0 || index >= container.getModel().getAvailableSlots())
			return null;

		return selectedAdverts[index];
	}

	public AdvertSelection[] getSelections()
	{
		return selectedAdverts;
	}

	public void addSelections(AdvertSelection[] selections)
	{
		for (int i = 0; i < selections.length; i++)
			addSelection(i, selections[i]);
	}

	public void addSelection(int index, AdvertSelection advertSelection)
	{
		if (index < 0 || index >= container.getModel().getAvailableSlots())
			return;

		selectedAdverts[index] = advertSelection;
	}

	public void removeSelection(int index)
	{
		addSelection(index, null);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);

		tagCompound.setBoolean("wall_mounted", wallMounted);

		container.toNBT(this, tagCompound);

		NBTTagList asList = new NBTTagList();
		for (int i = 0; i < selectedAdverts.length; i++)
		{
			if (selectedAdverts[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("index", (byte) i);
				selectedAdverts[i].toNBT(tag);
				asList.appendTag(tag);
			}
		}

		tagCompound.setTag("selections", asList);
		return tagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);

		wallMounted = tagCompound.getBoolean("wall_mounted");

		setModelContainer(ModelVariantContainer.fromNBT(this, tagCompound));

		NBTTagList nbttaglist = tagCompound.getTagList("selections", NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
			addSelection(tag.getByte("index"), AdvertSelection.fromNBT(tag));
		}
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		this.readFromNBT(packet.getNbtCompound());
		TileEntityUtils.updateGui(this);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return TileEntityUtils.getRenderingBounds(this);
	}
}
