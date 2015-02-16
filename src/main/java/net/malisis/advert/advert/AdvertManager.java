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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Ordinastie
 *
 */
public enum AdvertManager implements Iterable<Advert>
{
	instance;

	private static String ADVERTDIR = "adverts/";
	private static String LISTINGNAME = "listing.txt";

	private File listing;
	private SortedSet<Advert> adverts = new TreeSet<>();

	public void readAdvertFolder()
	{
		File packDir = new File("./" + ADVERTDIR);
		if (!packDir.exists())
			packDir.mkdir();

		listing = new File(packDir, LISTINGNAME);

		readAdvertListing();
	}

	@Override
	public Iterator<Advert> iterator()
	{
		return adverts.iterator();
	}

	private void readAdvertListing()
	{
		try (BufferedReader br = new BufferedReader(new FileReader(listing)))
		{
			for (String line; (line = br.readLine()) != null;)
			{
				Advert ad = Advert.fromListing(line);
				if (ad != null)
					adverts.add(ad);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
