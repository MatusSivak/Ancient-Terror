package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Sea14Encounter extends AbstractGeneralEncounter {

    public Sea14Encounter() {
        super(14, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.LORE, this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().improveSkill(Stat.INFLUENCE);
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void onFail() {
        int possessionsAtRisk = findInvestigatorItemsOrTrinkets().size();
        String flavor;
        if (possessionsAtRisk >= 2) {
            flavor = getTextBuilder().withFail().withFlavor().build();
        } else if (possessionsAtRisk == 1) {
            flavor = getTextBuilder().withFail().withFlavor(2).build();
        } else {
            flavor = getTextBuilder().withFail().withFlavor(3).build();
        }
        ServicePlatform.get().getEncounterService().typeFlavor(flavor);
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().addEventCommand(xxx -> {
                discardItemOrTrinket();
            });
            ServicePlatform.get().getService().addEventCommand(xxx -> {
                discardItemOrTrinket();
            });
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void discardItemOrTrinket() {
        List<? extends CardInfo> investigatorItemsOrTrinkets = findInvestigatorItemsOrTrinkets();
        if (investigatorItemsOrTrinkets.isEmpty()) {
            return;
        }
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorItemsOrTrinkets);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Item or Trinket");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

    private List<? extends CardInfo> findInvestigatorItemsOrTrinkets() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM, AssetTrait.TRINKET);
    }
}
