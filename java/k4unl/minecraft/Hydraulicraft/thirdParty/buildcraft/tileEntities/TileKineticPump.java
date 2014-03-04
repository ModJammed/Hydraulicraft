package k4unl.minecraft.Hydraulicraft.thirdParty.buildcraft.tileEntities;

import k4unl.minecraft.Hydraulicraft.api.HydraulicBaseClassSupplier;
import k4unl.minecraft.Hydraulicraft.api.IBaseClass;
import k4unl.minecraft.Hydraulicraft.api.IBaseGenerator;
import k4unl.minecraft.Hydraulicraft.api.IHydraulicGenerator;
import k4unl.minecraft.Hydraulicraft.api.IPressureNetwork;
import k4unl.minecraft.Hydraulicraft.lib.Log;
import k4unl.minecraft.Hydraulicraft.lib.config.Constants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

public class TileKineticPump extends TileEntity implements IHydraulicGenerator, IPowerReceptor {
	private boolean isRunning = false;
	private PowerHandler powerHandler;
	private int MJPower;
	private IBaseGenerator baseHandler;
	private ForgeDirection facing = ForgeDirection.NORTH;
	
	public TileKineticPump(){
		
	}
	
	private PowerHandler getPowerHandler(){
		if(powerHandler == null){
			powerHandler = new PowerHandler(this, Type.MACHINE);
			powerHandler.configure(Constants.MJ_USAGE_PER_TICK[getTier()]*2, Constants.MJ_USAGE_PER_TICK[getTier()] * 3, Constants.ACTIVATION_MJ, (getTier()+1) * 100);
		}
		return powerHandler;
	}
	
	@Override
	public void updateEntity(){
		getHandler().updateEntity();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound){
		getHandler().readFromNBT(tagCompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound){
		getHandler().writeToNBT(tagCompound);
	}
	
	@Override
	public void workFunction() {
		if(!getHandler().getRedstonePowered()){
			isRunning = false;
			getHandler().updateBlock();
			return;
		}
		//This function gets called every tick.
		boolean needsUpdate = false;
		needsUpdate = true;
		if(Float.compare(getGenerating(), 0.0F) > 0){
			getHandler().setPressure(getHandler().getPressure() + getGenerating());
			getPowerHandler().useEnergy(0, Constants.MJ_USAGE_PER_TICK[getTier()], true);
			//MJPower -= Constants.MJ_USAGE_PER_TICK[getTier()];
			//getEnergyStorage().extractEnergy(getMaxGenerating(), false);
			isRunning = true;
		}else{
			isRunning = false;
		}
		
		if(needsUpdate){
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public int getMaxGenerating() {
		if(!getHandler().isOilStored()){
			switch(getTier()){
			case 0:
				return Constants.MAX_MBAR_GEN_WATER_TIER_1;
			case 1:
				return Constants.MAX_MBAR_GEN_WATER_TIER_2;
			case 2:
				return Constants.MAX_MBAR_GEN_WATER_TIER_3;
			}			
		}else{
			switch(getTier()){
			case 0:
				return Constants.MAX_MBAR_GEN_OIL_TIER_1;
			case 1:
				return Constants.MAX_MBAR_GEN_OIL_TIER_2;
			case 2:
				return Constants.MAX_MBAR_GEN_OIL_TIER_3;
			}
		}
		return 0;
	}

	@Override
	public float getGenerating() {
		if(!getHandler().getRedstonePowered()) return 0f;

		float extractedEnergy = getPowerHandler().useEnergy(0, Constants.MJ_USAGE_PER_TICK[getTier()], false);
		//Log.info("PHL: " + getPowerHandler().getEnergyStored() + " EE: " + extractedEnergy);
		
		if(getPowerHandler().getEnergyStored() > Constants.MJ_USAGE_PER_TICK[getTier()] * 2){
			float gen = extractedEnergy * Constants.CONVERSION_RATIO_MJ_HYDRAULIC * (getHandler().isOilStored() ? 1.0F : Constants.WATER_CONVERSION_RATIO);
			//gen = gen * (gen / getMaxGenerating());
			if(gen > getMaxGenerating()){
				gen = getMaxGenerating();
			}
			
			if(Float.compare(gen + getHandler().getPressure(), getMaxPressure(getHandler().isOilStored())) > 0){
				//This means the pressure we are generating is too much!
				gen = getMaxPressure(getHandler().isOilStored()) - getHandler().getPressure();
			}
			
			return gen; 
		}else{
			return 0F;
		}
	}


    public int getTier(){
        return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    }
	

	@Override
	public int getMaxStorage() {
		return FluidContainerRegistry.BUCKET_VOLUME * (2 * (getTier() + 1));
	}

	@Override
	public void onBlockBreaks() {
		
	}

	@Override
	public float getMaxPressure(boolean isOil) {
		if(isOil){
			switch(getTier()){
			case 0:
				return Constants.MAX_MBAR_OIL_TIER_1;
			case 1:
				return Constants.MAX_MBAR_OIL_TIER_2;
			case 2:
				return Constants.MAX_MBAR_OIL_TIER_3;
			}			
		}else{
			switch(getTier()){
			case 0:
				return Constants.MAX_MBAR_WATER_TIER_1;
			case 1:
				return Constants.MAX_MBAR_WATER_TIER_2;
			case 2:
				return Constants.MAX_MBAR_WATER_TIER_3;
			}	
		}
		return 0;
	}

	@Override
	public IBaseClass getHandler() {
		if(baseHandler == null) baseHandler = HydraulicBaseClassSupplier.getGeneratorClass(this);
        return baseHandler;
	}

	@Override
	public void readNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		facing = ForgeDirection.getOrientation(tagCompound.getInteger("facing"));

		isRunning = tagCompound.getBoolean("isRunning");
		//MJPower = tagCompound.getInteger("MJPower");
		getPowerHandler().readFromNBT(tagCompound, "powerHandler");
	}

	@Override
	public void writeNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		tagCompound.setInteger("facing", facing.ordinal());
		tagCompound.setBoolean("isRunning", isRunning);
		getPowerHandler().writeToNBT(tagCompound, "powerHandler");
		//tagCompound.setInteger("MJPower", MJPower);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		getHandler().onDataPacket(net, packet);
	}

	@Override
	public Packet getDescriptionPacket() {
		return getHandler().getDescriptionPacket();
	}
	
	@Override
	public void validate(){
		super.validate();
		getHandler().validate();
	}

	@Override
	public void onPressureChanged(float old) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFluidLevelChanged(int old) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canConnectTo(ForgeDirection side) {
		return side.equals(facing);
	}

	public ForgeDirection getFacing() {
		return facing;
	}

	public void setFacing(ForgeDirection rotation) {
		facing = rotation;
	}
	
	public boolean getIsRunning(){
		return isRunning;
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		if(side.equals(facing.getOpposite())){
			return getPowerHandler().getPowerReceiver();
		}else{
			return null;
		}
	}

	@Override
	public void doWork(PowerHandler workProvider) { }

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public IPressureNetwork getNetwork(ForgeDirection side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNetwork(ForgeDirection side, IPressureNetwork toSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void firstTick() {
		// TODO Auto-generated method stub
		
	}
	
}