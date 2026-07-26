package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard2 extends GreenMythosCard {

    public GreenMythosCard2() {
        setTitle("Tainted Rations");
        setFlavor("You have been surrounded by corruption for so long, " +
                "that it has permeated everything you touch. " +
                "Somehow, all your food supplies have grown rancid overnight.");
        addInfo("Each investigator:\n" +
                "-[#BAD]Loses three Health\n" +
                "unless he gains Poisoned Condition[]");
    }
}
