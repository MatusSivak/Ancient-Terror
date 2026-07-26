package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea6Encounter extends AbstractShubNiggurathResearchEncounter {

    public ResearchSea6Encounter() {
        super(6, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(2);
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            ServicePlatform.get().getEncounterService().typeFlavor(" \n"+ getTextBuilder().withFlavor(2).build());
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(2).build(),
                    getTextBuilder().withInfo(3).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
                List<? extends CardInfo> conditions = findDarkPactOrCursedConditions();
                if (!conditions.isEmpty()) {
                    selectConditionToDiscard(conditions);
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private List<? extends CardInfo> findDarkPactOrCursedConditions() {
        LinkedList<ConditionInfo> conditions = new LinkedList<>();
        if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.CURSED)) {
            conditions.add(ServicePlatform.get().getConditionsDeck().getCondition(getActiveInvestigatorId(), ConditionId.CURSED));
        }
        if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            conditions.add(ServicePlatform.get().getConditionsDeck().getCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT));
        }
        return conditions;
    }

    private void selectConditionToDiscard(List<? extends CardInfo> conditions) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(conditions);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Condition");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }
}
