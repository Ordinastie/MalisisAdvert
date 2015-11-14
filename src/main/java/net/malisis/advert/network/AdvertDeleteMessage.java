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
import net.malisis.advert.advert.ClientAdvert;
import net.malisis.advert.advert.ServerAdvert;
import net.malisis.advert.network.AdvertDeleteMessage.DeletePacket;
import net.malisis.core.network.IMalisisMessageHandler;
import net.malisis.core.network.MalisisMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@MalisisMessage
public class AdvertDeleteMessage implements IMalisisMessageHandler<DeletePacket, IMessage>
{
	public AdvertDeleteMessage()
	{
		MalisisAdvert.network.registerMessage(this, AdvertDeleteMessage.DeletePacket.class, Side.SERVER);
		MalisisAdvert.network.registerMessage(this, AdvertDeleteMessage.DeletePacket.class, Side.CLIENT);
	}

	@Override
	public void process(DeletePacket message, MessageContext ctx)
	{
		Advert advert;
		if (ctx.side == Side.SERVER)
			advert = ServerAdvert.get(message.id);
		else
			advert = ClientAdvert.get(message.id);

		if (advert == null)
			return;
		advert.delete();

		if (ctx.side == Side.SERVER)
			sendDelete(advert);
	}

	public static void queryDelete(Advert advert)
	{
		DeletePacket packet = new DeletePacket(advert);
		MalisisAdvert.network.sendToServer(packet);
	}

	public static void sendDelete(Advert advert)
	{
		DeletePacket packet = new DeletePacket(advert);
		MalisisAdvert.network.sendToAll(packet);
	}

	public static class DeletePacket implements IMessage
	{
		private int id;

		public DeletePacket(Advert advert)
		{
			id = advert.getId();
		}

		public DeletePacket()
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
