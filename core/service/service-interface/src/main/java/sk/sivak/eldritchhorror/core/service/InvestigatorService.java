package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;

/**
 * @author msivak
 */
public interface InvestigatorService extends CommonService {

    void changeActiveInvestigator();

    void unsetActiveInvestigator();

    void changeActiveInvestigator(InvestigatorId investigatorId);

    void showActiveInvestigator(boolean displayTransition);

    void changeAndShowActiveInvestigator();

    Single<InvestigatorId> selectInvestigator(InvestigatorRestriction investigatorRestriction);

    void improveSkill(Stat skill);

    void loseImprovement(LoseImprovementData loseImprovementData);

    void becomeDelayed(DelayedData delayedData);

    void endDelayed(InvestigatorId investigatorId);

    void lostInTimeAndSpace(InvestigatorId investigatorId);

    void showLostInTimeAndSpace(InvestigatorId investigatorId);

    void hideLostInTimeAndSpace(InvestigatorId investigatorId);

    void removeInvestigatorFromPlace(InvestigatorId investigatorId);

    void spawnInvestigator(InvestigatorId investigatorId, LocationId locationId);


    void putGateToTop(GateInfo selectedGate);

    void putGateToBottom(GateInfo selectedGate);

    Single<Boolean> spendCluesAsGroup(int cluesCount);

    void devourInvestigator(InvestigatorId investigatorId);

    void defeatInvestigator(InvestigatorId investigatorId, boolean health);

    void discardDefeatedInvestigator(InvestigatorId investigatorId);

    void removeDefeatedInvestigator(InvestigatorId investigatorId);

    void selectReplacingInvestigator(InvestigatorId removedInvestigator);

    void selectNewLeadInvestigator(boolean reorder);

    void replaceInvestigators();
}
