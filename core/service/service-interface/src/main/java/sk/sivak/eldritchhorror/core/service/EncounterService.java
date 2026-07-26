package sk.sivak.eldritchhorror.core.service;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;

/**
 * @author msivak
 */
public interface EncounterService extends CommonService {
    void encounter();

    void justExecuteEncounter();

    void executeEncounter();

    // Typewriter things...
    void showTypewriterPaper(boolean newPaper);

    void finishTypewriterPaper();

    void hideTypewriterPaper();

    void typeHeader(String header);

    void typeFlavor(String flavor);

    void typeInfo(String info);

    Single<Integer> displayButtons(String... buttonTexts);

    void disableAnotherEncounter(InvestigatorId investigatorId);

    void checkDefeatedAfterEncounter();

    void endOfEncounter(Encounter encounter);

    void endOfEncounter();

    void insertDefeatSequencePoint();

    Single<String> readInput(String label);
}
