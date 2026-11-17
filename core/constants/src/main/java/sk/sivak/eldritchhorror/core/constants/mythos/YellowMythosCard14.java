package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard14 extends YellowMythosCard {

    public YellowMythosCard14() {
        setTitle("The Night Audit");
        setFlavor("A dry scratching follows the investigators from room to room, as if someone were balancing accounts. " +
                "Existing injuries exact another toll on the body, and each disorder of the mind deepens its strain. " +
                "Every outstanding bargain also claims a piece of evidence; the collector charges only for burdens already carried.");
        addInfo("Each investigator:\n" +
                "-[#BAD]Loses 1 Health for each Injury Condition[]\n" +
                "-[#BAD]Loses 1 Sanity for each Madness Condition[]\n" +
                "-[#BAD]Discards 1 Clue for each Deal Condition[]");
    }
}
