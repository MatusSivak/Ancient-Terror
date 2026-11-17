package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard3 extends YellowMythosCard {

    public YellowMythosCard3() {
        setTitle("The Bell beneath the Pulse");
        setFlavor("A sound too low to hear begins beating against every investigator's heart. " +
                "It weakens the body and shakes thought loose from memory. " +
                "A blessing can be spent to silence it for its bearer; otherwise, both flesh and mind must bear the toll.");
        addInfo("Each investigator:\n" +
                "-[#BAD]Loses two Health[]\n" +
                "-[#BAD]Loses two Sanity[]\n" +
                "[#BAD]unless he discards a Blessed Condition[]");
    }
}
