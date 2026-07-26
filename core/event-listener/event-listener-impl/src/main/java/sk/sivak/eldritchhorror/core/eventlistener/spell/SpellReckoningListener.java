package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.condition.AbstractConditionListener;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;

public class SpellReckoningListener extends EventListenerImpl<Object> {

    private Supplier<AbstractSpellBackListener> findSpellBackListenerSupplier;
    private Supplier<InvestigatorId> cardOwnerSupplier;
    private boolean charged = false;

    public SpellReckoningListener(AbstractSpellListener abstractSpellListener) {
        findSpellBackListenerSupplier = abstractSpellListener::findSpellBackListener;
        cardOwnerSupplier = () -> abstractSpellListener.spellOwnerId;
    }

    @Override
    public void onNotify(Object eventData) {
        if (eventData == ReckoningFireType.CHARGE) {
            charged = true;
            return;
        }
        if (eventData == ReckoningFireType.FIRE && !charged) {
            return;
        }
        charged = false;
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (!ServicePlatform.get().getInvestigators().isOnBoard(cardOwnerSupplier.get())) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(cardOwnerSupplier.get());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);

            AbstractSpellBackListener spellBackListener = findSpellBackListenerSupplier.get();
            spellBackListener.executeWhole();

            ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
            ServicePlatform.get().getService().release();
        });
    }

    @Override
    public Class<Object> getDataClass() {
        return Object.class;
    }
}
