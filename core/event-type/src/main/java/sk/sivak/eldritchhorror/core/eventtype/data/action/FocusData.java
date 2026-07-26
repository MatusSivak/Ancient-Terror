package sk.sivak.eldritchhorror.core.eventtype.data.action;

/**
 * @author msivak
 */
public class FocusData {
    private int focusGained;

    public int getFocusGained() {
        return focusGained;
    }

    public void setFocusGained(int focusGained) {
        this.focusGained = focusGained;
    }

    @Override
    public String toString() {
        return "FocusData{" +
                "focusGained=" + focusGained +
                '}';
    }
}
