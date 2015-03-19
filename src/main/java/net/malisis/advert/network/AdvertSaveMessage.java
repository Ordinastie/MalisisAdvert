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

package net.malisis.advert.network;

import io.netty.buffer.ByteBuf;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.Advert;
import net.malisis.advert.advert.ServerAdvert;
import net.malisis.advert.network.AdvertSaveMessage.SavePacket;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
public class AdvertSaveMessage implements IMessageHandler<SavePacket, IMessage>
{
	public static AdvertSaveMessage instance = new AdvertSaveMessage();

	private AdvertSaveMessage()
	{
		MalisisAdvert.network.registerMessage(this, AdvertSaveMessage.SavePacket.class, Side.SERVER);
	}

	@Override
	public IMessage onMessage(SavePacket message, MessageContext ctx)
	{
		if (message instanceof SavePacket && ctx.side == Side.SERVER)
		{
			ServerAdvert advert = ServerAdvert.get(message.id, true);
			advert.setInfos(message.name, message.url);
			advert.save();

			AdvertListMessage.sendAdvert(advert);
		}

		return null;
	}

	public static void save(Advert advert)
	{
		SavePacket packet = new SavePacket(advert);
		MalisisAdvert.network.sendToServer(packet);
	}

	public static class SavePacket implements IMessage
	{
		private int id;
		private String name;
		private String url;

		public SavePacket(Advert advert)
		{
			id = advert.getId();
			name = advert.getName();
			url = advert.getUrl();
		}

		public SavePacket()
		{}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			id = buf.readInt();
			name = ByteBufUtils.readUTF8String(buf);
			url = ByteBufUtils.readUTF8String(buf);
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(id);
			ByteBufUtils.writeUTF8String(buf, name == null ? "" : name);
			ByteBufUtils.writeUTF8String(buf, url == null ? "" : url);
		}
	}

}
