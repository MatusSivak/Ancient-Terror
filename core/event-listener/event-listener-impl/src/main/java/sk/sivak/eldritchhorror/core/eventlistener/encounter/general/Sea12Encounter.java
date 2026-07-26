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

public class Sea12Encounter extends AbstractGeneralEncounter {

    public Sea12Encounter() {
        super(12, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.WILL, null, this::onFail);
    }

    private void onFail() {
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
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

            ServicePlatform.get().getEncounterService().release();
        });
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }
}
