package de.longor.talecraft.voxelator.shapes;

import de.longor.talecraft.util.BlockRegion;
import de.longor.talecraft.util.MutableBlockPos;
import de.longor.talecraft.voxelator.BrushParameter;
import de.longor.talecraft.voxelator.CachedWorldDiff;
import de.longor.talecraft.voxelator.VXShape;
import de.longor.talecraft.voxelator.Voxelator.ShapeFactory;
import de.longor.talecraft.voxelator.params.BooleanBrushParameter;
import de.longor.talecraft.voxelator.params.IntegerBrushParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class VXShapeBox extends VXShape {
	private static final BrushParameter[] PARAMS = new BrushParameter[]{
		new IntegerBrushParameter("width" , 0, 64, 5),
		new IntegerBrushParameter("height", 0, 64, 5),
		new IntegerBrushParameter("length", 0, 64, 5),
		new BooleanBrushParameter("hollow", false)
	};
	
	public static ShapeFactory FACTORY = new ShapeFactory() {
		@Override
		public String getName() {
			return "box";
		}
		@Override
		public VXShape newShape(NBTTagCompound shapeData, BlockPos origin) {
//			int px = shapeData.getInteger("position.x") + origin.getX();
//			int py = shapeData.getInteger("position.y") + origin.getY();
//			int pz = shapeData.getInteger("position.z") + origin.getZ();
			int px = origin.getX();
			int py = origin.getY();
			int pz = origin.getZ();
			int w = shapeData.getInteger("width");
			int h = shapeData.getInteger("height");
			int l = shapeData.getInteger("length");
			boolean hollow = shapeData.getBoolean("hollow");
			System.out.println(w + ", " + h + ", " + l);
			return new VXShapeBox(new BlockPos(px, py, pz), w, h, l, hollow);
		}
		
		@Override
		public NBTTagCompound newShape(String[] parameters) {
			if(parameters.length == 1) {
				NBTTagCompound shapeData = new NBTTagCompound();
				shapeData.setString("type", getName());
				shapeData.setInteger("width", Integer.parseInt(parameters[0]));
				shapeData.setInteger("height", Integer.parseInt(parameters[1]));
				shapeData.setInteger("length", Integer.parseInt(parameters[2]));
				shapeData.setBoolean("hollow", Boolean.parseBoolean(parameters[3]));
				return shapeData;
			}
			
			return null;
		}
		@Override
		public BrushParameter[] getParameters() {
			return PARAMS;
		}
	};
	
	private final BlockPos position;
	private final int width;
	private final int height;
	private final int length;
	private final boolean hollow;

	public VXShapeBox(BlockPos position, int width, int height, int length, boolean hollow) {
		this.position = position;
		this.width = width;
		this.height = height;
		this.length = length;
		this.hollow = hollow;
	}

	@Override
	public BlockPos getCenter() {
		return position;
	}

	@Override
	public BlockRegion getRegion() {
		return new BlockRegion(position, width, height, length);
	}

	@Override
	public boolean test(BlockPos pos, BlockPos center, MutableBlockPos offset, CachedWorldDiff fworld) {
		int diffx = MathHelper.abs_int(pos.getX() - position.getX());
		int diffy = MathHelper.abs_int(pos.getY() - position.getY());
		int diffz = MathHelper.abs_int(pos.getZ() - position.getZ());
		return diffx < width && diffy < height && diffz < length && (hollow ? !(new VXShapeBox(position, width-1, height-1, length-1, false).test(pos, center, offset, fworld)) : true);
	}

}
