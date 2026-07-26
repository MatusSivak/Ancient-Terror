package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollUsingData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;

public class AdjustRolledDicesInMysteryEncounterAction implements Action<Object, Object> {

    private AfterRollDicesListener afterRollDicesListener;
    private BeforeRerollUsingCluesListener rerollUsingCluesListener;
    private BeforeRerollDieListener beforeRerollDieListener;

    public AdjustRolledDicesInMysteryEncounterAction() {

    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            afterRollDicesListener = new AfterRollDicesListener();
            rerollUsingCluesListener = new BeforeRerollUsingCluesListener();
            beforeRerollDieListener = new BeforeRerollDieListener();
            ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollDicesListener, BeforeAfterEvent.ROLL_DICES);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(rerollUsingCluesListener, BeforeAfterEvent.REROLL_USING_CLUE);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRerollDieListener, BeforeAfterEvent.REROLL_DIE);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeDrawMythosCardListener(), DRAW_MYTHOS_CARD);
            onSub.onSuccess(null);
        });
    }

    private class BeforeDrawMythosCardListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(afterRollDicesListener);
                ServicePlatform.get().getEventQueue().unregisterListener(rerollUsingCluesListener);
                ServicePlatform.get().getEventQueue().unregisterListener(beforeRerollDieListener);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class BeforeRerollDieListener extends EventListenerImpl<CountRerollDiceData> {

        @Override
        public void onNotify(CountRerollDiceData eventData) {
            Random rng = new Random();
            eventData.addRerolledValue(rng.nextInt(4)+1);
            eventData.addRerolledValue(rng.nextInt(2)+5);
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTutorialService().displayWindowOnInfoStage(0,0,ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT, false);
            ServicePlatform.get().getService().convertTo(CountRerollDiceData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CountRerollDiceData> getDataClass() {
            return CountRerollDiceData.class;
        }
    }

    private class AfterRollDicesListener extends EventListenerImpl<TestData> {


        @Override
        public void onNotify(TestData eventData) {
            Random rng = new Random();
            eventData.setMinScoreToEndTest(1);
            for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
            }
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    @Override
    public void setInput(Object input) {

    }

    private class BeforeRerollUsingCluesListener extends EventListenerImpl<RerollUsingData> {

        @Override
        public void onNotify(RerollUsingData eventData) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTutorialService().displayBlockerOnInfoStage();
            ServicePlatform.get().getTutorialService().displayChalkboard("That was very unsuccessful roll!",
                    ViewProperties.VIEWPORT_WIDTH/2, ViewProperties.VIEWPORT_HEIGHT/2, true);
            ServicePlatform.get().getTutorialService().displayChalkboard("Check your investigator one more time, to discover your passive ability.",
                    ViewProperties.VIEWPORT_WIDTH/2, ViewProperties.VIEWPORT_HEIGHT/2, false);

            ServicePlatform.get().getTutorialService().displayWindowOnInfoStage(5,60, 65,65,true);
            ServicePlatform.get().getTutorialService().waitForInvestigatorClick();
            ServicePlatform.get().getTutorialService().displayBlockerOnMapStage();
            ServicePlatform.get().getTutorialService().displayBlockerOnInfoStage();
            ServicePlatform.get().getTutorialService().hideChalkboard();
            ServicePlatform.get().getTutorialService().waitFewMilliseconds(2000);
            ServicePlatform.get().getTutorialService().highlightSpySpecial();
            ServicePlatform.get().getTutorialService().waitForInvestigatorHide();
            ServicePlatform.get().getTutorialService().displayBlockerOnInfoStage();
            ServicePlatform.get().getTutorialService().displayChalkboard("You must sacrifice one Clue. Then you will be able to reroll two Dice.",
                    ViewProperties.VIEWPORT_WIDTH/2, ViewProperties.VIEWPORT_HEIGHT/2, true);
            ServicePlatform.get().getTutorialService().hideChalkboard();
            ServicePlatform.get().getTutorialService().displayWindowOnInfoStage(480,63, 115,50,false);
            /*
            ServicePlatform.get().getService().addEventCommand(in -> {
                return Single.create(ss -> {
                    Random rng = new Random();
                    Collections.sort(eventData.getTestData().getDiceRolls(), (o1, o2) -> o1.getDiceValue() - o2.getDiceValue());
                    List<DiceRoll> diceRolls = eventData.getTestData().getDiceRolls();
                    DiceRoll diceRoll1 = diceRolls.get(0);
                    diceRoll1.setDiceValue(rng.nextInt(2)+5);
                    diceRoll1.setScore(DiceRoll.Score.GOOD);
                    DiceRoll diceRoll2 = diceRolls.get(1);
                    diceRoll2.setDiceValue(rng.nextInt(4)+1);
                    diceRoll2.setScore(DiceRoll.Score.BAD);
                    Completable completable = ServicePlatform.get().getTestController().rerollDice(
                            Arrays.asList(diceRoll1, diceRoll2));
                    completable.subscribe(() -> ss.onSuccess(eventData.getTestData()));
                });
            });
            */
            ServicePlatform.get().getService().convertTo(RerollUsingData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<RerollUsingData> getDataClass() {
            return RerollUsingData.class;
        }
    }
}
