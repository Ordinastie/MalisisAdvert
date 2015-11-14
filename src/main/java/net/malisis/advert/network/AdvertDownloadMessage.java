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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.Advert;
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.advert.advert.ServerAdvert;
import net.malisis.core.network.IMalisisMessageHandler;
import net.malisis.core.network.MalisisMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.io.Files;

/**
 * Message to tell the client to open a GUI.
 *
 * @author Ordinastie
 *
 */
@MalisisMessage
public class AdvertDownloadMessage implements IMalisisMessageHandler<IMessage, IMessage>
{
	public AdvertDownloadMessage()
	{
		MalisisAdvert.network.registerMessage(this, AdvertDownloadMessage.Query.class, Side.SERVER);
		MalisisAdvert.network.registerMessage(this, AdvertDownloadMessage.Response.class, Side.CLIENT);
	}

	@Override
	public void process(IMessage message, MessageContext ctx)
	{
		if (message instanceof Query && ctx.side == Side.SERVER)
			processQuery((Query) message, ctx);

		if (message instanceof Response && ctx.side == Side.CLIENT)
			processResponse((Response) message, ctx);
	}

	private void processQuery(Query message, MessageContext ctx)
	{
		ServerAdvert advert = ServerAdvert.get(message.id);
		if (advert == null)
			return;

		sendImageData(advert, ctx.getServerHandler().playerEntity);
	}

	private void processResponse(Response message, MessageContext ctx)
	{
		ClientAdvert.setPending(false);
		ClientAdvert advert = ClientAdvert.get(message.id);
		if (advert == null)
			return;

		if (message.error != null)
		{
			advert.setError(message.error);
			return;
		}

		BufferedImage img;
		try
		{
			img = ImageIO.read(new ByteArrayInputStream(message.data));
			if (img != null)
				advert.setTexture(img, message.size);
			else
				advert.setError("Could not read image.");

		}
		catch (IOException e)
		{
			MalisisAdvert.log.error("Could not set the texture for {}", advert, e);
			advert.setError("Could not read image.");
		}
	}

	public static void queryDownload(Advert advert)
	{
		Query packet = new Query(advert);
		MalisisAdvert.network.sendToServer(packet);
	}

	public static void sendImageData(ServerAdvert advert, EntityPlayerMP player)
	{
		if (!advert.getFile().exists() && advert.getError() == null)
		{
			advert.downloadAdvert(player);
			return;
		}

		Response packet = new Response(advert);
		MalisisAdvert.network.sendTo(packet, player);
	}

	public static class Response implements IMessage
	{
		private int id;
		private long size;
		private String error;
		private byte[] data = new byte[0];

		public Response(ServerAdvert advert)
		{
			this.id = advert.getId();
			this.size = advert.getSize();
			if (advert.getError() != null)
				this.error = advert.getError();
			else
				try
				{
					data = Files.toByteArray(advert.getFile());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}

		public Response()
		{}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			id = buf.readInt();
			size = buf.readLong();
			if (buf.readBoolean())
				error = ByteBufUtils.readUTF8String(buf);
			else
			{
				data = new byte[buf.readableBytes()];
				buf.readBytes(data);
			}
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(id);
			buf.writeLong(size);
			buf.writeBoolean(error != null);
			if (error != null)
				ByteBufUtils.writeUTF8String(buf, error);
			else
				buf.writeBytes(data);
		}

	}

	public static class Query implements IMessage
	{
		private int id;

		public Query(Advert advert)
		{
			this.id = advert.getId();
		}

		public Query()
		{}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			id = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(id);
		}
	}
}
