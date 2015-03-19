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

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.network.AdvertDownloadMessage;
import net.malisis.advert.network.AdvertListMessage;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.util.Timer;
import cpw.mods.fml.common.network.ByteBufUtils;

/**
 * @author Ordinastie
 *
 */
public class ClientAdvert extends Advert
{
	private static SortedMap<Integer, ClientAdvert> adverts = new TreeMap<>();
	private static boolean pending = false;
	private static Timer timer = new Timer();

	private GuiTexture texture;

	public ClientAdvert(int id, String name, String url)
	{
		super(id, name, url);
	}

	@Override
	public void setInfos(String name, String url)
	{
		if (!this.url.equals(url) && texture != null)
		{
			texture.delete();
			texture = null;
		}
		super.setInfos(name, url);
	}

	public GuiTexture getTexture()
	{
		if (texture == null)
			download();

		return texture;
	}

	public void setTexture(BufferedImage image, long size)
	{
		texture = new GuiTexture(image, name);
		this.size = size;
		width = image.getWidth();
		height = image.getHeight();
	}

	@Override
	public void delete()
	{
		super.delete();
		if (texture != null)
			texture.delete();
		adverts.remove(id);
	}

	@Override
	public void save()
	{
		super.save();
		adverts.put(id, this);
	}

	public void download()
	{
		if (isPending())
			return;

		setPending(true);
		AdvertDownloadMessage.queryDownload(this);
	}

	static
	{
		queryAdvertList();
	}

	public static ClientAdvert get(int id)
	{
		return get(id, false);
	}

	public static ClientAdvert get(int id, boolean create)
	{
		ClientAdvert advert = adverts.get(id);
		if (advert == null)
		{
			if (create)
				advert = new ClientAdvert(id, "", "");
			else if (!isPending())
				MalisisAdvert.log.error("Cannot find Advert \"{}\" for client", id);
		}

		return advert;
	}

	public static Collection<ClientAdvert> listAdverts()
	{
		return adverts.values();
	}

	public static void setPending(boolean b)
	{
		pending = b;
		if (pending)
			timer.start();
	}

	public static boolean isPending()
	{
		if (timer.elapsedTime() > 5000)
			pending = false;
		return pending;
	}

	public static void queryAdvertList()
	{
		if (isPending())
			return;
		setPending(true);
		adverts.clear();
		AdvertListMessage.queryList();
	}

	public static void setAdvertList(ClientAdvert[] ads)
	{
		System.out.println("Setting " + ads.length + " adverts for client.");
		for (ClientAdvert advert : ads)
			adverts.put(advert.getId(), advert);
	}

	public static ClientAdvert fromBytes(ByteBuf buf)
	{
		int id = buf.readInt();
		ClientAdvert advert = ClientAdvert.get(id, true);
		advert.setInfos(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
		advert.setData(buf.readLong(), buf.readInt(), buf.readInt());

		return advert;
	}
}
