package de.longor.talecraft.items;

import de.longor.talecraft.TaleCraft;
import de.longor.talecraft.clipboard.ClipboardItem;
import de.longor.talecraft.server.ServerClipboard;
import de.longor.talecraft.server.ServerHandler;
import de.longor.talecraft.server.ServerMirror;
import de.longor.talecraft.util.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CutItem extends TCItem {

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote) {
			rightClickClient(world, player);
		} else {
			rightClickServer(world, player);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 0);
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(world.isRemote)
			rightClickClient(world, player);
		else
			rightClickServer(world, player);

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	public static void rightClickServer(World world, EntityPlayer player) {
		ServerMirror mirror = ServerHandler.getServerMirror(null);
		ServerClipboard clipboard = mirror.getClipboard();

		int[] bounds = WandItem.getBoundsFromPlayerOrNull(player);
		String keyString = "player."+player.getGameProfile().getId().toString();

		if(bounds == null)
			return;

		ClipboardItem item = ClipboardItem.copyRegion(bounds, world, keyString, player);

		if(item != null) {
			clipboard.put(keyString, item);
			WorldHelper.fill(world, bounds, Blocks.AIR.getDefaultState());
		}
	}

	public static void rightClickClient(World world, EntityPlayer player) {
		int[] bounds = WandItem.getBoundsFromPlayerOrNull(player);

		if(bounds == null)
			return;

		ClipboardItem item = ClipboardItem.copyRegion(bounds, world, "player.self", player);

		if(item != null) {
			TaleCraft.asClient().setClipboard(item);
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		// cut entity to clipboard!

		// ClipboardItem.copyEntity();

		// by returning TRUE, we prevent damaging the entity being hit.
		return true;
	}

}
