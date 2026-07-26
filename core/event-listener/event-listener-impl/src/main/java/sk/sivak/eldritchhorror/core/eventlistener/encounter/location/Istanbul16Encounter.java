package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Istanbul16Encounter extends AbstractLocationEncounter{

    public Istanbul16Encounter() {
        super(16, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.INFLUENCE),
                this::onFail)
                .execute();
    }

    private void onFail() {
        List<? extends CardInfo> investigatorItemsorTrinkets = findInvestigatorItemsOrTrinkets();
        if (investigatorItemsorTrinkets.isEmpty()) {
            return;
        }
        selectItemToDiscard(investigatorItemsorTrinkets);
    }

    private void selectItemToDiscard(List<? extends CardInfo> investigatorItems) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorItems);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Item or Trinket");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

    private List<? extends CardInfo> findInvestigatorItemsOrTrinkets() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM, AssetTrait.TRINKET);
    }

}
