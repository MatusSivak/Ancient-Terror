package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

/**
 * From Bad to Worse: each investigator loses 1 Health per Injury, 1 Sanity per Madness
 * and discards 1 Clue per Deal Condition.
 */
public class YellowMythosCard14 implements MythosCardEventListener {

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            List<ConditionInfo> conditions = ServicePlatform.get().getConditionsDeck().getConditions(investigatorId);
            int injuries = count(conditions, ConditionTrait.INJURY);
            int madnesses = count(conditions, ConditionTrait.MADNESS);
            int clues = Math.min(count(conditions, ConditionTrait.DEAL),
                    ServicePlatform.get().getCluePool().getClueCount(investigatorId));
            if (injuries == 0 && madnesses == 0 && clues == 0) {
                continue;
            }
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            if (injuries > 0) {
                ServicePlatform.get().getTokenService().loseHealth(injuries);
            }
            if (madnesses > 0) {
                ServicePlatform.get().getTokenService().loseSanity(madnesses);
            }
            for (int i = 0; i < clues; i++) {
                ServicePlatform.get().getTokenService().loseClue();
            }
        }
        ServicePlatform.get().getService().release();
    }

    static int count(List<ConditionInfo> conditions, ConditionTrait trait) {
        int count = 0;
        for (ConditionInfo condition : conditions) {
            if (condition.getTraits().contains(trait)) {
                count++;
            }
        }
        return count;
    }
}
