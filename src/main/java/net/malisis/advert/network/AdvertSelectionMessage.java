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
import net.malisis.advert.advert.AdvertSelection;
import net.malisis.advert.model.AdvertModel.IModelVariant;
import net.malisis.advert.model.ModelVariantContainer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.network.IMalisisMessageHandler;
import net.malisis.core.registry.AutoLoad;
import net.malisis.core.util.TileEntityUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@AutoLoad(true)
public class AdvertSelectionMessage implements IMalisisMessageHandler<AdvertSelectionMessage.Packet<? extends IModelVariant>, IMessage>
{
	public AdvertSelectionMessage()
	{
		MalisisAdvert.network.registerMessage(this, getPacketClass(), Side.SERVER);
	}

	@SuppressWarnings("unchecked")
	private static Class<Packet<? extends IModelVariant>> getPacketClass()
	{
		return (Class<Packet<? extends IModelVariant>>) new Packet<>().getClass();
	}

	@Override
	public void process(Packet<? extends IModelVariant> message, MessageContext ctx)
	{
		World world = IMalisisMessageHandler.getWorld(ctx);
		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, message.pos);
		if (te == null)
			return;

		te.setModelContainer(message.container);
		te.addSelections(message.selections);
	}

	public static <T extends IModelVariant> void saveSelection(AdvertTileEntity tileEntity, ModelVariantContainer<T> container, AdvertSelection[] selections)
	{
		Packet<T> packet = new Packet<>(tileEntity, container, selections);
		MalisisAdvert.network.sendToServer(packet);
	}

	public static class Packet<T extends IModelVariant> implements IMessage
	{
		private BlockPos pos;
		private ModelVariantContainer<T> container;
		private AdvertSelection[] selections = new AdvertSelection[0];

		public Packet()
		{}

		public Packet(AdvertTileEntity tileEntity, ModelVariantContainer<T> container, AdvertSelection[] selections)
		{
			this.pos = tileEntity.getPos();
			this.container = container;
			this.selections = selections;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			pos = BlockPos.fromLong(buf.readLong());
			container = ModelVariantContainer.fromBytes(buf);

			selections = new AdvertSelection[container.getModel().getAvailableSlots()];
			while (buf.isReadable())
				selections[buf.readByte()] = AdvertSelection.fromBytes(buf);
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeLong(pos.toLong());

			container.toBytes(buf);

			for (int i = 0; i < selections.length; i++)
			{
				if (selections[i] != null)
				{
					buf.writeByte(i);
					selections[i].toBytes(buf);
				}
			}
		}

	}
}
