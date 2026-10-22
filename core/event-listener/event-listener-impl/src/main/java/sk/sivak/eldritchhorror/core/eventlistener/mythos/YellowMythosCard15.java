package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;

import java.util.List;

/**
 * Burden of Greed: each investigator may discard any number of Item possessions,
 * then loses 1 Health for each Item possession he has.
 */
public class YellowMythosCard15 extends AbstractDiscardAnyNumberMythosCard {

    @Override
    protected List<? extends CardInfo> getMatchingCards(InvestigatorId investigatorId) {
        return EncounterUtils.getPossession(investigatorId, AssetTrait.ITEM);
    }

    @Override
    protected boolean losesHealth() {
        return true;
    }

    @Override
    protected boolean gainsMadnessOnLoss() {
        return false;
    }
}
