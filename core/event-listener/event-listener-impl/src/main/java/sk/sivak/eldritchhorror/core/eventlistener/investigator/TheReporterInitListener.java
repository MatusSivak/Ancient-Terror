package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardConditionFromInvestigatorData;
import sk.sivak.eldritchhorror.core.model.ConditionsDeckRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import java.util.ArrayList;
import java.util.List;

public class TheReporterInitListener extends AbstractInvestigatorInitListener {
    private final List<EventListener> listeners = new ArrayList<>();
    private boolean resolving;

    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_REPORTER; }

    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.LUCKY_TALISMAN);
        getService().gainConditionFromDeck(getInvestigatorId(), ConditionId.CURSED);
        registerStartingImprovement();
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override public void justRegisterListeners() {
        resolving = false;
        EventListener<DiscardConditionFromInvestigatorData> afterDiscard = new EventListenerImpl<DiscardConditionFromInvestigatorData>() {
            @Override public Class<DiscardConditionFromInvestigatorData> getDataClass() { return DiscardConditionFromInvestigatorData.class; }
            @Override public void onNotify(DiscardConditionFromInvestigatorData data) { onConditionDiscarded(data); }
        };
        listeners.add(afterDiscard);
        getEventQueue().addAfterEventListener(afterDiscard, BeforeAfterEvent.DISCARD_CONDITION_FROM_INVESTIGATOR);
    }

    @Override public void unregisterInvestigator() {
        for (EventListener listener : listeners) getEventQueue().unregisterListener(listener);
        listeners.clear();
        getEventQueue().unregisterListener(this);
    }

    private boolean shouldTrigger(DiscardConditionFromInvestigatorData data) {
        if (resolving || data.getInvestigatorId() != getInvestigatorId() || data.getConditionInfo() == null) return false;
        ConditionId discarded = data.getConditionInfo().getId();
        if (discarded != ConditionId.CURSED && discarded != ConditionId.BLESSED) return false;
        ConditionsDeckRead conditions = ServicePlatform.get().getConditionsDeck();
        if (conditions.hasCondition(getInvestigatorId(), ConditionId.CURSED)
                || conditions.hasCondition(getInvestigatorId(), ConditionId.BLESSED)
                || !conditions.canGetConditionId(getInvestigatorId(), ConditionId.CURSED)) return false;
        for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            if (investigator.getInfo().getInvestigatorId() == getInvestigatorId()) {
                return investigator.getCurrentHealth() > 0 && investigator.getCurrentSanity() > 0;
            }
        }
        return false;
    }

    private void onConditionDiscarded(DiscardConditionFromInvestigatorData data) {
        if (!shouldTrigger(data)) return;
        resolving = true;
        InvestigatorId previous = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        Question<Object> question = new Question<>();
        question.setTitle("Rex is no longer Cursed: gain 1 Clue, improve 1 skill and gain a Cursed Condition.");
        question.setPortraitBeforeTitle(getInvestigatorId());
        question.setOptions(Question.Option.okOption);
        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
            getService().hold();
            ServicePlatform.get().getTokenService().gainClueFromPool(getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
            getService().gainConditionFromDeck(getInvestigatorId(), ConditionId.CURSED);
            getService().addEventCommand(in -> { resolving = false; });
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(previous);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            getService().convertTo(DiscardConditionFromInvestigatorData.class, () -> data);
            getService().release();
        });
        getService().release();
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
                    Question<Object> question = new Question<>();
                    question.setTitle("The Reporter starts with one skill improved.");
                    question.setPortraitBeforeTitle(getInvestigatorId());
                    question.setOptions(Question.Option.okOption);
                    ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                        getService().hold();
                        ServicePlatform.get().getInvestigatorService().improveSkill(null);
                        getService().convertToNull();
                        getService().release();
                    });
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
}
