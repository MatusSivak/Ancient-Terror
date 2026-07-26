package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainSpellFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.model.SpellsDeckWrite;

/**
 * @author msivak
 */
public class GainSpellFromDeckAction extends AbstractHookableAction<GainSpellFromDeckData, GainSpellFromDeckData> {

    private static final Logger logger = LogManager.getLogger(GainSpellFromDeckAction.class);

    public GainSpellFromDeckAction() {
        super(BeforeAfterEvent.GAIN_SPELL_FROM_DECK);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainSpellFromDeckData> ss) {
        SpellsDeckWrite spellsDeck = ServicePlatform.get().getSpellsDeck();
        if (!spellsDeck.hasSpell(input.getInvestigatorId(), input.getSpellId())) {
            SpellInfo spellInfo = spellsDeck.gainSpell(input.getSpellId(), input.getInvestigatorId());
            ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).register(spellInfo, input.getInvestigatorId());
            ss.onSuccess(input);
        } else {
            ss.onSuccess(input);
        }
    }
}
