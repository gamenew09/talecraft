package de.longor.talecraft.items;

import de.longor.talecraft.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class FillerItem extends TCItem {

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote)
			return EnumActionResult.PASS;
		
		if(player.isSneaking()) {
			IBlockState mask = world.getBlockState(pos);
			int maskID = Block.getStateId(mask);
			
			int oldMaskID = getNBTfromItemStack(stack).getInteger("mask_id");
			
			if(maskID != oldMaskID) {
				// Override Mask
				getNBTfromItemStack(stack).setInteger("mask_id", maskID);
				if(player instanceof EntityPlayerMP) {
					String msg = TextFormatting.AQUA + "Replacement mask set: " + mask;
					((EntityPlayerMP)player).addChatMessage(new TextComponentString(msg));
				}
			} else {
				// Remove Mask
				getNBTfromItemStack(stack).removeTag("mask_id");
				if(player instanceof EntityPlayerMP) {
					String msg = TextFormatting.AQUA + "Replacement mask unset.";
					((EntityPlayerMP)player).addChatMessage(new TextComponentString(msg));
				}
			}
			return EnumActionResult.SUCCESS;
		}
		
		IBlockState fill = world.getBlockState(pos);
		IBlockState mask = null;
		
		if(stack.hasTagCompound()) {
			int maskID = getNBTfromItemStack(stack).getInteger("mask_id");
			if(maskID != 0 && maskID != -1) {
				mask = Block.getStateById(maskID);
			}
		}

		// note: the bounds are already sorted
		int[] bounds = WandItem.getBoundsFromPlayerOrNull(player);

		if(bounds == null) {
			return EnumActionResult.PASS;
		}

		final int maxvolume = 64*64*64;
		final int volume = (bounds[3]-bounds[0]+1) * (bounds[4]-bounds[1]+1) * (bounds[5]-bounds[2]+1);

		if(volume >= maxvolume) {
			// HELL NO!
			if(player instanceof EntityPlayerMP) {
				String msg = TextFormatting.RED + "ERROR: TOO MANY BLOCKS TO FILL -> " + volume + " >= " + maxvolume;
				((EntityPlayerMP)player).addChatMessage(new TextComponentString(msg));
			}
			return EnumActionResult.FAIL;
		}
		
		if(mask != null) {
			WorldHelper.replace(world, bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5], fill, mask);
			return EnumActionResult.SUCCESS;
		}
		
		WorldHelper.fill(world, bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5], fill);
		
		return EnumActionResult.SUCCESS;
	}

}
