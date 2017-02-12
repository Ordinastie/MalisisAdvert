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

import java.util.Collection;

import io.netty.buffer.ByteBuf;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.Advert;
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.advert.advert.ServerAdvert;
import net.malisis.core.network.IMalisisMessageHandler;
import net.malisis.core.network.MalisisMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@MalisisMessage
public class AdvertListMessage implements IMalisisMessageHandler<IMessage, IMessage>
{
	public AdvertListMessage()
	{
		MalisisAdvert.network.registerMessage(this, AdvertListMessage.Query.class, Side.SERVER);
		MalisisAdvert.network.registerMessage(this, AdvertListMessage.Response.class, Side.CLIENT);
	}

	@Override
	public void process(IMessage message, MessageContext ctx)
	{
		if (message instanceof Query && ctx.side == Side.SERVER)
		{
			ServerAdvert.readListing();
			sendList(IMalisisMessageHandler.getPlayer(ctx));
		}

		if (message instanceof Response && ctx.side == Side.CLIENT)
		{
			ClientAdvert.setAdvertList((ClientAdvert[]) ((Response) message).ads);
		}
	}

	public static void queryList()
	{
		MalisisAdvert.network.sendToServer(new Query());
	}

	public static void sendList(EntityPlayer player)
	{
		MalisisAdvert.network.sendTo(new Response(ServerAdvert.listAdverts()), (EntityPlayerMP) player);
	}

	public static void sendAdvert(ServerAdvert advert)
	{
		MalisisAdvert.network.sendToAll(new Response(advert));
	}

	public static class Response implements IMessage
	{
		private Advert[] ads;

		public Response()
		{}

		public Response(Collection<ServerAdvert> listAdverts)
		{
			ads = listAdverts.toArray(new ServerAdvert[0]);
		}

		public Response(ServerAdvert advert)
		{
			ads = new ServerAdvert[] { advert };
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			int count = buf.readInt();
			ads = new ClientAdvert[count];
			for (int i = 0; i < count; i++)
			{
				ads[i] = ClientAdvert.fromBytes(buf);
			}
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(ads.length);
			for (Advert advert : ads)
			{
				((ServerAdvert) advert).toBytes(buf);
			}
		}
	}

	public static class Query implements IMessage
	{
		public Query()
		{}

		@Override
		public void fromBytes(ByteBuf buf)
		{}

		@Override
		public void toBytes(ByteBuf buf)
		{}
	}
}
