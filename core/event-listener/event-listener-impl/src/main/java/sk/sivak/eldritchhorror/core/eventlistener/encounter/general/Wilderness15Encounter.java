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

public class Wilderness15Encounter extends AbstractGeneralEncounter {

    public Wilderness15Encounter() {
        super(15, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.LORE, -2, this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainArtifact(AssetTrait.ELIXIR);
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void onFail() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

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

    private List<? extends CardInfo> findInvestigatorTrinkets() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.TRINKET);
    }


}
