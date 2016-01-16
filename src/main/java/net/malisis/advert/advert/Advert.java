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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.malisis.advert.MalisisAdvert;

import org.apache.commons.lang3.StringUtils;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

/**
 * @author Ordinastie
 *
 */
public abstract class Advert implements Comparable<Advert>
{
	protected static String advertDir = "adverts/";

	protected int id;
	protected String name;
	protected long size;
	protected int width;
	protected int height;
	protected String url;
	private String error;

	protected File file;
	protected String hash;

	public Advert(int id)
	{
		this.id = id;
	}

	//#region Getters/Setters
	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getUrl()
	{
		return url;
	}

	public long getSize()
	{
		return size;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public String getError()
	{
		return error;
	}

	//#end Getters/Setters

	public void setInfos(String name, String url)
	{
		this.name = name;
		this.url = url;
		this.error = null;
	}

	public void setHash(String hash)
	{
		this.hash = hash;
		setFile();
	}

	public void setData(long size, int width, int height)
	{
		this.size = size;
		this.width = width;
		this.height = height;
	}

	protected String calculateHash(byte[] img)
	{
		return Hashing.md5().hashBytes(img).toString();
	}

	public File getFile()
	{
		return file;
	}

	protected void setFile()
	{
		if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(hash))
		{
			file = new File(advertDir, hash);
			loadFile();
		}
	}

	protected boolean writeFile(byte[] img)
	{
		setHash(calculateHash(img));

		try (FileOutputStream fos = new FileOutputStream(file))
		{
			fos.write(img);
			fos.close();
			return true;
		}
		catch (Exception e)
		{
			setError(e.getMessage());
			MalisisAdvert.log.error(e);
			return false;
		}

	}

	protected void loadFile()
	{
		if (file == null || !file.exists())
			return;

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
		}
	}

	protected abstract void downloadFile();

	public boolean isDownloaded()
	{
		return file.exists();
	}

	public void save()
	{}

	public void delete()
	{
		if (file != null)
			file.delete();
	}

	@Override
	public int compareTo(Advert o)
	{
		return name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Advert && ((Advert) obj).id == id;
	}

	@Override
	public String toString()
	{
		String str = "";
		if (this instanceof ClientAdvert)
			str += "[C]";
		else if (this instanceof ServerAdvert)
			str += "[S]";
		str += " (" + id + ") " + name + " - " + url;

		return str;
	}
}
