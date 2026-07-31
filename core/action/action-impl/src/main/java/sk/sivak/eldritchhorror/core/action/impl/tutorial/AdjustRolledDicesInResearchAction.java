package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollUsingData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;

public class AdjustRolledDicesInResearchAction implements Action<Object, Object> {

    private AfterRollDicesListener afterRollDicesListener;
    private BeforeRerollUsingFocusListener rerollUsingFocusListener;

    public AdjustRolledDicesInResearchAction() {

    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            afterRollDicesListener = new AfterRollDicesListener();
            rerollUsingFocusListener = new BeforeRerollUsingFocusListener();
            ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollDicesListener, BeforeAfterEvent.ROLL_DICES);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(rerollUsingFocusListener, BeforeAfterEvent.REROLL_USING_FOCUS);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeDrawMythosCardListener(), DRAW_MYTHOS_CARD);
            onSub.onSuccess(null);
        });
    }

    private class BeforeDrawMythosCardListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(afterRollDicesListener);
                ServicePlatform.get().getEventQueue().unregisterListener(rerollUsingFocusListener);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class AfterRollDicesListener extends EventListenerImpl<TestData> {

        private int executedTimes = 0;

        @Override
        public void onNotify(TestData eventData) {
            Random rng = new Random();
            eventData.setMinScoreToEndTest(1);
            for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
            }
            executedTimes++;
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    @Override
    public void setInput(Object input) {

    }

    private class BeforeRerollUsingFocusListener extends EventListenerImpl<RerollUsingData> {

        @Override
        public void onNotify(RerollUsingData eventData) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTutorialService().displayChalkboard("tutorial.research.noSuccessUseFocus",
                    ViewProperties.VIEWPORT_WIDTH/2, ViewProperties.VIEWPORT_HEIGHT/2, true);
            ServicePlatform.get().getTutorialService().hideChalkboard();
            ServicePlatform.get().getTokenService().loseFocus();
            ServicePlatform.get().getTutorialService().waitFewMilliseconds(500);
            ServicePlatform.get().getService().addEventCommand(in -> {
                return Single.create(ss -> {
                    Random rng = new Random();
                    Collections.sort(eventData.getTestData().getDiceRolls(), (o1, o2) -> o1.getDiceValue() - o2.getDiceValue());
                    List<DiceRoll> diceRolls = eventData.getTestData().getDiceRolls();
                    DiceRoll diceRoll = diceRolls.get(0);
                    diceRoll.setDiceValue(rng.nextInt(2)+5);
                    diceRoll.setScore(DiceRoll.Score.GOOD);
                    Completable completable = ServicePlatform.get().getTestController().rerollDice(Collections.singletonList(diceRoll));
                    completable.subscribe(() -> ss.onSuccess(eventData.getTestData()));
                });
            });
            ServicePlatform.get().getService().convertTo(RerollUsingData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<RerollUsingData> getDataClass() {
            return RerollUsingData.class;
        }
    }
}
