package sk.sivak.eldritchhorror.core.view.components.tutorial;

public final class YigEndingDialog extends AncientOneEndingDialog {
    public static final String TEXT_KEY = "ancientOne.yig.endGame";

    public YigEndingDialog() {
        super("YIG", TEXT_KEY, "ancientOne.yig",
                "ancient_one/yig-ending-art-v2.png", "sounds/doom_advance.wav");
    }
}
