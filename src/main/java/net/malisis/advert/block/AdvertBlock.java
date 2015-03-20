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

package net.malisis.advert.block;

import net.malisis.advert.AdvertModel;
import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.gui.advertselection.AdvertSelectionGui;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.block.BoundingBoxType;
import net.malisis.core.block.MalisisBlock;
import net.malisis.core.util.AABBUtils;
import net.malisis.core.util.EntityUtils;
import net.malisis.core.util.TileEntityUtils;
import net.malisis.core.util.chunkcollision.IChunkCollidable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Ordinastie
 *
 */
public class AdvertBlock extends MalisisBlock implements ITileEntityProvider, IChunkCollidable
{
	public static int renderId = -1;

	public static final int DIR_NORTH = 0;
	public static final int DIR_SOUTH = 1;
	public static final int DIR_WEST = 2;
	public static final int DIR_EAST = 3;

	private IIcon panelIcon;

	public AdvertBlock()
	{
		super(Material.iron);
		setResistance(6000);
		setHardness(6000);
		setBlockName("advertBlock");
		setCreativeTab(MalisisAdvert.tab);
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		blockIcon = register.registerIcon("malisisadvert:MA");
		panelIcon = register.registerIcon("malisisadvert:panel");
	}

	public IIcon getPanelIcon()
	{
		return panelIcon;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		return side;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
	{
		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, x, y, z);
		if (te == null)
			return;

		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata == 1)
		{
			te.setModel(AdvertModel.PANEL_SMALL_FOOT);
			ForgeDirection dir = EntityUtils.getEntityFacing(player);
			world.setBlockMetadataWithNotify(x, y, z, dir.ordinal() - 2, 3);
		}
		else
		{
			te.setWallMounted(true);
			te.setModel(AdvertModel.PANEL_WALL);
			world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(metadata).getOpposite().ordinal() - 2, 3);
		}

	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
			return true;

		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, x, y, z);
		if (te == null || !player.canCommandSenderUseCommand(0, "malisisadvert"))
			return true;

		new AdvertSelectionGui(te).display();

		return true;
	}

	@Override
	public AxisAlignedBB[] getBoundingBox(IBlockAccess world, int x, int y, int z, BoundingBoxType type)
	{
		int[] dirs = { 2, 0, 1, 3 };
		float w = .1875F;
		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, x, y, z);
		if (te == null)
			return new AxisAlignedBB[] { AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1) };

		AxisAlignedBB foot = AxisAlignedBB.getBoundingBox(0.375F, 0, 0.5F - w, 0.625F, 1, 0.5F);
		AxisAlignedBB panel = AxisAlignedBB.getBoundingBox(-0.5F, 0, 0.5F - w, 1.5F, 3, 0.5F);

		if (te.getModel() == AdvertModel.PANEL_WALL)
			panel.offset(0, 0, 0.5F);

		if (te.getModel() == AdvertModel.PANEL_FULL_FOOT)
			panel.maxY++;

		AxisAlignedBB[] aabbs;
		if (te.getModel() == AdvertModel.PANEL_SMALL_FOOT)
		{
			panel.offset(0, 1, 0);
			aabbs = new AxisAlignedBB[] { foot, panel };
		}
		else
			aabbs = new AxisAlignedBB[] { panel };

		return AABBUtils.rotate(aabbs, dirs[te.getBlockMetadata()]);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new AdvertTileEntity();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return renderId;
	}
}
