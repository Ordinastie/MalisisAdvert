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

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @author Ordinastie
 *
 */
public class Advert implements Comparable<Advert>
{
	private static ListeningExecutorService threadPool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

	private String name;
	private String url;
	private String fileName;

	public Advert(String name, String url)
	{
		this.name = name;
		this.url = url;
	}

	//#region Getters/Setters
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName()
	{
		return fileName;
	}

	//#end Getters/Setters

	public boolean isDownloaded()
	{
		return true;
	}

	public void download()
	{

	}

	private static ListenableFuture<String> downloadURL(final URL url)
	{
		return threadPool.submit(new Callable<String>()
		{
			@Override
			public String call() throws Exception
			{
				return Resources.toString(url, Charsets.UTF_8);
			}
		});
	}

	@Override
	public int compareTo(Advert o)
	{
		return name.compareTo(o.name);
	}

	public static Advert fromListing(String line)
	{
		String[] parts = line.split(";");
		if (parts.length != 2)
			return null;
		return new Advert(parts[0], parts[1]);
	}
}
