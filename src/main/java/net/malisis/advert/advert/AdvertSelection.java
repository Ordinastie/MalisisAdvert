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

package net.malisis.advert.advert;

import io.netty.buffer.ByteBuf;
import net.malisis.core.renderer.icon.MalisisIcon;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Ordinastie
 *
 */
public class AdvertSelection
{
	public int id;
	public IIcon icon;
	public float u = 0;
	public float v = 0;
	public float U = 1;
	public float V = 1;

	public AdvertSelection(int id, float u, float v, float U, float V)
	{
		this.id = id;
		setUVs(u, v, U, V);
	}

	public AdvertSelection(int id)
	{
		this(id, 0, 0, 1, 1);
	}

	public void setUVs(float u, float v, float U, float V)
	{
		this.u = Math.max(0, u);
		this.v = Math.max(0, v);
		this.U = Math.min(1, U);
		this.V = Math.min(1, V);
	}

	public ClientAdvert getAdvert()
	{
		return ClientAdvert.get(id);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon()
	{
		if (icon == null)
			icon = new MalisisIcon("AS." + id, u, v, U, V);
		return icon;
	}

	public int height()
	{
		Advert ad = getAdvert();
		if (ad == null)
			return 1;
		return ad.getHeight();
	}

	public int width()
	{
		Advert ad = getAdvert();
		if (ad == null)
			return 1;
		return ad.getWidth();
	}

	public void setPixels(int x, int y, int X, int Y)
	{
		setUVs(getU(x), getV(y), getU(X), getV(Y));
	}

	public float getU(int x)
	{
		return (float) x / width();
	}

	public float getV(int y)
	{
		return (float) y / height();
	}

	public int getX(float u)
	{
		return Math.round(width() * u);
	}

	public int getY(float v)
	{
		return Math.round(height() * v);
	}

	public void toNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("advert", id);
		nbt.setFloat("u", u);
		nbt.setFloat("v", v);
		nbt.setFloat("U", U);
		nbt.setFloat("V", V);
	}

	public void toBytes(ByteBuf buf)
	{
		if (getAdvert() == null)
			return;

		buf.writeInt(id);
		buf.writeFloat(u);
		buf.writeFloat(v);
		buf.writeFloat(U);
		buf.writeFloat(V);

	}

	public static AdvertSelection fromNBT(NBTTagCompound nbt)
	{
		if (!nbt.hasKey("advert"))
			return null;

		AdvertSelection as = new AdvertSelection(nbt.getInteger("advert"), nbt.getFloat("u"), nbt.getFloat("v"), nbt.getFloat("U"),
				nbt.getFloat("V"));
		return as;
	}

	public static AdvertSelection fromBytes(ByteBuf buf)
	{
		AdvertSelection as = new AdvertSelection(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
		return as;
	}
}
