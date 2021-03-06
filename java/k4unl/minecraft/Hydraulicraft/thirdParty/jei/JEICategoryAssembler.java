package k4unl.minecraft.Hydraulicraft.thirdParty.jei;

import k4unl.minecraft.Hydraulicraft.lib.Localization;
import k4unl.minecraft.Hydraulicraft.lib.config.Names;
import mezz.jei.api.IGuiHelper;

import javax.annotation.Nonnull;
import java.awt.*;

public class JEICategoryAssembler extends JEICategoryAbstract {

    private static final int       offsetLeft  = 46;
    private static final int       offsetTop   = 2;
    private static final Point     pointOutput = new Point(124, 20);
    private static final Rectangle fluidIn     = new Rectangle(31 - 6, 16 - 14, 47 - 31, 70 - 16);
    private static       Point[]   pointInputs = new Point[9];


    public JEICategoryAssembler(IGuiHelper helper) {

        super(helper);

        for (int i = 0; i < 9; i++)
            pointInputs[i] = new Point(i % 3 * 18 + offsetLeft, i / 3 * 18 + offsetTop);
    }

    @Override
    public String getBackgroundTextureName() {

        return "assembler";
    }

    @Override
    public Rectangle getRectangleForFluidOutput(int i) {

        return null;
    }

    @Override
    public Rectangle getRectangleForFluidInput(int i) {

        return fluidIn;
    }

    @Override
    public Point getPointForInput(int i) {

        return pointInputs[i];
    }

    @Override
    public Point getPointForOutput(int i) {

        return pointOutput;
    }

    @Nonnull
    @Override
    public String getUid() {

        return JEIPlugin.assemblerRecipe;
    }

    @Nonnull
    @Override
    public String getTitle() {

        return Localization.getLocalizedName(Names.blockHydraulicAssembler.unlocalized);
    }

}
