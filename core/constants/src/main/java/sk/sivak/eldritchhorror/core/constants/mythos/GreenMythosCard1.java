package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard1 extends GreenMythosCard {

    public GreenMythosCard1() {
        setTitle("The Survey Line Bends");
        setFlavor("The expedition's bearings begin to curve around a point that is missing from every map. " +
                "A pulse passes through the site and its approaches, bruising flesh and filling nearby minds with impossible distances. " +
                "The search must be redirected; the old survey can no longer be trusted.");
        addInfo("Each investigator on the\n" +
                "active Expedition or an adjacent space:\n" +
                "-[#BAD]Loses one Health[]\n" +
                "-[#BAD]Loses one Sanity[]\n" +
                "-Expedition locations are shuffled");
    }
}
