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

import java.util.List;

import net.malisis.advert.MalisisAdvert;
import net.malisis.advert.network.AdvertGuiMessage;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.block.BoundingBoxType;
import net.malisis.core.block.MalisisBlock;
import net.malisis.core.block.component.DirectionalComponent;
import net.malisis.core.renderer.DefaultRenderer;
import net.malisis.core.renderer.MalisisRendered;
import net.malisis.core.util.AABBUtils;
import net.malisis.core.util.EntityUtils;
import net.malisis.core.util.TileEntityUtils;
import net.malisis.core.util.chunkcollision.IChunkCollidable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

/**
 * @author Ordinastie
 *
 */
@MalisisRendered(block = AdvertRenderer.class, item = DefaultRenderer.Block.class)
public class AdvertBlock extends MalisisBlock implements ITileEntityProvider, IChunkCollidable
{
	public static final PropertyBool WALL = PropertyBool.create("wall");

	public AdvertBlock()
	{
		super(Material.IRON);
		setResistance(6000);
		setHardness(6000);
		setName("advertBlock");
		setCreativeTab(MalisisAdvert.tab);
		setTexture(MalisisAdvert.modid + ":blocks/MA");

		addComponent(new DirectionalComponent(DirectionalComponent.ALL,
				(state, side, placer) -> side == EnumFacing.UP ? EntityUtils.getEntityFacing(placer).getOpposite() : side));
	}

	@Override
	protected List<IProperty<?>> getProperties()
	{
		return Lists.newArrayList(WALL);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack itemStack)
	{
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, itemStack).withProperty(WALL,
				facing != EnumFacing.UP);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, pos);
		if (te == null)
			return;

		te.setWallMounted(state.getValue(WALL));
		te.setModelContainer(null);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
			return true;

		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, pos);
		if (te == null || !player.canUseCommand(0, "malisisadvert"))
			return true;

		AdvertGuiMessage.openSelection((EntityPlayerMP) player, te);

		return true;
	}

	@Override
	public AxisAlignedBB[] getBoundingBoxes(IBlockAccess world, BlockPos pos, IBlockState state, BoundingBoxType type)
	{
		if (type == BoundingBoxType.PLACEDBOUNDINGBOX)
			return AABBUtils.identities();

		AdvertTileEntity te = TileEntityUtils.getTileEntity(AdvertTileEntity.class, world, pos);
		if (te == null)
			return AABBUtils.identities();

		return te.getModelContainer().getBoundingBox();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new AdvertTileEntity();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return super.getStateFromMeta(meta).withProperty(WALL, (meta & 8) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return super.getMetaFromState(state) + (state.getValue(WALL) ? 8 : 0);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public int blockRange()
	{
		return 3;
	}
}
