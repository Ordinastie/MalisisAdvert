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
import net.malisis.advert.gui.advertselection.AdvertSelectionGui;
import net.malisis.advert.gui.manager.AdvertManagerGui;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.network.IMalisisMessageHandler;
import net.malisis.core.network.MalisisMessage;
import net.malisis.core.util.TileEntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@MalisisMessage
public class AdvertGuiMessage implements IMalisisMessageHandler<AdvertGuiMessage.Packet, IMessage>
{
	public static int ADVERTMANAGER = 1;
	public static int ADVERTSELECTION = 2;

	public AdvertGuiMessage()
	{
		MalisisAdvert.network.registerMessage(this, AdvertGuiMessage.Packet.class, Side.CLIENT);
	}

	@Override
	public void process(Packet message, MessageContext ctx)
	{
		if (message.type == ADVERTMANAGER)
			new AdvertManagerGui().display();
		if (message.type == ADVERTSELECTION)
		{
			AdvertTileEntity tileEntity = TileEntityUtils.getTileEntity(AdvertTileEntity.class, Minecraft.getMinecraft().theWorld,
					message.pos);
			if (tileEntity != null)
				new AdvertSelectionGui(tileEntity).display();
		}
	}

	public static void openManager(EntityPlayerMP player)
	{
		MalisisAdvert.network.sendTo(new Packet(ADVERTMANAGER, null), player);
	}

	public static void openSelection(EntityPlayerMP player, AdvertTileEntity tileEntity)
	{
		MalisisAdvert.network.sendTo(new Packet(ADVERTSELECTION, tileEntity), player);
	}

	public static class Packet implements IMessage
	{
		private int type;
		private BlockPos pos;

		public Packet()
		{}

		public Packet(int type, AdvertTileEntity tileEntity)
		{
			this.type = type;
			if (type == ADVERTMANAGER)
				return;

			this.pos = tileEntity.getPos();
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			type = buf.readInt();
			if (type == ADVERTMANAGER)
				return;

			pos = BlockPos.fromLong(buf.readLong());
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(type);
			if (type == ADVERTMANAGER)
				return;

			buf.writeLong(pos.toLong());
		}
	}

}
