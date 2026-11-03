package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public interface MythosCardEventListener {

    void execute();

    default void execute(InvestigatorId narratedTarget) {
        execute();
    }
}
