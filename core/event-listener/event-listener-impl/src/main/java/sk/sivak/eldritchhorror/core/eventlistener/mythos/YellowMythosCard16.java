package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

import java.util.List;

/**
 * Treacherous Magic: each investigator may discard any number of Spells, then loses 1 Sanity
 * for each Spell he has. Each investigator that lost Sanity gains a Madness Condition.
 */
public class YellowMythosCard16 extends AbstractDiscardAnyNumberMythosCard {

    @Override
    protected List<? extends CardInfo> getMatchingCards(InvestigatorId investigatorId) {
        return ServicePlatform.get().getSpellsDeck().getSpells(investigatorId);
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
