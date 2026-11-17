package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard8 extends GreenMythosCard {

    public GreenMythosCard8() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("The Shelter Mark");
        setFlavor("One investigator finds a pale handprint on a wall where no hand could have reached. " +
                "Its lines lift from the plaster and settle across the investigator's shadow. " +
                "The mark is a blessing: something beyond the wall has chosen to offer its protection.");
        addInfo("{investigator}:\n" +
                "-[#GOOD]Gains a Blessed Condition[]");
    }
}
