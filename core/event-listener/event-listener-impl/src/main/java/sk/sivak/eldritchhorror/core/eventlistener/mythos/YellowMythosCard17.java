package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;

import java.util.List;

/**
 * Driven to Madness: each investigator may discard any number of Ally Assets, then loses 1 Sanity
 * for each Ally Asset he has. Each investigator that lost Sanity gains a Madness Condition.
 */
public class YellowMythosCard17 extends AbstractDiscardAnyNumberMythosCard {

    @Override
    protected List<? extends CardInfo> getMatchingCards(InvestigatorId investigatorId) {
        return EncounterUtils.getPossession(investigatorId, AssetTrait.ALLY);
    }

    @Override
    protected boolean losesHealth() {
        return false;
    }

    @Override
    protected boolean gainsMadnessOnLoss() {
        return true;
    }
}
