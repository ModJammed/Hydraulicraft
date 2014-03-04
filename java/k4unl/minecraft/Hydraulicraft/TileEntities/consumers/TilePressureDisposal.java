package k4unl.minecraft.Hydraulicraft.TileEntities.consumers;

import k4unl.minecraft.Hydraulicraft.api.HydraulicBaseClassSupplier;
import k4unl.minecraft.Hydraulicraft.api.IBaseClass;
import k4unl.minecraft.Hydraulicraft.api.IHydraulicConsumer;
import k4unl.minecraft.Hydraulicraft.api.IPressureNetwork;
import k4unl.minecraft.Hydraulicraft.api.PressureNetwork;
import k4unl.minecraft.Hydraulicraft.lib.Functions;
import k4unl.minecraft.Hydraulicraft.lib.Log;
import k4unl.minecraft.Hydraulicraft.lib.config.Constants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraftforge.common.ForgeDirection;

public class TilePressureDisposal extends TileEntity implements
		IHydraulicConsumer {

	private IBaseClass baseHandler;
	private IPressureNetwork pNetwork;

	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet){
		getHandler().onDataPacket(net, packet);
	}
	
	@Override
	public Packet getDescriptionPacket(){
		return getHandler().getDescriptionPacket();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound){
		super.readFromNBT(tagCompound);
		getHandler().readFromNBT(tagCompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound){
		super.writeToNBT(tagCompound);
		getHandler().writeToNBT(tagCompound);
	}
	
	@Override
	public int getMaxStorage() {
		return 10;
	}

	@Override
	public float getMaxPressure(boolean isOil) {
		return Constants.MAX_MBAR_OIL_TIER_3;
	}

	
	@Override
	public IBaseClass getHandler() {
		if(baseHandler == null) baseHandler = HydraulicBaseClassSupplier.getConsumerClass(this);
        return baseHandler;
	}

	@Override
	public void readNBT(NBTTagCompound tagCompound) {

	}

	@Override
	public void writeNBT(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub

	}

	@Override
	public float workFunction(boolean simulate) {
		if(getHandler().getRedstonePowered()){
			if(getHandler().getPressure() > (Constants.MAX_MBAR_GEN_OIL_TIER_3*4)){
				return (Constants.MAX_MBAR_GEN_OIL_TIER_3*4) % getHandler().getPressure();
			}else{
				return getHandler().getPressure();
			}
		}
		return 0;
	}

	@Override
	public void onBlockBreaks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEntity() {
		getHandler().updateEntity();
	}

	public void checkRedstonePower() {
		getHandler().checkRedstonePower();
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
		return true;
	}

	@Override
	public IPressureNetwork getNetwork(ForgeDirection side) {
		return pNetwork;
	}

	@Override
	public void setNetwork(ForgeDirection side, IPressureNetwork toSet) {
		pNetwork = toSet;
	}

	@Override
	public void firstTick() {
		IPressureNetwork newNetwork = Functions.getNearestNetwork(worldObj, xCoord, yCoord, zCoord);
		if(newNetwork != null){
			pNetwork = newNetwork;
			pNetwork.addMachine(this);
			Log.info("Found an existing network (" + newNetwork.getRandomNumber() + ") @ " + xCoord + "," + yCoord + "," + zCoord);
		}else{
			pNetwork = new PressureNetwork(0, this);
			Log.info("Created a new network @ " + xCoord + "," + yCoord + "," + zCoord);
		}
	}
}