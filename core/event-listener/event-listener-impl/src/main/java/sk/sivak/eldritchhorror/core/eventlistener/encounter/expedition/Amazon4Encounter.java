package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Amazon4Encounter extends AbstractExpeditionEncounter {

    public Amazon4Encounter() {
        super(4, LocationId.THE_AMAZON);
    }

    @Override
    protected void execute() {
        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                this::gainTwoClues,
                this::selectItemToDiscard).withExpeditionFlavor();

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1,
                this::selectIllnessOrInjuryToDiscard,
                () -> {
                    loseHealth(1);
                    gainCondition(ConditionId.POISONED);
                }).withTwoFailInfos().withExpeditionFlavor();

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, -1, passTemplate, failTemplate).execute();
    }

    private void selectIllnessOrInjuryToDiscard() {
        List<ConditionInfo> illnessesOrInjuries = new LinkedList<>();
        illnessesOrInjuries.addAll(ServicePlatform.get().getConditionsDeck().getCondition(getActiveInvestigatorId(), ConditionTrait.ILLNESS));
        illnessesOrInjuries.addAll(ServicePlatform.get().getConditionsDeck().getCondition(getActiveInvestigatorId(), ConditionTrait.INJURY));
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(illnessesOrInjuries);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Illness or Injury Condition");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

    private void selectItemToDiscard() {
        List<? extends CardInfo> investigatorItems = findInvestigatorItems();
        if (!investigatorItems.isEmpty()) {
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(investigatorItems);
            selectCardData.setHideText("Display Inventory?");
            selectCardData.setTitleText("Select Item");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
        }
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }
}
