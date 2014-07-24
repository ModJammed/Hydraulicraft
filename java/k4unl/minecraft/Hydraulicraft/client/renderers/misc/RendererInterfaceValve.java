package k4unl.minecraft.Hydraulicraft.client.renderers.misc;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import k4unl.minecraft.Hydraulicraft.client.renderers.IconSupplier;
import k4unl.minecraft.Hydraulicraft.client.renderers.RenderHelper;
import k4unl.minecraft.Hydraulicraft.lib.Log;
import k4unl.minecraft.Hydraulicraft.lib.helperClasses.Location;
import k4unl.minecraft.Hydraulicraft.lib.helperClasses.Vector3fMax;
import k4unl.minecraft.Hydraulicraft.tileEntities.misc.TileInterfaceValve;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTankInfo;
import org.lwjgl.opengl.GL11;

public class RendererInterfaceValve extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler  {

    public static final int   RENDER_ID         = RenderingRegistry.getNextAvailableRenderId();
    public static final Block FAKE_RENDER_BLOCK = new Block(Material.rock) {

        @Override
        public IIcon getIcon(int meta, int side) {

            return currentBlockToRender.getIcon(meta, side);
        }
    };

    public static Block currentBlockToRender = Blocks.stone;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

        currentBlockToRender = block;
        renderer.renderBlockAsItem(FAKE_RENDER_BLOCK, 1, 1.0F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
	    Tessellator tes = Tessellator.instance;



        boolean ret = renderer.renderStandardBlock(block, x, y, z);

	    int innerXDifference = 0;
	    int innerYDifference = 0;
	    int innerZDifference = 0;

	    int outerXDifference = 0;
	    int outerYDifference = 0;
	    int outerZDifference = 0;
	    TileInterfaceValve valve = (TileInterfaceValve)world.getTileEntity(x, y, z);
	    boolean isValidTank = valve.isValidTank();
	    Log.info("Render: isTank = " + isValidTank);
	    if(valve.isValidTank()) {
		    Location corner1 = valve.getTankCorner1();
		    Location corner1_out = new Location(valve.getTankCorner1());
		    corner1_out.addX(-1);
		    corner1_out.addY(-1);
		    corner1_out.addZ(-1);

		    Location corner2 = valve.getTankCorner2();
		    Location corner2_out = new Location(valve.getTankCorner2());
		    corner2_out.addX(2);
		    corner2_out.addY(2);
		    corner2_out.addZ(2);

		    innerXDifference = corner2.getX() - corner1.getX();
		    innerYDifference = corner2.getY() - corner1.getY();
		    innerZDifference = corner2.getZ() - corner1.getZ();

		    outerXDifference = corner2_out.getX() - corner1_out.getX();
		    outerYDifference = corner2_out.getY() - corner1_out.getY();
		    outerZDifference = corner2_out.getZ() - corner1_out.getZ();

		    Log.info(corner2_out.printCoords());


		    Tessellator.instance.addTranslation(corner1_out.getX(), corner1_out.getY(), corner1_out.getZ());
		    GL11.glEnable(GL11.GL_BLEND);

		    IIcon icon = IconSupplier.tankGrid;
		    float u = icon.getMinU();
		    float v = icon.getMinV();
		    float U = icon.getMaxU();
		    float V = icon.getMaxV();
		    Tessellator tessellator = Tessellator.instance;

		    for(int xR = 0; xR < outerXDifference; xR++){
			    for(int yR = 0; yR < outerYDifference; yR++) {
				    //Draw north side
				    tessellator.setNormal(0, 0, -1);
				    tessellator.addVertexWithUV(-0.02F + xR, -0.02F + yR, -0.02F, u, v);
				    tessellator.addVertexWithUV(-0.02F + xR, 0.02F + yR+1, -0.02F, u, V);
				    tessellator.addVertexWithUV(0.02F + xR + 1, 0.02F + yR+1, -0.02F, U, V);
				    tessellator.addVertexWithUV(0.02F + xR + 1, -0.02F + yR, -0.02F, U, v);

				    //Draw south side
				    tessellator.setNormal(0, 0, 1);
				    tessellator.addVertexWithUV(-0.02F + xR, -0.02F + yR, outerZDifference+0.02F, u, v);
				    tessellator.addVertexWithUV(0.02F + xR + 1, -0.02F + yR, outerZDifference+0.02F, u, V);
				    tessellator.addVertexWithUV(0.02F + xR + 1, 0.02F + yR + 1, outerZDifference+0.02F, U, V);
				    tessellator.addVertexWithUV(-0.02F + xR, 0.02F + yR + 1, outerZDifference+0.02F, U, v);
			    }
		    }
		    for(int zR = 0; zR < outerZDifference; zR++){
			    for(int yR = 0; yR < outerYDifference; yR++) {
					//Draw west side:
				    tessellator.setNormal(-1, 0, 0);
				    tessellator.addVertexWithUV(-0.02F, -0.02F + yR, 0.02F + zR + 1, u, v);
				    tessellator.addVertexWithUV(-0.02F, 0.02F + yR + 1, 0.02F + zR + 1, u, V);
				    tessellator.addVertexWithUV(-0.02F, 0.02F + yR + 1, -0.02F + zR, U, V);
				    tessellator.addVertexWithUV(-0.02F, -0.02F + yR, -0.02F + zR, U, v);

				    //Draw east side:
				    tessellator.setNormal(1, 0, 0);
				    tessellator.addVertexWithUV(outerXDifference + 0.02F, -0.02F + yR, -0.02F + zR, u, v);
				    tessellator.addVertexWithUV(outerXDifference + 0.02F, 0.02F + yR + 1, -0.02F + zR, u, V);
				    tessellator.addVertexWithUV(outerXDifference + 0.02F, 0.02F + yR + 1, 0.02F + zR + 1, U, V);
				    tessellator.addVertexWithUV(outerXDifference + 0.02F, -0.02F + yR, 0.02F + zR + 1, U, v);
			    }
		    }

		    for(int zR = 0; zR < outerZDifference; zR++){
			    for(int xR = 0; xR < outerXDifference; xR++) {
					//Top side
				    tessellator.setNormal(0, 1, 0);
				    tessellator.addVertexWithUV(-0.02F + xR, outerYDifference + 0.02F, 0.02F + zR + 1, u, v);
				    tessellator.addVertexWithUV(0.02F + xR + 1, outerYDifference + 0.02F, 0.02F + zR + 1, u, V);
				    tessellator.addVertexWithUV(0.02F + xR + 1, outerYDifference + 0.02F, -0.02F + zR, U, V);
				    tessellator.addVertexWithUV(-0.02F + xR, outerYDifference + 0.02F, -0.02F + zR, U, v);

				    //Bottom side
				    tessellator.setNormal(0, -1, 0);
				    tessellator.addVertexWithUV(0.02F + xR + 1, -0.02F, 0.02F + zR + 1, u, v);
				    tessellator.addVertexWithUV(-0.02F + xR, -0.02F, 0.02F + zR + 1, u, V);
				    tessellator.addVertexWithUV(-0.02F + xR, -0.02F, -0.02F + zR, U, V);
				    tessellator.addVertexWithUV(0.02F + xR + 1, -0.02F, -0.02F + zR, U, v);
			    }
		    }

		    //RenderHelper.drawTesselatedCubeWithTexture(new Vector3fMax(-0.02F, -0.02F, -0.02F, outerXDifference + 0.02F, outerYDifference + 0.02F, outerZDifference + 0.02F), IconSupplier.tankGrid);
		    GL11.glDisable(GL11.GL_BLEND);
		    Tessellator.instance.addTranslation(-corner1_out.getX(), -corner1_out.getY(), -corner1_out.getZ());
	    }
        return ret;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {

        return true;
    }

    @Override
    public int getRenderId() {

        return RENDER_ID;
    }

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
	                               double z, float f) {
		TileInterfaceValve t = (TileInterfaceValve) tileentity;
		//Get metadata for rotation:
		int rotation = 0;//t.getDir();
		int metadata = t.getBlockMetadata();

		doRender(t, (float) x, (float) y, (float) z, f, rotation, metadata);
	}

	public void doRender(TileInterfaceValve t, float x, float y,
	                     float z, float f, int rotation, int metadata){

		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);

//		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(resLoc);



		if(t.isValidTank()) {
			FluidTankInfo tankInfo = t.getTankInfo(ForgeDirection.UNKNOWN)[0];
			Location corner1 = t.getTankCorner1();
			Location corner2 = t.getTankCorner2();

			int innerXDifference = 0;
			int innerYDifference = 0;
			int innerZDifference = 0;
			int locationDifferenceX = t.xCoord - corner1.getX();
			int locationDifferenceY = t.yCoord - corner1.getY();
			int locationDifferenceZ = t.zCoord - corner1.getZ();


			innerXDifference = corner2.getX() - corner1.getX() + 1;
			innerYDifference = corner2.getY() - corner1.getY() + 1;
			innerZDifference = corner2.getZ() - corner1.getZ() + 1;

			GL11.glTranslatef(-locationDifferenceX, -locationDifferenceY, -locationDifferenceZ);

			//GL11.glColor3f(1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//GL11.glDisable(GL11.GL_TEXTURE_2D); //Do not use textures
			GL11.glDisable(GL11.GL_LIGHTING); //Disregard lighting
			//GL11.glBegin(GL11.GL_QUADS);


			Vector3fMax insides = new Vector3fMax(0.001F, -0.001F, 0.001F, innerXDifference - 0.001F, -0.001F, innerZDifference - 0.001F);
			float percentage;
			if(tankInfo != null){
				if(tankInfo.fluid != null){
					percentage = (float)tankInfo.fluid.amount / (float)tankInfo.capacity;
					insides.setYMax(percentage * innerYDifference);
					IIcon fluidIcon = tankInfo.fluid.getFluid().getIcon();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					RenderHelper.drawTesselatedCubeWithTexture(insides, fluidIcon);
				}
			}
			GL11.glDisable(GL11.GL_BLEND);

			//RenderHelper.drawColoredCube(insides);
			//GL11.glEnd();


		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}

