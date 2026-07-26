package sk.sivak.eldritchhorror.core.model;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

import java.util.List;

/**
 * @author msivak
 */
public interface InvestigatorsWrite extends InvestigatorsRead {

    void initWithPlayers(int players);

    void initAvailableInvestigators(boolean bonusInvestigatorsPurchased);

    void initSelectedInvestigators(InvestigatorInfo[] selected);

    void setLeadInvestigator(InvestigatorId investigatorId, boolean reorder);

    @Override
    InvestigatorWrite getActiveInvestigator();

    @Override
    InvestigatorWrite getInvestigator(InvestigatorId investigatorId);

    void initActiveInvestigator();

    void changeActiveInvestigator();

    void changeActiveInvestigator(InvestigatorId targetInvestigatorId);

    void lostInTimeAndSpace(InvestigatorId input);

    void unsetActiveInvestigator();

    void devourInvestigator(InvestigatorId activeInvestigatorId);

    void initReplacingInvestigator(InvestigatorId removedInvestigator, InvestigatorInfo replacing);

    void defeatInvestigator(InvestigatorId devouredInvestigatorId);

    void removeDefeatedInvestigator(InvestigatorId investigatorId);

    List<? extends InvestigatorWrite> getOnBoardInvestigators();

    SaveDataWrite.InvestigatorsSaveDataWrite save();

    void load(InvestigatorsSaveDataRead investigatorsSaveData, boolean hasPurchasedBonusInvestigators);

    void addLockedToAvailable();
}
