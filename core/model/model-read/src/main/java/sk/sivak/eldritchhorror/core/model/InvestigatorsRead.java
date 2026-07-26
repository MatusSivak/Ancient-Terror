package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;

import java.util.List;

/**
 * @author msivak
 */
public interface InvestigatorsRead {

    int getPlayers();

    InvestigatorRead getActiveInvestigator();

    InvestigatorId getActiveInvestigatorId();

    InvestigatorRead getInvestigator(InvestigatorId investigatorId);

    List<? extends InvestigatorRead> getOnBoardInvestigators();

    List<InvestigatorInfo> getAvailableInvestigators();

    boolean isActiveLast();

    InvestigatorRead getLeadInvestigator();

    boolean isOnBoard(InvestigatorId investigatorId);

    List<InvestigatorId> getToBeReplacedInvestigators();

    List<? extends InvestigatorRead> getDefeatedInvestigators();

    int getDefeatedOrDevouredCount();
}
