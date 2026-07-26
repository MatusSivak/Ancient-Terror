package sk.sivak.eldritchhorror.core.eventtype.data.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class DefeatedInvestigatorEncounterData {
    private InvestigatorId investigatorId;
    private boolean health;
    private Stat crippledTestStat;
    private Stat insaneTestStat;
    private Runnable transferItemsAction;

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public boolean isHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }

    public Stat getCrippledTestStat() {
        return crippledTestStat;
    }

    public void setCrippledTestStat(Stat crippledTestStat) {
        this.crippledTestStat = crippledTestStat;
    }

    public Stat getInsaneTestStat() {
        return insaneTestStat;
    }

    public void setInsaneTestStat(Stat insaneTestStat) {
        this.insaneTestStat = insaneTestStat;
    }

    public Runnable getTransferItemsAction() {
        return transferItemsAction;
    }

    public void setTransferItemsAction(Runnable transferItemsAction) {
        this.transferItemsAction = transferItemsAction;
    }
}
