package sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

public class BlessedConditionBackListener4 extends AbstractBlessedConditionBackListener {

    public BlessedConditionBackListener4(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {

        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        List<InvestigatorId> canGainBoonInvestigators = new LinkedList<>();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            if (ServicePlatform.get().getConditionsDeck().canGetTrait(investigatorId, ConditionTrait.BOON)) {
                canGainBoonInvestigators.add(investigatorId);
            }
        }

        TypewriterUtils.confirmInfos("[#GOOD]You or another investigator\ngain Boon Condition[]").subscribe(() -> {
            if (canGainBoonInvestigators.isEmpty()) {
                TypewriterUtils.confirmInfos("No one can gain Boon Condition").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
            investigatorRestriction.addAllowedInvestigators(canGainBoonInvestigators);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(selectedInvestigator -> {
                ServicePlatform.get().getService().hold();
                InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
                if (activeInvestigator.getInfo().getInvestigatorId() != selectedInvestigator) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                }
                ServicePlatform.get().getGameService().gainCondition(ConditionTrait.BOON);
                if (activeInvestigator.getInfo().getInvestigatorId() != selectedInvestigator) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigator.getInfo().getInvestigatorId());
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }
}
