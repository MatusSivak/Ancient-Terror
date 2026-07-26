package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea16Encounter extends AbstractShubNiggurathResearchEncounter {

    public ResearchSea16Encounter() {
        super(16, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.GHOUL, () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            selectOneInvestigatorToDiscardCursed();
            ServicePlatform.get().getService().release();
        }, null).withTwoPassInfos().execute();
    }

    private void selectOneInvestigatorToDiscardCursed() {
        List<InvestigatorId> cursedInvestigators = new LinkedList<>();
        for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            if (ServicePlatform.get().getConditionsDeck().hasCondition(investigator.getInfo().getInvestigatorId(), ConditionId.CURSED)) {
                cursedInvestigators.add(investigator.getInfo().getInvestigatorId());
            }
        }

        if (cursedInvestigators.isEmpty()) {
            return;
        }

        InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
        investigatorRestriction.addAllowedInvestigators(cursedInvestigators);
        ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(selectedInvestigator -> {

            InvestigatorId activeInvestigator = getActiveInvestigatorId();
            ServicePlatform.get().getService().hold();
            if (selectedInvestigator != activeInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            }
            EncounterUtils.onSelectCardToDiscard(ServicePlatform.get().getConditionsDeck().getCondition(selectedInvestigator, ConditionId.CURSED));
            if (selectedInvestigator != activeInvestigator) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigator);
            }
            ServicePlatform.get().getService().release();

        });

    }
}
