package pet.minecraft.Hydraulicraft.blocks;

import pet.minecraft.Hydraulicraft.baseClasses.MachineBlock;
import pet.minecraft.Hydraulicraft.lib.config.Ids;
import pet.minecraft.Hydraulicraft.lib.config.Names;
import pet.minecraft.Hydraulicraft.lib.helperClasses.Id;
import pet.minecraft.Hydraulicraft.lib.helperClasses.Name;
import pet.minecraft.Hydraulicraft.proxy.ClientProxy;

public class BlockHydraulicPump extends MachineBlock {

    protected BlockHydraulicPump() {
        super(Ids.blockHydraulicPump, Names.blockHydraulicPump);
        this.hasFrontIcon = true;
    }
}