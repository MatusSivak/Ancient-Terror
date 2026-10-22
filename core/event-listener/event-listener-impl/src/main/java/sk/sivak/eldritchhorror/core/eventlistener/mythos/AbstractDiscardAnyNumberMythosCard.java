package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

/**
 * Shared logic for Mythos cards where each investigator may discard any number of matching cards
 * and then loses Health or Sanity for each matching card he still has.
 */
abstract class AbstractDiscardAnyNumberMythosCard implements MythosCardEventListener {

    protected abstract List<? extends CardInfo> getMatchingCards(InvestigatorId investigatorId);

    protected abstract boolean losesHealth();

    protected abstract boolean gainsMadnessOnLoss();

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            List<? extends CardInfo> cards = getMatchingCards(investigatorId);
            if (cards.isEmpty()) {
                continue;
            }
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            for (CardInfo card : cards) {
                ServicePlatform.get().getGameService().showCard(createRequest(card)).subscribe(response -> {
                    if (response == ShowCardResponse.YES) {
                        discard(investigatorId, card);
                    }
                });
            }
            ServicePlatform.get().getService().addEventCommand(in -> { applyPenalty(investigatorId); });
        }
        ServicePlatform.get().getService().release();
    }

    private void applyPenalty(InvestigatorId investigatorId) {
        int kept = getMatchingCards(investigatorId).size();
        if (kept == 0) {
            return;
        }
        ServicePlatform.get().getService().hold();
        if (losesHealth()) {
            ServicePlatform.get().getTokenService().loseHealth(kept);
        } else {
            ServicePlatform.get().getTokenService().loseSanity(kept);
        }
        if (gainsMadnessOnLoss()) {
            ServicePlatform.get().getGameService().gainCondition(ConditionTrait.MADNESS);
        }
        ServicePlatform.get().getService().release();
    }

    private ShowCardRequest createRequest(CardInfo card) {
        ShowCardRequest request = new ShowCardRequest();
        if (card instanceof AssetInfo) {
            request.setAssetInfo((AssetInfo) card);
        } else if (card instanceof ArtifactInfo) {
            request.setArtifactInfo((ArtifactInfo) card);
        } else if (card instanceof SpellInfo) {
            request.setSpellInfo((SpellInfo) card);
        }
        request.setTitle("Discard " + card.getName() + "?");
        request.setHideType(HideType.DISCARD_ON_YES);
        request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        return request;
    }

    private void discard(InvestigatorId investigatorId, CardInfo card) {
        if (card instanceof AssetInfo) {
            ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, (AssetInfo) card, false);
        } else if (card instanceof ArtifactInfo) {
            ServicePlatform.get().getService().discardArtifactFromInvestigator(investigatorId, (ArtifactInfo) card, false);
        } else if (card instanceof SpellInfo) {
            ServicePlatform.get().getService().discardSpellFromInvestigator(investigatorId, (SpellInfo) card);
        }
    }
}
