package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

/**
 * Shattered Magic: each investigator discards half (rounded up) of his Magical possessions.
 * Doom advances by 1 for each Artifact discarded this way.
 */
public class YellowMythosCard18 implements MythosCardEventListener {

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            int toDiscard = (getMagicalPossessions(investigatorId).size() + 1) / 2;
            if (toDiscard == 0) {
                continue;
            }
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            for (int i = 0; i < toDiscard; i++) {
                ServicePlatform.get().getService().addEventCommand(in -> { selectAndDiscard(investigatorId); });
            }
        }
        ServicePlatform.get().getService().release();
    }

    private void selectAndDiscard(InvestigatorId investigatorId) {
        List<? extends CardInfo> magicalPossessions = getMagicalPossessions(investigatorId);
        if (magicalPossessions.isEmpty()) {
            return;
        }
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(magicalPossessions);
        selectCardData.setTitleText("Select Magical possession to discard");
        selectCardData.setHideText("Display Magical possessions?");
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(cardInfo -> {
            ServicePlatform.get().getService().hold();
            if (cardInfo instanceof ArtifactInfo) {
                ServicePlatform.get().getService().discardArtifactFromInvestigator(investigatorId, (ArtifactInfo) cardInfo, true);
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            } else if (cardInfo instanceof AssetInfo) {
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, (AssetInfo) cardInfo, true);
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private List<? extends CardInfo> getMagicalPossessions(InvestigatorId investigatorId) {
        return EncounterUtils.getPossession(investigatorId, AssetTrait.MAGICAL);
    }
}
