package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard2 extends GreenMythosCard {

    public GreenMythosCard2() {
        setTitle("Dust in the Blood");
        setFlavor("A fine metallic dust settles inside sealed rooms and beneath tightly wrapped clothing. " +
                "Once inhaled, it offers a cruel choice: endure the violent purge that tears it from the body, or let the poison remain. " +
                "Those already carrying the poison have no refuge from the new exposure.");
        addInfo("Each investigator:\n" +
                "-[#BAD]Loses three Health\n" +
                "unless he gains Poisoned Condition[]");
    }
}
