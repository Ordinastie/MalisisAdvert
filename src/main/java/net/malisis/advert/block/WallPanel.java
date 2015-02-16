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

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.block.BoundingBoxType;
import net.malisis.core.block.MalisisBlock;
import net.malisis.core.renderer.icon.ClippedIcon;
import net.malisis.core.renderer.icon.MalisisIcon;
import net.malisis.core.util.EntityUtils;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Ordinastie
 *
 */
public class WallPanel extends MalisisBlock implements ITileEntityProvider
{
	public static int renderId = -1;

	public static final int DIR_NORTH = 0;
	public static final int DIR_SOUTH = 1;
	public static final int DIR_WEST = 2;
	public static final int DIR_EAST = 3;
	public static final int HAS_FOOT = 1 << 3;

	public WallPanel()
	{
		super(Material.iron);
		setResistance(6000);
		setHardness(6000);
		setBlockName("wallpanel");
		setCreativeTab(MalisisAdvert.tab);
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		MalisisIcon icon = new MalisisIcon("malisisadvert:wallpanel");
		icon.register((TextureMap) register);
		blockIcon = new ClippedIcon(icon, 0, 0, 2F / 3F, 1);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (side == 1)
			return HAS_FOOT;
		else
			return (ForgeDirection.getOrientation(side).getOpposite().ordinal() - 2);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
	{
		if (!hasFoot(world.getBlockMetadata(x, y, z)))
			return;

		ForgeDirection dir = EntityUtils.getEntityFacing(player);
		world.setBlockMetadataWithNotify(x, y, z, dir.ordinal() - 2 | HAS_FOOT, 2);
	}

	@Override
	public AxisAlignedBB[] getBoundingBox(IBlockAccess world, int x, int y, int z, BoundingBoxType type)
	{
		float w = .1875F;
		int metadata = world.getBlockMetadata(x, y, z);
		if (hasFoot(metadata))
			return new AxisAlignedBB[] { AxisAlignedBB.getBoundingBox(0.5F - w / 2, 0, 0.5F - w / 2, 0.5F + w / 2, 1, 0.5F + w / 2) };

		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(-.5F, 0, -0.5F, 1.5F, 3, 1.5F);
		switch (metadata & 3)
		{
			case WallPanel.DIR_SOUTH:
				aabb.minZ = 1 - w;
				aabb.maxZ = 1;
				break;
			case WallPanel.DIR_EAST:
				aabb.minX = 1 - w;
				aabb.maxX = 1;
				break;
			case WallPanel.DIR_WEST:
				aabb.minX = 0;
				aabb.maxX = w;
				break;
			case WallPanel.DIR_NORTH:
				aabb.minZ = 0;
				aabb.maxZ = w;
				break;
		}

		return new AxisAlignedBB[] { aabb };
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
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

	public static boolean hasFoot(int metadata)
	{
		return (metadata & HAS_FOOT) != 0;
	}
}
