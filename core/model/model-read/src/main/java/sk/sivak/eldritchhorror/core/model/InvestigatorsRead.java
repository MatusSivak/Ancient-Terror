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

    List<? extends InvestigatorRead> getSelectedInvestigators();

    List<InvestigatorInfo> getAvailableInvestigators();

    List<InvestigatorId> getPlayedInvestigators();

    boolean isActiveLast();

    /** Ids of selected investigators (including lost in time and space) in turn order. */
    List<InvestigatorId> getInvestigatorIdsInTurnOrder();

    /** Raw turn order; may also contain ids of investigators no longer in game (e.g. defeated or devoured). */
    List<InvestigatorId> getInvestigatorOrder();

    /** Fresh uniform choice among on-board investigators, or null when none are eligible. */
    InvestigatorRead getRandomInvestigator();

    boolean isOnBoard(InvestigatorId investigatorId);

    List<InvestigatorId> getToBeReplacedInvestigators();

    List<? extends InvestigatorRead> getDefeatedInvestigators();

    int getDefeatedOrDevouredCount();
}
