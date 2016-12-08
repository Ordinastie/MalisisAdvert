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
import net.malisis.advert.model.AdvertModel.IModelVariant;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.renderer.RenderParameters;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * @author Ordinastie
 *
 */
public class ModelVariantContainer<T extends IModelVariant>
{
	private AdvertModel<T> model;
	private T variant;
	private boolean wallMounted = false;

	public ModelVariantContainer(AdvertModel<T> model, T variant, boolean wallMounted)
	{
		this.wallMounted = wallMounted;
		if (variant == null || variant.isWallMounted() != wallMounted)
			variant = model.defaultVariant(wallMounted);

		this.model = model;
		this.variant = variant;

		this.model = model;
		this.variant = variant;
	}

	public AdvertModel<T> getModel()
	{
		return model;
	}

	public T getVariant()
	{
		return variant;
	}

	public AxisAlignedBB[] getBoundingBox()
	{
		return model.getBoundingBox(variant);
	}

	public void renderBlock(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp)
	{
		model.renderBlock(renderer, tileEntity, rp, variant);
	}

	public void renderTileEntity(AdvertRenderer renderer, AdvertTileEntity tileEntity, RenderParameters rp)
	{
		model.renderTileEntity(renderer, tileEntity, rp, variant);
	}

	public int getGuiComponent(MalisisGui gui, UIContainer<?> container)
	{
		return model.getGuiComponent(gui, container, variant, wallMounted);
	}

	public void toNBT(AdvertTileEntity te, NBTTagCompound nbt)
	{
		nbt.setString("model", model.getId());
		model.writeToNBT(te, nbt);

		if (variant != null)
			variant.writeToNBT(nbt);
	}

	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, model.getId());
		if (variant != null)
			variant.toBytes(buf);
	}

	public static <T extends IModelVariant> ModelVariantContainer<T> fromNBT(AdvertTileEntity te, NBTTagCompound nbt)
	{
		@SuppressWarnings("unchecked")
		AdvertModel<T> model = (AdvertModel<T>) AdvertModel.getModel(nbt.getString("model"));
		model.readFromNBT(te, nbt);

		T variant = model.defaultVariant(te.isWallMounted());
		if (variant != null)
			variant.readFromNBT(nbt);

		return new ModelVariantContainer<>(model, variant, te.isWallMounted());
	}

	public static <T extends IModelVariant> ModelVariantContainer<T> fromGui(UISelect<AdvertModel<?>> selModel, UIContainer<?> modelCont, boolean isWallMounted)
	{
		@SuppressWarnings("unchecked")
		AdvertModel<T> model = (AdvertModel<T>) selModel.getSelectedValue();
		T variant = model.getVariantFromGui(modelCont);

		return new ModelVariantContainer<>(model, variant, isWallMounted);
	}

	public static <T extends IModelVariant> ModelVariantContainer<T> fromBytes(ByteBuf buf)
	{
		@SuppressWarnings("unchecked")
		AdvertModel<T> model = (AdvertModel<T>) AdvertModel.getModel(ByteBufUtils.readUTF8String(buf));
		T variant = model.defaultVariant(false);
		if (variant != null)
			variant.fromBytes(buf);

		//trust variant wallMounted is coherent with te state
		return new ModelVariantContainer<>(model, variant, variant != null ? variant.isWallMounted() : false);
	}

	public static ModelVariantContainer<?> getDefaultContainer(boolean wallMounted)
	{
		return new ModelVariantContainer<>(AdvertModel.getModel(null), null, wallMounted);
	}
}
