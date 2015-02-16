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

package net.malisis.advert.packet;

import io.netty.buffer.ByteBuf;
import net.malisis.core.inventory.MalisisInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Message to tell the client to open a GUI.
 *
 * @author Ordinastie
 *
 */
public class DownloadAdvertMessage implements IMessageHandler<DownloadAdvertMessage.Packet, IMessage>
{

	/**
	 * Handles the received {@link Packet} on the client. Opens the GUI.
	 *
	 * @param message the message
	 * @param ctx the ctx
	 * @return the i message
	 */
	@Override
	public IMessage onMessage(Packet message, MessageContext ctx)
	{
		if (ctx.side == Side.SERVER)
			return null;
		return null;
	}

	/**
	 * Sends a packet to client to notify it to open a {@link MalisisInventory}.
	 *
	 * @param container the container
	 * @param player the player
	 * @param windowId the window id
	 */
	public static void send(EntityPlayerMP player, String url)
	{
		Packet packet = new Packet(url);
		NetworkHandler.network.sendTo(packet, player);
	}

	public static class Packet implements IMessage
	{
		private String url;

		public Packet(String url)
		{
			this.url = url;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			url = ByteBufUtils.readUTF8String(buf);
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			ByteBufUtils.writeUTF8String(buf, url);
		}

	}
}
