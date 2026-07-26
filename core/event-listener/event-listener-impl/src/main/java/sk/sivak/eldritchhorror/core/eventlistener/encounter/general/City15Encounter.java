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

public class City15Encounter extends AbstractGeneralEncounter {

    public City15Encounter() {
        super(15, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.OBSERVATION, -1, this::onSuccess, this::onFail);
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
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

            List<? extends CardInfo> investigatorItems = findInvestigatorItems();
            if (!investigatorItems.isEmpty()) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(investigatorItems);
                selectCardData.setHideText("Display Inventory?");
                selectCardData.setTitleText("Select Item");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
            }

            List<? extends CardInfo> investigatorTrinkets = findInvestigatorTrinkets();
            if (!investigatorTrinkets.isEmpty()) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(investigatorTrinkets);
                selectCardData.setHideText("Display Inventory?");
                selectCardData.setTitleText("Select Trinket");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
            }

            ServicePlatform.get().getEncounterService().release();
        });
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }

    private List<? extends CardInfo> findInvestigatorTrinkets() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.TRINKET);
    }
}
