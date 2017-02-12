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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import io.netty.buffer.ByteBuf;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.network.AdvertDownloadMessage;
import net.malisis.core.util.Silenced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * @author Ordinastie
 *
 */
public class ServerAdvert extends Advert
{
	private static File packDir = new File("./" + advertDir);
	private static ListeningExecutorService threadPool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
	private static ListenableFuture<Void> task;
	private static Set<EntityPlayer> listeners = new HashSet<>();

	private static SortedMap<Integer, ServerAdvert> adverts = new TreeMap<>();

	private static String advertListing = "listing.txt";
	private static int globalId = 1;

	public ServerAdvert(int id)
	{
		super(id);
	}

	@Override
	public void setInfos(String name, String url)
	{
		boolean newUrl = this.url != null && !StringUtils.equals(this.url, url);
		if (newUrl && file != null)
		{
			file.delete();
			file = null;
		}

		super.setInfos(name, url);

		if (newUrl)
			downloadFile();
	}

	@Override
	public void save()
	{
		super.save();
		adverts.put(id, this);
		writeListing();
	}

	@Override
	public void delete()
	{
		super.delete();
		adverts.remove(id);
		writeListing();
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(id);
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, url);
		ByteBufUtils.writeUTF8String(buf, hash != null ? hash : "");
		buf.writeLong(size);
		buf.writeInt(width);
		buf.writeInt(height);

	}

	public void downloadAdvert(EntityPlayer player)
	{
		listeners.add(player);
		downloadFile();
	}

	private boolean checkOldFile()
	{
		File f = new File(advertDir, "" + id);
		if (!f.exists())
			return false;

		byte[] img = Silenced.get(() -> Files.toByteArray(f));
		f.delete();

		writeFile(img);
		writeListing();
		return true;
	}

	@Override
	protected void downloadFile()
	{
		if (checkOldFile())
			return;

		if (task != null && !task.isDone())
			return;

		task = threadPool.submit(() -> {
			byte[] img = null;
			try
			{
				img = Resources.toByteArray(new URL(url));
			}
			catch (Exception e)
			{
				setError(e.getMessage());
				MalisisAdvert.log.error(e);
			}

			writeFile(img);
			writeListing();
			sendImageData();
			return null;
		});
	}

	private void sendImageData()
	{
		for (EntityPlayer player : listeners)
			AdvertDownloadMessage.sendImageData(ServerAdvert.this, player);
		listeners.clear();
	}

	static
	{
		if (!packDir.exists())
			packDir.mkdir();

		readListing();
	}

	public static ServerAdvert get(int id)
	{
		return get(id, false);
	}

	public static ServerAdvert get(int id, boolean create)
	{
		ServerAdvert advert = adverts.get(id);
		if (advert == null || id == 0)
		{
			if (create)
				advert = new ServerAdvert(globalId++);
			else
				MalisisAdvert.log.error("Cannot find Advert {} for server", id);
		}

		return advert;
	}

	public static Collection<ServerAdvert> listAdverts()
	{
		return adverts.values();
	}

	public static int getAdvertCount()
	{
		return adverts.size();
	}

	public static void readListing()
	{
		File listing = new File(packDir, advertListing);
		if (!listing.exists())
			return;
		try (BufferedReader br = new BufferedReader(new FileReader(listing)))
		{

			for (String line; (line = br.readLine()) != null;)
			{
				String[] parts = line.split(";");
				if (parts.length == 3 || parts.length == 4)
				{
					int id = Integer.decode(parts[0]);
					ServerAdvert advert = new ServerAdvert(id);
					advert.setInfos(parts[1], parts[2]);
					if (parts.length == 4)
						advert.setHash(parts[3]);

					adverts.put(id, advert);
					if (id >= globalId)
						globalId = id + 1;
				}
			}
		}
		catch (IOException e)
		{
			MalisisAdvert.log.error("Could not read advert listing file : ", e);
		}
	}

	public static void writeListing()
	{
		File listing = new File(packDir, advertListing);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(listing)))
		{
			for (ServerAdvert advert : adverts.values())
				bw.write(advert.id + ";" + advert.name + ";" + advert.url + ";" + advert.hash + "\r\n");
		}
		catch (IOException e)
		{
			MalisisAdvert.log.error("Could not write advert listing file : ", e);
		}
	}

}
