package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import java.util.ArrayList;
import java.util.List;

public class TheExplorerInitListener extends AbstractInvestigatorInitListener {
    private final List<EventListener> listeners = new ArrayList<>();

    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_EXPLORER; }

    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.MINERALOGY_RESEARCH);
        registerStartingImprovement();
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
        EventListener<SpendData> spend = new EventListenerImpl<SpendData>() {
            @Override public Class<SpendData> getDataClass() { return SpendData.class; }
            @Override public void onNotify(SpendData data) { discountFocus(data); }
        };
        listeners.add(refresh);
        listeners.add(spend);
        getEventQueue().addDirectEventListener(refresh, DirectEvent.REENABLE_DISABLED_ABILITIES);
        getEventQueue().addBeforeEventListener(spend, BeforeAfterEvent.SPEND);
    }

    @Override public void unregisterInvestigator() {
        for (EventListener listener : listeners) getEventQueue().unregisterListener(listener);
        listeners.clear();
        getEventQueue().unregisterListener(this);
    }
    private void registerStartingImprovement() {
        EventListener<Void> setup = new EventListenerImpl<Void>() {
            @Override public Class<Void> getDataClass() { return Void.class; }
            @Override public void onNotify(Void data) {
                getEventQueue().unregisterListener(this);
                getService().addEventCommand(in -> {
                    InvestigatorId previous = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                    getService().hold();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getInvestigatorId());
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(previous);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    getService().convertToNull();
                    getService().release();
                });
            }
        };
        listeners.add(setup);
        getEventQueue().addAfterEventListener(setup, BeforeAfterEvent.SHOW_PHASE);
    }

    private void discountFocus(SpendData data) {
        InvestigatorRead owner = ServicePlatform.get().getInvestigators().getInvestigator(getInvestigatorId());
        Integer focus = data.getTokenAmountMap().get(TokenType.FOCUS);
        if (owner.isPassiveAbilityDisabled() || focus == null || focus < 1
                || owner.getCurrentLocationId() != ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId()) return;
        if (data.isSilent()) {
            data.getTokenAmountMap().put(TokenType.FOCUS, focus - 1);
            return;
        }
        Question<Boolean> question = new Question<>();
        question.setTitle("Use Ursula's once-per-round ability to spend 1 less Focus?");
        question.setPortraitBeforeTitle(getInvestigatorId());
        question.setOptions(Question.Option.noYesOptions);
        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
            if (answer.getResponseData()) {
                data.getTokenAmountMap().put(TokenType.FOCUS, focus - 1);
                data.addPostPayAction(() -> owner.setPassiveAbilityDisabled(true));
                data.addPostRevertAction(() -> owner.setPassiveAbilityDisabled(false));
            }
            getService().convertTo(SpendData.class, () -> data);
        });
    }
}

