package sk.sivak.eldritchhorror.core.view.components.tutorial;

public final class AzathothEndingDialog extends AncientOneEndingDialog {
    public static final String TEXT_KEY = "ancientOne.azathoth.endGame";

    public AzathothEndingDialog() {
        super("AZATHOTH", TEXT_KEY, "ancientOne.azathoth",
                "ancient_one/azathoth-ending-art.png", "sounds/azathoth_ending.wav");
    }
}
