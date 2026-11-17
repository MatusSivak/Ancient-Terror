package sk.sivak.eldritchhorror.core.constants.mythos;

public class GreenMythosCard12 extends GreenMythosCard {

    public GreenMythosCard12() {
        setMythosDifficulty(MythosDifficulty.EASY);
        setTitle("Lessons in Vanishing Ink");
        setFlavor("A sheet of instructions appears only when viewed from the corner of the eye. " +
                "Its first exercise steadies the attention; those already focused can turn that concentration into a hidden lead. " +
                "For those prepared to give up a clue, the final exercise offers a glamour that bends what others see.");
        addInfo("Each investigator may select one:\n" +
                "-[#GOOD]Gain 1 Focus[]\n" +
                "-[#GOOD]Spend 1 Focus to gain 1 Clue[]\n" +
                "-[#GOOD]Spend 1 Clue to gain 1 Glamour Spell[]");
    }
}
