package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard1 extends GreenMythosCard {

    public GreenMythosCard1() {
        setTitle("Strange Energies");
        setFlavor("The disturbance occurred in such remote areas that they went largely unnoticed at first, " +
                "but soon news spread to research outposts. Those scientists reached out to you about strange " +
                "lights in the night sky and flocks of birds dying in flight and plummeting to the ground.");
        addInfo("Each investigator on the\n" +
                "active Expedition or an adjacent space:\n" +
                "-[#BAD]Loses one Health[]\n" +
                "-[#BAD]Loses one Sanity[]\n" +
                "-Expedition locations are shuffled");
    }
}
