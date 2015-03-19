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

/**
 * @author Ordinastie
 *
 */
public abstract class Advert implements Comparable<Advert>
{
	protected int id;
	protected String name;
	protected long size;
	protected int width;
	protected int height;
	protected String url;
	private String error;

	public Advert(int id, String name, String url)
	{
		this.id = id;
		this.name = name;
		this.url = url;
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

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
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

	public void setData(long size, int width, int height)
	{
		this.size = size;
		this.width = width;
		this.height = height;
	}

	public void save()
	{}

	public void delete()
	{}

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
