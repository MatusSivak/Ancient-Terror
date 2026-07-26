package sk.sivak.eldritchhorror.core.eventtype;

public class AfterActionPerformedData {
    private boolean canPerformAction;
    private boolean last;

    public boolean canPerformAction() {
        return canPerformAction;
    }

    public void setCanPerformAction(boolean canPerformAction) {
        this.canPerformAction = canPerformAction;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
