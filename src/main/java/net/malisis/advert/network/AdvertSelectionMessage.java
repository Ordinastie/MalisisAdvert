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
import net.malisis.advert.AdvertModel;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.network.MalisisMessage;
import net.malisis.core.util.TileEntityUtils;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@MalisisMessage
public class AdvertSelectionMessage implements IMessageHandler<AdvertSelectionMessage.Packet, IMessage>
{
	public AdvertSelectionMessage()
	{
		MalisisAdvert.network.registerMessage(this, AdvertSelectionMessage.Packet.class, Side.SERVER);
	}

	@Override
	public IMessage onMessage(Packet message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().playerEntity.worldObj;
		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, message.x, message.y, message.z);
		if (te == null)
			return null;

		te.setModel(message.model);
		te.addSelection(0, message.advertSelection);

		return null;
	}

	public static void saveSelection(AdvertTileEntity tileEntity, AdvertModel model, AdvertSelection advertSelection)
	{
		Packet packet = new Packet(tileEntity, model, advertSelection);
		MalisisAdvert.network.sendToServer(packet);
	}

	public static class Packet implements IMessage
	{
		private int x, y, z;
		private AdvertModel model;
		private AdvertSelection advertSelection;

		public Packet()
		{}

		public Packet(AdvertTileEntity tileEntity, AdvertModel model, AdvertSelection advertSelection)
		{
			this.x = tileEntity.xCoord;
			this.y = tileEntity.yCoord;
			this.z = tileEntity.zCoord;
			this.model = model;
			this.advertSelection = advertSelection;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();

			model = AdvertModel.values()[buf.readInt()];

			if (!buf.isReadable())
				return;

			advertSelection = AdvertSelection.fromBytes(buf);
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);

			buf.writeInt(model.ordinal());

			if (advertSelection != null)
				advertSelection.toBytes(buf);
		}

	}
}
