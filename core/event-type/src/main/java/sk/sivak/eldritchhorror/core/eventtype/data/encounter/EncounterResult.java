package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

public class EncounterResult {
    private boolean canHaveAnotherEncounter;
    private boolean isLast;

    public boolean canHaveAnotherEncounter() {
        return canHaveAnotherEncounter;
    }

    public void setCanHaveAnotherEncounter(boolean canHaveAnotherEncounter) {
        this.canHaveAnotherEncounter = canHaveAnotherEncounter;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
