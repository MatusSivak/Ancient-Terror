package sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

public class BlessedConditionBackListener1 extends AbstractBlessedConditionBackListener {

    public BlessedConditionBackListener1(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        List<InvestigatorId> notBlessedInvestigators = new LinkedList<>();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            if (investigatorId == getActiveInvestigatorId()) {
                continue;
            }
            if (ServicePlatform.get().getConditionsDeck().hasCondition(investigatorId, ConditionId.BLESSED)) {
                continue;
            }
            notBlessedInvestigators.add(investigatorId);
        }


        TypewriterUtils.confirmInfos("[#GOOD]Another investigator of your choice\n" +
                "that does not have a Blessed Condition\n" +
                "gains a Blessed Condition[]").subscribe(() -> {
            if (notBlessedInvestigators.isEmpty()) {
                TypewriterUtils.confirmInfos("Everyone has Blessed Condition.").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
            investigatorRestriction.addAllowedInvestigators(notBlessedInvestigators);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(selectedInvestigator -> {
                ServicePlatform.get().getService().hold();
                InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED);
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigator.getInfo().getInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }
}
