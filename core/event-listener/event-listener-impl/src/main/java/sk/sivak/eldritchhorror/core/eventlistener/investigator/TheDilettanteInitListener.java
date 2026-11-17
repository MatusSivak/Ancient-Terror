package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainConditionFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import java.util.ArrayList;
import java.util.List;

public class TheDilettanteInitListener extends AbstractInvestigatorInitListener {
    private final List<EventListener> listeners = new ArrayList<>();

    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_DILETTANTE; }

    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.DOT_25_AUTOMATIC);
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.FINE_JEWELRY);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override public void justRegisterListeners() {
        EventListener<Void> refresh = new EventListenerImpl<Void>() {
            @Override public Class<Void> getDataClass() { return Void.class; }
            @Override public void onNotify(Void data) {
                ServicePlatform.get().getInvestigators().getInvestigator(getInvestigatorId()).setPassiveAbilityDisabled(false);
            }
        };
        EventListener<GainedCardData> afterGainedCard = new EventListenerImpl<GainedCardData>() {
            @Override public Class<GainedCardData> getDataClass() { return GainedCardData.class; }
            @Override public void onNotify(GainedCardData data) {
                if (data.getCardType() != GainedCardData.CardType.CONDITION || !(data.getCardToGain() instanceof ConditionInfo)
                        || ((ConditionInfo) data.getCardToGain()).getId() != ConditionId.DEBT) return;
                offerDiscard(ServicePlatform.get().getInvestigators().getActiveInvestigatorId(), GainedCardData.class, data);
            }
        };
        EventListener<GainConditionFromDeckData> afterGainFromDeck = new EventListenerImpl<GainConditionFromDeckData>() {
            @Override public Class<GainConditionFromDeckData> getDataClass() { return GainConditionFromDeckData.class; }
            @Override public void onNotify(GainConditionFromDeckData data) {
                if (data.getConditionId() != ConditionId.DEBT) return;
                offerDiscard(data.getInvestigatorId(), GainConditionFromDeckData.class, data);
            }
        };
        listeners.add(refresh);
        listeners.add(afterGainedCard);
        listeners.add(afterGainFromDeck);
        getEventQueue().addDirectEventListener(refresh, DirectEvent.REENABLE_DISABLED_ABILITIES);
        getEventQueue().addAfterEventListener(afterGainedCard, BeforeAfterEvent.REGISTER_GAINED_CARD);
        getEventQueue().addAfterEventListener(afterGainFromDeck, BeforeAfterEvent.GAIN_CONDITION_FROM_DECK);
    }

    @Override public void unregisterInvestigator() {
        for (EventListener listener : listeners) getEventQueue().unregisterListener(listener);
        listeners.clear();
        getEventQueue().unregisterListener(this);
    }

    private boolean isJennyOnBoard() {
        for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            if (investigator.getInfo().getInvestigatorId() == getInvestigatorId()) return true;
        }
        return false;
    }

    private <T> void offerDiscard(InvestigatorId owner, Class<T> dataClass, T data) {
        InvestigatorRead jenny = ServicePlatform.get().getInvestigators().getInvestigator(getInvestigatorId());
        if (owner == null || jenny.isPassiveAbilityDisabled() || !isJennyOnBoard()) return;
        ConditionInfo debt = ServicePlatform.get().getConditionsDeck().getCondition(owner, ConditionId.DEBT);
        if (debt == null) return;
        String ownerName = ServicePlatform.get().getInvestigators().getInvestigator(owner).getInfo().getInvestigatorName();
        getService().hold();
        Question<Boolean> question = new Question<>();
        question.setTitle("Jenny Barnes: " + ownerName + " may discard the Debt Condition. (Once per round)");
        question.setPortraitBeforeTitle(getInvestigatorId());
        question.setOptions(Question.Option.noYesOptions);
        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
            getService().hold();
            if (answer.getResponseData()) {
                jenny.setPassiveAbilityDisabled(true);
                getService().discardConditionFromInvestigator(owner, debt);
            }
            getService().convertTo(dataClass, () -> data);
            getService().release();
        });
        getService().release();
    }
}
