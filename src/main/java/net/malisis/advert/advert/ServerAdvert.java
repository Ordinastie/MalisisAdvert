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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.network.AdvertDownloadMessage;
import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.lang3.StringUtils;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import cpw.mods.fml.common.network.ByteBufUtils;

/**
 * @author Ordinastie
 *
 */
public class ServerAdvert extends Advert
{
	private static ListeningExecutorService threadPool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
	private static ListenableFuture<Void> task;
	private static Set<EntityPlayerMP> listeners = new HashSet<>();

	private static SortedMap<Integer, ServerAdvert> adverts = new TreeMap<>();
	private static String advertDir = "adverts/";
	private static String advertListing = "listing.txt";
	private static File packDir = new File("./" + advertDir);
	private static int globalId = 0;

	protected File file;

	public ServerAdvert(int id, String name, String url)
	{
		super(id, name, url);
		setFile();
	}

	public File getFile()
	{
		return file;
	}

	private void setFile()
	{
		if (StringUtils.isEmpty(name))
			return;

		file = new File(advertDir, "" + id);
		if (file != null && file.exists())
		{
			size = file.length();
			BufferedImage img;
			try
			{
				img = ImageIO.read(new ByteArrayInputStream(Files.toByteArray(file)));
				if (img != null)
				{
					width = img.getWidth();
					height = img.getHeight();
				}
			}
			catch (IOException e)
			{
				MalisisAdvert.log.error("Could not get image infos for {}", this, e);
				return;
			}
		}
	}

	public boolean isDownloaded()
	{
		return file.exists();
	}

	@Override
	public void setInfos(String name, String url)
	{
		if (!this.url.equals(url) && file != null)
			file.delete();
		super.setInfos(name, url);
		setFile();
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
		if (file != null)
			file.delete();
		adverts.remove(id);
		writeListing();
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(id);
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, url);
		buf.writeLong(size);
		buf.writeInt(width);
		buf.writeInt(height);
	}

	public void downloadAdvert(EntityPlayerMP player)
	{
		listeners.add(player);
		if (task != null && !task.isDone())
			return;

		//MalisisCore.message("Downloading...");

		task = threadPool.submit(new Callable<Void>()
		{
			@Override
			public Void call() throws Exception
			{
				try
				{
					byte[] img = Resources.toByteArray(new URL(url));
					try (FileOutputStream fos = new FileOutputStream(file))
					{
						fos.write(img);
						fos.close();
					}
					catch (IOException e)
					{
						setError(e.getMessage());
						e.printStackTrace();
					}

				}
				catch (Exception e)
				{
					setError(e.getMessage());
					e.printStackTrace();
				}

				for (EntityPlayerMP player : listeners)
				{
					setFile();
					AdvertDownloadMessage.sendImageData(ServerAdvert.this, player);
				}
				listeners.clear();

				return null;
			}
		});
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
		if (advert == null)
		{
			if (create)
				advert = new ServerAdvert(globalId++, "", "");
			else
				MalisisAdvert.log.error("Cannot find Advert \"{}\" for server", id);
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
		try (BufferedReader br = new BufferedReader(new FileReader(listing)))
		{

			for (String line; (line = br.readLine()) != null;)
			{
				String[] parts = line.split(";");
				if (parts.length == 3)
				{
					int id = Integer.decode(parts[0]);
					adverts.put(id, new ServerAdvert(id, parts[1], parts[2]));
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
				bw.write(advert.id + ";" + advert.name + ";" + advert.url + "\r\n");
		}
		catch (IOException e)
		{
			MalisisAdvert.log.error("Could not write advert listing file : ", e);
		}
	}

}
